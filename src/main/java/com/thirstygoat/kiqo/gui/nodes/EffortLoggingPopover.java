package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.gui.effort.EffortViewModel;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Created by samschofield on 22/09/15.
 */
public class EffortLoggingPopover extends PopOver {
    private EffortViewModel viewModel;

    private TextField personSelector;
    private DatePicker endDatePicker;
    private TextField timeTextField;
    private TextField hourSpinner;
    private TextField minuteSpinner;
    private TextArea commentTextArea;
    private Button logButton;
    private Task task;

    public EffortLoggingPopover(EffortViewModel viewModel, Task task) {
        super();
        this.viewModel = viewModel;
        initContent();
        attachViewModel();
        populateFields();
        attachValidators();
        this.task = task;
    }

    private void attachViewModel() {
        FxUtils.setTextFieldSuggester(personSelector, viewModel.eligibleAssignees());
        personSelector.textProperty().bindBidirectional(viewModel.personProperty(),
                        StringConverters.personStringConverter(viewModel.organisationProperty()));

        endDatePicker.setValue(LocalDate.now());
        endDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (task.getStory().getSprint() != null) {
                    if (item.isAfter(task.getStory().getSprint().getEndDate()) || item.isBefore(task.getStory().getSprint().getStartDate())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }

            }
        });

        viewModel.endDateProperty().bindBidirectional(endDatePicker.valueProperty());

        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LocalTime time;
            try {
                time = LocalTime.parse(newValue, Utilities.TIME_FORMATTER);
                viewModel.endTimeProperty().setValue(time);
            } catch (DateTimeParseException e) {
            }
        });
        timeTextField.setText(LocalTime.now().format(Utilities.TIME_FORMATTER));

        viewModel.durationProperty().set(Duration.ofHours(0));
        hourSpinner.textProperty().addListener(((observable, oldValue, newValue) -> {
            viewModel.durationProperty().set(calculateDuration());
        }));

        minuteSpinner.textProperty().addListener(((observable, oldValue, newValue) -> {
            viewModel.durationProperty().set(calculateDuration());
        }));

        minuteSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 59, minuteSpinner));
        hourSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 99, hourSpinner));

        viewModel.commentProperty().bindBidirectional(commentTextArea.textProperty());

        logButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
        logButton.setOnAction(e -> {
            if (viewModel.allValidation().isValid()) {
                viewModel.commitEdit();
                hide();
                hide(javafx.util.Duration.millis(0));
            }
        });

        focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue) {
                hide(javafx.util.Duration.millis(0));
            }
        }));
    }

    private void populateFields() {
        if (viewModel.effortObjectProperty().get() != null) {
            personSelector.setText(viewModel.effortObjectProperty().get().personProperty().getValue().getShortName());
            endDatePicker.setValue(viewModel.effortObjectProperty().get().endDateTimeProperty().getValue().toLocalDate());
            timeTextField.setText(Utilities.TIME_FORMATTER.format(viewModel.effortObjectProperty().get().endDateTimeProperty().getValue().toLocalTime()));
            hourSpinner.setText(Long.toString(viewModel.effortObjectProperty().get().durationProperty().get().toHours()));
            minuteSpinner.setText(Long.toString(viewModel.effortObjectProperty().get().durationProperty().get().toMinutes() % 60));
            commentTextArea.setText(viewModel.effortObjectProperty().get().commentProperty().getValue());
        }
    }

    private void attachValidators() {
        ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
        validationVisualizer.initVisualization(viewModel.personValidation(), personSelector, true);
        validationVisualizer.initVisualization(viewModel.endDateValidation(), endDatePicker, true);
        validationVisualizer.initVisualization(viewModel.endTimeValidation(), timeTextField, true);
        validationVisualizer.initVisualization(viewModel.commentValidation(), commentTextArea, true);
        validationVisualizer.initVisualization(viewModel.durationValidation(), hourSpinner, true);
    }

    /**
     * Inits all the gui elements for the popover, sets their sizes and populates the popover with the content
     */
    private void initContent() {
        setAutoHide(true);
        setDetachable(false);

        /* Main content */
        VBox content = new VBox();
        content.setFillWidth(true);
        content.setMaxWidth(320);
        content.setMaxHeight(320);
        content.setPadding(new Insets(5, 10, 10, 10));
        content.setSpacing(5);

        /* Heading */
        Label heading = new Label();
        heading.setText("Log effort");
        heading.getStyleClass().add("heading-label");

        /* Person */
        VBox personVbox = new VBox();
        Label personLabel = new Label();
        personLabel.setText("Person");
        personSelector = new TextField();
        personSelector.setPromptText("Select a person");
        personVbox.getChildren().addAll(personLabel, personSelector);

        /* Date + Time */
        HBox dateTimeHbox = new HBox();
        dateTimeHbox.setSpacing(5);

        VBox dateVbox = new VBox();
        HBox.setHgrow(dateVbox, Priority.SOMETIMES);
        Label dateLabel = new Label();
        dateLabel.setText("Date");
        endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now());
        dateVbox.getChildren().addAll(dateLabel, endDatePicker);

        VBox timeVbox = new VBox();
        HBox.setHgrow(timeVbox, Priority.SOMETIMES);
        Label timeLabel = new Label();
        timeLabel.setText("Time");
        timeTextField = new TextField();
        timeTextField.setText(Utilities.TIME_FORMATTER.format(LocalTime.now()));
        timeVbox.getChildren().addAll(timeLabel, timeTextField);

        VBox durationVbox = new VBox();
        HBox durationHbox = new HBox();
        durationHbox.setSpacing(5);
        Label durationLabel = new Label();
        durationLabel.setText("Duration");
        durationHbox.setPrefWidth(150);

        hourSpinner = new TextField();
        hourSpinner.setPromptText("H");
        hourSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 99, hourSpinner));

        minuteSpinner = new TextField();
        minuteSpinner.setPromptText("M");
        minuteSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 59, minuteSpinner));

        durationHbox.getChildren().addAll(hourSpinner, minuteSpinner);
        durationVbox.getChildren().addAll(durationLabel, durationHbox);
        dateTimeHbox.getChildren().addAll(dateVbox, timeVbox, durationVbox);

        /* Comment */
        commentTextArea = new TextArea();
        commentTextArea.setPromptText("Add a comment");
        commentTextArea.setWrapText(true);

        HBox buttonHbox = new HBox();
        buttonHbox.setAlignment(Pos.BASELINE_RIGHT);
        logButton = new Button();
        logButton.getStyleClass().add(".form .form-button");
        logButton.setText("Log");
        buttonHbox.getChildren().add(logButton);

        content.getChildren().setAll(heading, personVbox, dateTimeHbox, commentTextArea, buttonHbox);
        setContentNode(content);
    }

    /**
     * Calculates the total number of minutes from a combination of the hours and minutes text fields
     */
    private Duration calculateDuration() {
        int hours;
        try {
            hours = Integer.parseInt(hourSpinner.getText());
        } catch (Exception e) {
            hours = 0;
        }
        int minutes;
        try {
            minutes = Integer.parseInt(minuteSpinner.getText());
        } catch (Exception e) {
            minutes = 0;
        }

        minutes = minutes + (hours * 60);
        return Duration.ofMinutes(minutes);
    }

}



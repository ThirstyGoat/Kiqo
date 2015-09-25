package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.gui.DelayedValidationVisualizer;
import com.thirstygoat.kiqo.gui.effort.EffortViewModel;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
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
        this.task = task;
        initContent();
        attachViewModel();
        populateFields();
        Platform.runLater(() -> attachValidators());
    }

    private void attachViewModel() {
        FxUtils.setTextFieldSuggester(personSelector, viewModel.eligibleAssignees());
        personSelector.textProperty().bindBidirectional(viewModel.personProperty(),
                        StringConverters.personStringConverter(viewModel.organisationProperty()));

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
        DelayedValidationVisualizer validationVisualizer = new DelayedValidationVisualizer(viewModel.dirtyProperty());
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
        VBox main = new VBox();
        main.setPadding(new Insets(10, 10, 10, 10));

        /* Heading */
        Label heading = new Label("Log effort");
        heading.getStyleClass().add("heading-label");

        /* Person */
        Label personLabel = new Label("Person");
        personSelector = new TextField();
        personSelector.setPromptText("Select a person");
        VBox personVbox = new VBox();
        personVbox.getChildren().addAll(personLabel, personSelector);

        HBox dateTimeHbox = new HBox();

        VBox dateVbox = new VBox();
        Label dateLabel = new Label("Date");
        endDatePicker = new DatePicker();
        endDatePicker.setValue((LocalDate.now().isBefore(task.getStory().getSprint().getEndDate().plusDays(1))) ? LocalDate.now() : null);
        dateVbox.getChildren().addAll(dateLabel, endDatePicker);

        VBox timeVbox = new VBox();
        Label timeLabel = new Label("Time");
        timeTextField = new TextField();
        timeTextField.setText(Utilities.TIME_FORMATTER.format(LocalTime.now()));
        timeVbox.getChildren().addAll(timeLabel, timeTextField);

        VBox durationVbox = new VBox();

        HBox durationHbox = new HBox();
        durationHbox.setSpacing(5);
        Label durationLabel = new Label("Duration");
        hourSpinner = new TextField();
        hourSpinner.setPromptText("H");
        minuteSpinner = new TextField();
        minuteSpinner.setPromptText("M");
        durationHbox.setPrefWidth(150);

        hourSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 99, hourSpinner));
        minuteSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 59, minuteSpinner));

        durationHbox.getChildren().addAll(hourSpinner, minuteSpinner);
        durationVbox.getChildren().addAll(durationLabel, durationHbox);
        dateTimeHbox.getChildren().addAll(dateVbox, timeVbox, durationVbox);

        HBox.setHgrow(timeVbox, Priority.SOMETIMES);
        HBox.setHgrow(dateVbox, Priority.SOMETIMES);

        /* Comment */
        commentTextArea = new TextArea();
        commentTextArea.setPromptText("Add a comment...");
        commentTextArea.setWrapText(true);
        commentTextArea.setPrefHeight(80);

        HBox buttonHbox = new HBox();
        buttonHbox.setAlignment(Pos.BASELINE_RIGHT);
        logButton = new Button("Log");
        logButton.getStyleClass().add(".form .form-button");
        buttonHbox.getChildren().add(logButton);

        main.getChildren().setAll(heading, personVbox, dateTimeHbox, commentTextArea, buttonHbox);
        setContentNode(main);

        dateTimeHbox.setSpacing(3);
        main.setSpacing(10);
        main.setPrefWidth(320);

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



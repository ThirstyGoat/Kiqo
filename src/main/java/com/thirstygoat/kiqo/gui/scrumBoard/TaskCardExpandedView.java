package com.thirstygoat.kiqo.gui.scrumBoard;

import com.thirstygoat.kiqo.gui.customCells.EffortListCell;
import com.thirstygoat.kiqo.gui.customCells.ImpedimentListCell;
import com.thirstygoat.kiqo.gui.effort.EffortViewModel;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelFilteredListSelectionView;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Impediment;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;


/**
 * Created by james on 19/08/15.
 */
public class TaskCardExpandedView implements FxmlView<TaskCardViewModel>, Initializable {

    @InjectViewModel
    TaskCardViewModel viewModel;
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private Label teamLabel;
    @FXML
    private GoatLabelFilteredListSelectionView<Person> assignedPeopleLabel;
    @FXML
    private GoatLabelTextField estimatedHoursLabel;
//    @FXML
//    private TextField personTextField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private TextField commentTextField;
    @FXML
    private VBox detailsVBox;
    @FXML
    private VBox loggingVBox;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Button loggingButton;
    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private ToggleButton loggingToggleButton;
    @FXML
    private HBox buttonsHBox;
    @FXML
    private CheckBox blockedCheckBox;
    @FXML
    private Button addImpedimentButton;
    @FXML
    private Button removeImpedimentButton;
    @FXML
    private TextField impedimentTextField;
    @FXML
    private ListView<Impediment> impedimentsListView;
    @FXML
    private ListView<Effort> loggedEffortListView;
    @FXML
    private TableColumn<Effort, Person> personTableColumn;
    @FXML
    private TableColumn<Effort, String> commentTableColumn;
    @FXML
    private TableColumn<Effort, Float> durationTableColumn;
    @FXML
    private TableColumn<Effort, LocalDateTime> endTimeTableColumn;
    @FXML
    private VBox editView;
    @FXML
    private VBox buttonsView;
    @FXML
    private Button newEffortButton;
    @FXML
    private Button deleteEffortIcon;
    @FXML
    private Button editEffortButton;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSegmentedButton();
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        FxUtils.initGoatLabel(estimatedHoursLabel, viewModel, viewModel.estimateProperty(), viewModel.estimateValidation());  //TODO fix the parsing error when "-" is typed into the box
        initBlockedCheckBox();
        initImpediments();
        initEffortLogging();



        //TODO add the assigned people to form after creating a new GoatLabel for filtered selection thingy
        FxUtils.initGoatLabel(assignedPeopleLabel, viewModel, viewModel.assignedPeolpe(), viewModel.eligableAssignedPeople());
//        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.getTask().get().getAssignedPeople(), viewModel.teamValidation());
    }


    private void initEffortLogging() {
        EffortViewModel effortViewModel = new EffortViewModel();
        effortViewModel.taskProperty().bind(viewModel.getTask());
        effortViewModel.organisationProperty().bind(viewModel.organisationProperty());

        newEffortButton.setOnAction(event -> {
            EffortViewModel e = new EffortViewModel();
            e.taskProperty().bind(viewModel.getTask());
            e.organisationProperty().bind(viewModel.organisationProperty());
            PopOver p = createLogEffortPopOver(null, e);
            Platform.runLater(() -> p.show(buttonsView));
        });

        loggedEffortListView.setItems(viewModel.loggedEffort());
        loggedEffortListView.setCellFactory((lv) -> new EffortListCell(effortViewModel));

    }


    private void initImpediments() {
        impedimentsListView.setCellFactory(param -> new ImpedimentListCell());
        impedimentsListView.setItems(viewModel.impedimentsObservableList());
        impedimentTextField.textProperty().bindBidirectional(viewModel.textFieldString());
        impedimentTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                viewModel.addImpediment();
            }
        });

        addImpedimentButton.setOnAction(e -> viewModel.addImpediment());
        addImpedimentButton.disableProperty().bind(Bindings.isEmpty(impedimentTextField.textProperty()));
        removeImpedimentButton.setOnAction(e -> {
            int place = impedimentsListView.getSelectionModel().getSelectedIndex();
            viewModel.removeImpediment(impedimentsListView.getSelectionModel().getSelectedItem());
            impedimentsListView.getSelectionModel().select((place > 0) ? place - 1 : 0);

        });
        removeImpedimentButton.disableProperty().bind(
                Bindings.isNull(impedimentsListView.getSelectionModel().selectedItemProperty()));
    }

    private void initBlockedCheckBox() {
        blockedCheckBox.selectedProperty().bindBidirectional(viewModel.blockedProperty());
        blockedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.commitEdit();
        });
    }

    private void initSegmentedButton() {
        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else {
                if (newValue == detailsToggleButton) {
                    showNode(detailsVBox);
                } else if (newValue == loggingToggleButton) {
                    showNode(loggingVBox);
                }
            }
        });
    }


    private void showNode(Node node) {
        for (Node node1 : mainAnchorPane.getChildren()) {
            node1.setManaged(false);
            node1.setVisible(false);
        }
        node.setManaged(true);
        node.setVisible(true);
    }

    /**
     * Creates a pop over populated with all the fields needed for logging effort
     */
    public static PopOver createLogEffortPopOver(Effort effort, EffortViewModel viewModel) {
        PopOver popOver = new PopOver();
        popOver.setAutoHide(true);
        popOver.setDetachable(false);

        /* Main content */
        VBox content = new VBox();
        content.setFillWidth(true);
        content.setMaxWidth(300);
        content.setMaxHeight(300);
        content.setPadding(new Insets(5, 5, 5, 5));
        content.setSpacing(5);

        /* Heading */
        Label heading = new Label();
        heading.setText("Log effort");
        heading.getStyleClass().add("heading-label");

        /* Person */
        VBox personVbox = new VBox();
        Label personLabel = new Label();
        personLabel.setText("Person");
        TextField personSelector = new TextField();
        personSelector.setPromptText("Select a person");
        personVbox.getChildren().addAll(personLabel, personSelector);

        /* Date + Time */
        HBox dateTimeHbox = new HBox();
        dateTimeHbox.setSpacing(5);

        VBox dateVbox = new VBox();
        Label dateLabel = new Label();
        dateLabel.setText("Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now());
        dateVbox.getChildren().addAll(dateLabel, endDatePicker);

        VBox timeVbox = new VBox();
        Label timeLabel = new Label();
        timeLabel.setText("Time");
        TextField timeTextField = new TextField();
        timeTextField.setText(Utilities.TIME_FORMATTER.format(LocalTime.now()));
        timeVbox.getChildren().addAll(timeLabel, timeTextField);

        VBox durationVbox = new VBox();
        HBox durationHbox = new HBox();
        durationHbox.setSpacing(5);
        Label durationLabel = new Label();
        durationLabel.setText("Duration");

        TextField hourSpinner = new TextField();
        hourSpinner.setPromptText("H");
        hourSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 99, hourSpinner));
        HBox.setHgrow(hourSpinner, Priority.ALWAYS);
        hourSpinner.setPrefWidth(80);

        TextField minuteSpinner = new TextField();
        HBox.setHgrow(minuteSpinner, Priority.ALWAYS);
        minuteSpinner.setPromptText("M");
        minuteSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 59, minuteSpinner));
        minuteSpinner.setPrefWidth(80);

        durationHbox.getChildren().addAll(hourSpinner, minuteSpinner);
        durationVbox.getChildren().addAll(durationLabel, durationHbox);

        dateTimeHbox.getChildren().addAll(dateVbox, timeVbox, durationVbox);

        /* Comment */
        TextArea commentTextArea = new TextArea();
        commentTextArea.setPromptText("Add a comment");
        commentTextArea.setWrapText(true);

        HBox buttonHbox = new HBox();
        buttonHbox.setAlignment(Pos.BASELINE_RIGHT);
        Button logButton = new Button();
        logButton.getStyleClass().add(".form .form-button");
        logButton.setText("Log");
        buttonHbox.getChildren().add(logButton);

        content.getChildren().setAll(heading, personVbox, dateTimeHbox, commentTextArea, buttonHbox);
        popOver.setContentNode(content);



        if (effort != null) {
            personLabel.setText(effort.personProperty().getValue().getShortName());
            endDatePicker.setValue(effort.endDateTimeProperty().getValue().toLocalDate());
            timeTextField.setText(Utilities.TIME_FORMATTER.format(effort.endDateTimeProperty().getValue().toLocalTime()));
            hourSpinner.setText(Long.toString(effort.durationProperty().get().toHours()));
            minuteSpinner.setText(Long.toString(effort.durationProperty().get().toMinutes() % 60));
            commentTextArea.setText(effort.commentProperty().getValue());
        }

        viewModel.organisationProperty().addListener((observable, oldValue, newValue) -> {
            FxUtils.setTextFieldSuggester(personSelector, viewModel.eligablePeopleSupplier.get());
        });
        FxUtils.setTextFieldSuggester(personSelector, viewModel.eligablePeopleSupplier.get());

        personSelector.textProperty().bindBidirectional(
                viewModel.personProperty(),
                StringConverters.personStringConverter(viewModel.organisationProperty())
        );
        endDatePicker.setValue(LocalDate.now());
        viewModel.endDateProperty().bind(endDatePicker.valueProperty());

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
            viewModel.durationProperty().set(Duration.ofMinutes(10));
        }));

        minuteSpinner.textProperty().addListener(((observable, oldValue, newValue) -> {
            viewModel.durationProperty().set(Duration.ofMinutes(200));
        }));

        hourSpinner.setPrefWidth(60);
        minuteSpinner.setPrefWidth(60);
        minuteSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 59, minuteSpinner));
        hourSpinner.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 99, hourSpinner));

        commentTextArea.textProperty().bindBidirectional(viewModel.commentProperty());

        logButton.setOnAction(e -> {
            if (viewModel.allValidation().isValid()) {
                viewModel.commitEdit();
                popOver.hide();
            }
        });

        return popOver;
    }

    private Duration calculateDuration() {
        int minutes = hourSpinner.getValue() * 60 + minuteSpinner.getValue();
        return Duration.ofMinutes(minutes);
    }



//    <VBox fx:id="editView">
//    <HBox spacing="5.0">
//    <TextField fx:id="personTextField" HBox.hgrow="ALWAYS" promptText="Select a person"/>
//    </HBox>
//    <HBox spacing="5" alignment="BASELINE_CENTER">
//    <DatePicker fx:id="endDatePicker" />
//    <TextField fx:id="endTimeTextField" prefWidth="130" />
//    <Label text="H"/>
//    <Spinner fx:id="hourSpinner" min="0" max="99" initialValue="0" HBox.hgrow="ALWAYS">
//    <editable>true</editable>
//    </Spinner>
//    <Label text="M"/>
//    <Spinner fx:id="minuteSpinner" min="0" max="59" initialValue="0" HBox.hgrow="ALWAYS">
//    <editable>true</editable>
//    </Spinner>
//    </HBox>
//    <HBox spacing="5.0">
//    <children>
//    <TextField fx:id="commentTextField" HBox.hgrow="ALWAYS" promptText="Write a comment..."/>
//    <Button fx:id="loggingButton" mnemonicParsing="false" text="Log" />
//    </children>
//    </HBox>
//    </VBox>


}

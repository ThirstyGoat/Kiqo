package com.thirstygoat.kiqo.gui.scrumBoard;

import com.thirstygoat.kiqo.gui.customCells.ImpedimentListCell;
import com.thirstygoat.kiqo.gui.effort.EffortViewModel;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelFilteredListSelectionView;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Impediment;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.time.LocalDateTime;
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
    @FXML
    private TextField personTextField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField durationTextField;
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
    private TableView<Effort> loggedEffortTableView;
    @FXML
    private TableColumn<Allocation, Person> personTableColumn;
    @FXML
    private TableColumn<Allocation, String> commentTableColumn;
    @FXML
    private TableColumn<Allocation, Float> durationTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDateTime> endTimeTableColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        FxUtils.initGoatLabel(estimatedHoursLabel, viewModel, viewModel.estimateProperty(), viewModel.estimateValidation());  //TODO fix the parsing error when "-" is typed into the box
        blockedCheckBox.selectedProperty().bindBidirectional(viewModel.blockedProperty());
        blockedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.commitEdit();
        });

        //TODO add the assigned people to form after creating a new GoatLabel for filtered selection thingy
//        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.getTask().get().getAssignedPeople(), viewModel.teamValidation());


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

        EffortViewModel effortViewModel = new EffortViewModel();
//        effortViewModel.load(new Effort(), viewModel.organisationProperty().get());
//        effortViewModel.organisationProperty().bind(viewModel.organisationProperty());

        loggingButton.setOnAction(e -> {
            System.out.println(effortViewModel.allValidation().isValid());
            System.out.println(effortViewModel.allValidation().getErrorMessages());
            effortViewModel.commitEdit();
        });

        effortViewModel.taskProperty().set(viewModel.getTask().get());
        personTextField.textProperty().bindBidirectional(effortViewModel.personProperty(),
                        StringConverters.personStringConverter(effortViewModel.organisationProperty()));
        FxUtils.setTextFieldSuggester(personTextField, effortViewModel.eligablePeopleSupplier.get());
        endDatePicker.valueProperty().bindBidirectional(effortViewModel.endDateProperty());
        endTimeTextField.textProperty().bindBidirectional(effortViewModel.endTimeProperty(),
                        new LocalTimeStringConverter());
        durationTextField.textProperty().bindBidirectional(effortViewModel.durationProperty(),
                        new NumberStringConverter());

        loggedEffortTableView.itemsProperty().bind(viewModel.loggedEffort());

        FxUtils.initGoatLabel(assignedPeopleLabel, viewModel, viewModel.assignedPeolpe(),
                        viewModel.eligableAssignedPeople());
    }



    private void showNode(Node node) {
        for (Node node1 : mainAnchorPane.getChildren()) {
            node1.setManaged(false);
            node1.setVisible(false);
        }
        node.setManaged(true);
        node.setVisible(true);
    }


}

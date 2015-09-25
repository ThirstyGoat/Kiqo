package com.thirstygoat.kiqo.gui.story;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationSupport;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * Created by Carina Blair on 15/05/2015.
 */
public class StoryFormView implements FxmlView<StoryFormViewModel>, Initializable {

    private FormButtonHandler formButtonHandler;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private boolean valid = false;
    private Command command;
    // Begin FXML Injections
    @FXML
    private Label heading;
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField creatorTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private TextField priorityTextField;
    @FXML
    private ComboBox<Scale> estimationScaleComboBox;
    //TODO omitted for syntactic correctness (backlog can't be set in this form anyway, so stories are tricky to find)
//    @FXML 
//    private GoatFilteredListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Hyperlink storyCycleHyperLink;

    @InjectViewModel
    private StoryFormViewModel viewModel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        creatorTextField.textProperty().bindBidirectional(viewModel.creatorProperty(),
                StringConverters.personStringConverter(viewModel.organisationProperty()));
        projectTextField.textProperty().bindBidirectional(viewModel.projectProperty(),
                StringConverters.projectStringConverter(viewModel.organisationProperty()));
        priorityTextField.textProperty().bindBidirectional(viewModel.priorityProperty(),
                new NumberStringConverter());
        FxUtils.restrictToNumericInput(Story.MIN_PRIORITY, Story.MAX_PRIORITY, priorityTextField.textProperty());

        estimationScaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        estimationScaleComboBox.getSelectionModel().selectFirst();

//        storySelectionView.setHeader(new Label("Depends on:")); TODO
//        storySelectionView.bindSelectedItems(viewModel.dependenciesProperty());
//        storySelectionView.bindAllItems(viewModel.eligibleDependencies());
//        storySelectionView.setStringPropertyCallback(Story::shortNameProperty);

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());

        Platform.runLater(() -> {
            setPrompts();
            setValidationSupport();
            longNameTextField.requestFocus();
            creatorTextField.disableProperty().bind(viewModel.getCreatorEditable().not());
        });
        
        Utilities.initShortNameSuggester(longNameTextField.textProperty(), shortNameTextField.textProperty());
        FxUtils.setTextFieldSuggester(creatorTextField, viewModel.creatorSupplier());
        FxUtils.setTextFieldSuggester(projectTextField, viewModel.projectSupplier());

        Platform.runLater(() -> {
            setPrompts();
            setValidationSupport();
            longNameTextField.requestFocus();
            creatorTextField.disableProperty().bind(viewModel.getCreatorEditable().not());
        });
//        setStoryCycleHyperLinkInfo(); TODO
//        storySelectionView.disableProperty().bind(Bindings.isNull(viewModel.backlogProperty())); TODO
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under " + Utilities.SHORT_NAME_MAX_LENGTH + " characters and unique");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this story");
        priorityTextField.setPromptText("Set a priority between " + Story.MIN_PRIORITY + " and " + Story.MAX_PRIORITY);

        // Populate Estimation Scale ComboBox
        estimationScaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        estimationScaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default
    }

    private void setValidationSupport() {
        DelayedValidationVisualizer validationVisualizer = new DelayedValidationVisualizer(viewModel.dirtyProperty());
        validationVisualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
        validationVisualizer.initVisualization(viewModel.longNameValidation(), longNameTextField, true);
        validationVisualizer.initVisualization(viewModel.creatorValidation(), creatorTextField, true);
        validationVisualizer.initVisualization(viewModel.projectValidation(), projectTextField, true);
        validationVisualizer.initVisualization(viewModel.priorityValidation(), priorityTextField, true);
        validationVisualizer.initVisualization(viewModel.scaleValidation(), estimationScaleComboBox);
    }

//    private void setStoryCycleHyperLinkInfo() { TODO
//        Label label = new Label();
//        label.setText("Only the stories that will not create a dependency loop are shown");
//        label.setPadding(new Insets(10, 10, 10, 10));
//        PopOver readyWhyPopOver = new PopOver(label);
//        readyWhyPopOver.setDetachable(false);
//
//        storyCycleHyperLink.setOnAction((e) -> {
//            readyWhyPopOver.show(storyCycleHyperLink);
//        });
//        storyCycleHyperLink.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                readyWhyPopOver.hide(Duration.millis(0));
//            }
//        });
//    }

    public void setExitStrategy(Runnable exitStrategy) {
        formButtonHandler = new FormButtonHandler(viewModel::getCommand, exitStrategy);
    }

    public void okAction() {
        if (formButtonHandler != null) {
            formButtonHandler.okAction();
        }
    }

    public void cancelAction() {
        if (formButtonHandler != null) {
            formButtonHandler.cancelAction();
        }
    }

    public StringProperty headingProperty() {
        return heading.textProperty();
    }

    public void setOkButtonText(String string) {
        okButton.setText(string);
    }
}

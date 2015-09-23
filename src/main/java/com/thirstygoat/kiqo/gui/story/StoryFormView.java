package com.thirstygoat.kiqo.gui.story;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.FormButtonHandler;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.validation.ValidationSupport;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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
    @FXML
    private GoatFilteredListSelectionView<Story> storySelectionView;
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

        estimationScaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        estimationScaleComboBox.getSelectionModel().selectFirst();

//        storySelectionView.setHeader(new Label("Depends on:"));
//        storySelectionView.targetItemsProperty().bindBidirectional(viewModel.dependenciesProperty());
//        storySelectionView.sourceItemsProperty().bind(viewModel.eligibleDependencies());

//        storySelectionView.setStringPropertyCallback(story -> story.shortNameProperty());

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
        
        Utilities.initShortNameSuggester(longNameTextField.textProperty(), shortNameTextField.textProperty());
        FxUtils.setTextFieldSuggester(creatorTextField, viewModel.creatorSupplier());
        FxUtils.setTextFieldSuggester(projectTextField, viewModel.projectSupplier());

        Platform.runLater(() -> {
            setPrompts();
            setValidationSupport();
            longNameTextField.requestFocus();
        });
//        setStoryCycleHyperLinkInfo();
//        storySelectionView.disableProperty().bind(Bindings.isNull(viewModel.backlogProperty()));
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under " + Utilities.SHORT_NAME_MAX_LENGTH + " characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this story.");

        // Populate Estimation Scale ComboBox
        estimationScaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        estimationScaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default
    }

    private void setValidationSupport() {
        ValidationVisualizer visualizer = new ControlsFxVisualizer();
        visualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
        visualizer.initVisualization(viewModel.longNameValidation(), longNameTextField, true);
        visualizer.initVisualization(viewModel.creatorValidation(), creatorTextField, true);
        visualizer.initVisualization(viewModel.projectValidation(), projectTextField, true);
        visualizer.initVisualization(viewModel.priorityValidation(), priorityTextField, true);
        visualizer.initVisualization(viewModel.scaleValidation(), estimationScaleComboBox);
    }
//
//    private void setStoryCycleHyperLinkInfo() {
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

    private void setupStoriesList() {
        storySelectionView.setHeader(new Label("Depends on:"));
    }

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

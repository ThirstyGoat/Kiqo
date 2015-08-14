package com.thirstygoat.kiqo.gui.formControllers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;
import org.controlsfx.validation.ValidationSupport;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.gui.viewModel.StoryViewModel;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;

/**
 * Created by Carina Blair on 15/05/2015.
 */
public class StoryFormView implements FxmlView<StoryFormViewModel>, Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private boolean valid = false;
    private Command command;
    // Begin FXML Injections
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
        setShortNameHandler();
        setPrompts();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        priorityTextField.setText(Integer.toString(Story.DEFAULT_PRIORITY));
        Platform.runLater(longNameTextField::requestFocus);
        setStoryCycleHyperLinkInfo();
        storySelectionView.disableProperty().bind(Bindings.isNull(viewModel.backlogProperty()));
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
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

    private void setStoryCycleHyperLinkInfo() {
        Label label = new Label();
        label.setText("Only the stories that will not create a dependency loop are shown");
        label.setPadding(new Insets(10, 10, 10, 10));
        PopOver readyWhyPopOver = new PopOver(label);
        readyWhyPopOver.setDetachable(false);

        storyCycleHyperLink.setOnAction((e) -> {
            readyWhyPopOver.show(storyCycleHyperLink);
        });
        storyCycleHyperLink.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                readyWhyPopOver.hide(Duration.millis(0));
            }
        });
    }

    private void setupStoriesList() {
        storySelectionView.setHeader(new Label("Depends on:"));
    }

    public void okAction() { viewModel.okAction(); }

    public void cancelAction() { viewModel.cancelAction();}


    /**
     * Sets the listener on the nameTextField so that the shortNameTextField is populated in real time
     * up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Auto populate short name text field
            if (!Objects.equals(newValue, longNameTextField.getText().substring(0,
                    Math.min(longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified.set(true);
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
    }
}

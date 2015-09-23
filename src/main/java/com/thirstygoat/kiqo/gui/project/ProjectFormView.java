package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.gui.FormButtonHandler;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class ProjectFormView implements FxmlView<ProjectFormViewModel>, Initializable {

    private FormButtonHandler formButtonHandler;

    @InjectViewModel
    ProjectFormViewModel viewModel;
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
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
        Utilities.initShortNameSuggester(longNameTextField.textProperty(), shortNameTextField.textProperty());
        
        Platform.runLater(() -> {
            setPrompts();
            attachValidators();
            longNameTextField.requestFocus();
        });
    }

    private void attachValidators() {
        ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
        validationVisualizer.initVisualization(viewModel.longNameValidation(), longNameTextField, true);
        validationVisualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
        validationVisualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextField);
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under " + Utilities.SHORT_NAME_MAX_LENGTH + " characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this project.");
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

    public StringProperty headingTextProperty() {
        return heading.textProperty();
    }
}

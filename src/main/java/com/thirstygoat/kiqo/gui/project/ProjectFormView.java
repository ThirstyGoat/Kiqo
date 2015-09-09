package com.thirstygoat.kiqo.gui.project;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class ProjectFormView implements FxmlView<ProjectFormViewModel>, Initializable {

    // Begin FXML Injections
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

    @InjectViewModel
    ProjectFormViewModel viewModel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());

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
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this project.");
    }

    public void okAction() {
        viewModel.okAction();
    }

    public void cancelAction() {
        viewModel.cancelAction();
    }
}
package com.thirstygoat.kiqo.gui.skill;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.gui.FormButtonHandler;

import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.validation.visualization.*;

/**
 * Connects the GUI form to the viewModel. NB: Requires that {@link #setExitStrategy(Runnable)} is called.
 * @author amy
 * 26/8/15
 */
public class SkillFormView implements FxmlView<SkillViewModel>, Initializable {
    private FormButtonHandler formButtonHandler;
    
    @InjectViewModel
    SkillViewModel viewModel;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {        
        bindToViewModel();

        attachValidators();
        
        Platform.runLater(() -> {
            nameTextField.requestFocus();
        });
    }
    
    private void bindToViewModel() {
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
    }

    private void attachValidators() {
        Platform.runLater(() -> { // wait for textfields to exist
            ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
            validationVisualizer.initVisualization(viewModel.nameValidation(), nameTextField, true);
            validationVisualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextField, false);
        });
    }
    
    public void setExitStrategy(Runnable exitStrategy) {
        formButtonHandler = new FormButtonHandler(() -> viewModel.createCommand(), exitStrategy);
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
}

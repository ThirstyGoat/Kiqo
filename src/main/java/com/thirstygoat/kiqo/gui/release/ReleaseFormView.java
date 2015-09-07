package com.thirstygoat.kiqo.gui.release;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.gui.FormButtonHandler;
import com.thirstygoat.kiqo.util.StringConverters;

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
public class ReleaseFormView implements FxmlView<ReleaseViewModel>, Initializable {
    private FormButtonHandler formButtonHandler;
    
    @InjectViewModel
    ReleaseViewModel viewModel;
    
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private DatePicker releaseDatePicker;
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
            shortNameTextField.requestFocus();
        });
    }
    
    private void bindToViewModel() {
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        projectTextField.textProperty().bindBidirectional(viewModel.projectNameProperty());
        releaseDatePicker.valueProperty().bindBidirectional(viewModel.dateProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
    }

    private void attachValidators() {
        Platform.runLater(() -> { // wait for textfields to exist
            ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
            validationVisualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
            validationVisualizer.initVisualization(viewModel.projectValidation(), projectTextField, true);
            validationVisualizer.initVisualization(viewModel.dateValidation(), releaseDatePicker, true);
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

package com.thirstygoat.kiqo.gui.skill;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.gui.FormButtonHandler;
import com.thirstygoat.kiqo.util.Utilities;

import com.thirstygoat.kiqo.util.FxUtils;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    @InjectViewModel
    SkillViewModel viewModel;
    private FormButtonHandler formButtonHandler;
    @FXML
    private Label heading;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {        
        bindToViewModel();
        
        attachValidators();
        Utilities.initShortNameLengthLimiter(nameTextField.textProperty());
        
        Platform.runLater(nameTextField::requestFocus);

        FxUtils.enableShiftEnter(descriptionTextArea, okButton::fire);
    }
    
    private void bindToViewModel() {
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty());
        descriptionTextArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
    }

    private void attachValidators() {
        viewModel.onFirstChanged(() -> {
        	ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
            validationVisualizer.initVisualization(viewModel.nameValidation(), nameTextField, true);
            validationVisualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextArea, false);
        });
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

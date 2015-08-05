package com.thirstygoat.kiqo.gui.sprint;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.gui.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintFormView implements FxmlView<SprintFormViewModel>, Initializable, IFormView {
    @InjectViewModel
    private SprintFormViewModel viewModel;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField goalTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField releaseTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField teamTextField;
    @FXML
    private TextField backlogTextField;
    @FXML
    private GoatListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        nameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        goalTextField.textProperty().bindBidirectional(viewModel.goalProperty());
        startDatePicker.valueProperty().bindBidirectional(viewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
        releaseTextField.textProperty().bindBidirectional(viewModel.releaseNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        teamTextField.textProperty().bindBidirectional(viewModel.teamNameProperty());
        backlogTextField.textProperty().bindBidirectional(viewModel.backlogNameProperty());
        
        attachValidators();
        okButton.disableProperty().bind(viewModel.validProperty().not());
    }
    
    @Override
    public void setExitStrategy(Runnable exitStrategy) {
        // disabled if invalid so any clicks must be good to close.
        okButton.setOnAction(event -> exitStrategy.run());
        cancelButton.setOnAction(event -> exitStrategy.run());
    }

    private void attachValidators() {
        ValidationVisualizer visualizer = new ControlsFxVisualizer();
        visualizer.initVisualization(viewModel.longNameValidation(), nameTextField, true);
        visualizer.initVisualization(viewModel.goalValidation(), goalTextField, true);
        visualizer.initVisualization(viewModel.startDateValidation(), startDatePicker, true);
        visualizer.initVisualization(viewModel.endDateValidation(), endDatePicker, true);
        visualizer.initVisualization(viewModel.releaseValidation(), releaseTextField, true);
        visualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextField, true);
        visualizer.initVisualization(viewModel.teamValidation(), teamTextField, true);
        visualizer.initVisualization(viewModel.backlogValidation(), backlogTextField, true);
    }
}


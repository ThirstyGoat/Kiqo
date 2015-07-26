package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.viewModel.MainController;
import com.thirstygoat.kiqo.viewModel.TaskFormViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Amy on 23/04/15.
 */
public class TaskFormController extends FormController<Task> {
    private Stage stage;
    private boolean valid;
    private Command<?> command;
    private Organisation organisation;
    private TaskFormViewModel viewModel;
    private Story story;
    private Task task;
    private MainController mainController;
    private final ValidationSupport validationSupport = new ValidationSupport();

    // Begin FXML Injections
    @FXML
    private TextField name;
    @FXML
    private TextField estimate;
    @FXML
    private TextArea description;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindFields();
        Platform.runLater(name::requestFocus);
    }

    private void bindFields() {
        viewModel = new TaskFormViewModel();
        name.textProperty().bindBidirectional(viewModel.shortNameProperty());
        estimate.textProperty().bindBidirectional(viewModel.estimateProperty());
        description.textProperty().bindBidirectional(viewModel.descriptionProperty());
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });
        cancelButton.setOnAction(event -> cancel());

        // Need to catch ENTER key presses to remove focus from textarea so that form can be submitted
        // Shift+Enter should create new line in the text area

        description.setOnKeyPressed(event -> {
            final KeyCombination shiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
            final KeyCombination enter = new KeyCodeCombination(KeyCode.ENTER);
            if (shiftEnter.match(event)) {
                // force new line
                description.appendText("\n");
                event.consume();
            } else if (enter.match(event)) {
                event.consume();
                okButton.fire();
            }
        });
    }

    private void cancel() {
        valid = false;
        stage.close();
    }

    private void setCommand() {
        viewModel.setCommand();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command<?> getCommand() {
        return viewModel.getCommand();
    }

    @Override
    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    @Override
    public void populateFields(Task task) throws RuntimeException {
        this.task = task;

        if (task == null) {
            // We are creating a new allocation (for an existing project)
            stage.setTitle("Create Task");
            okButton.setText("Create Task");
        } else {
            // edit an existing allocation
            viewModel.setTask(task);
            stage.setTitle("Edit Task");
            okButton.setText("Save");
//            description.setText(task.getShortName());
        }
    }
    private void setValidationSupport() {
        ValidationVisualizer visualizer = new ControlsFxVisualizer();
        visualizer.initVisualization(viewModel.nameValidation(), name, true);
        visualizer.initVisualization(viewModel.estimationValidation(), estimate, true);
        visualizer.initVisualization(viewModel.descriptionValidation(), description, true);
    }

    public void setStory(Story story) {
        viewModel.setStory(story);

        setValidationSupport();
        setButtonHandlers();
        okButton.disableProperty().bind(viewModel.formValidation().validProperty().not());
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private boolean validate() {
        if (validationSupport.isInvalid()) {
            return false;
        } else {
            valid = true;
        }
        setCommand();
        return true;
    }
}
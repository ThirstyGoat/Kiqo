package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Amy on 23/04/15.
 */
public class TaskFormController extends FormController<Task> {
    private Stage stage;
    private boolean valid;
    private Command<?> command;
    private Organisation organisation;
    private Story story;
    private Task task;
    private MainController mainController;

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
        setButtonHandlers();
        Platform.runLater(name::requestFocus);
        okButton.setDisable(true);
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            valid = description.getText().trim().length() > 0;
            if (valid) {
                setCommand();
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
        if (task == null) {
            task = new Task(description.getText().trim(), "", (float) 0.0);
            command = new CreateTaskCommand(task, story);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!task.getShortName().equals(description.getText())) {
                changes.add(new EditCommand<>(task, "criteria", description.getText()));
            }
            command = new CompoundCommand("Edit AC", changes);
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command<?> getCommand() {
        return command;
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
            stage.setTitle("Create Acceptance Criteria");
            okButton.setText("Create Acceptance Criteria");
        } else {
            // edit an existing allocation
            stage.setTitle("Edit Acceptance Criteria");
            okButton.setText("Save");
            description.setText(task.getShortName());
        }
    }

    public void setStory(Story story) {
        this.story = story;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
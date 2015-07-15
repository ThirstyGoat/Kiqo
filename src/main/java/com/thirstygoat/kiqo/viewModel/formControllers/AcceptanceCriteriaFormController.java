package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Amy on 23/04/15.
 */
public class AcceptanceCriteriaFormController extends FormController<AcceptanceCriteria> {
    private Stage stage;
    private boolean valid;
    private Command<?> command;
    private Organisation organisation;
    private Story story;
    private AcceptanceCriteria acceptanceCriteria;
    private MainController mainController;

    // Begin FXML Injections
    @FXML
    private TextArea acTextArea;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        Platform.runLater(acTextArea::requestFocus);
        okButton.setDisable(true);
        acTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            valid = newValue.trim().length() > 0;
            okButton.setDisable(!valid);
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            valid = acTextArea.getText().trim().length() > 0;
            if (valid) {
                setCommand();
                stage.close();
            }
        });
        cancelButton.setOnAction(event -> cancel());

        // Need to catch ENTER key presses to remove focus from textarea so that form can be submitted
        // Shift+Enter should create new line in the text area

        acTextArea.setOnKeyPressed(event -> {
            final KeyCombination shiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
            final KeyCombination enter = new KeyCodeCombination(KeyCode.ENTER);
            if (shiftEnter.match(event)) {
                // force new line
                acTextArea.appendText("\n");
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
        if (acceptanceCriteria == null) {
            acceptanceCriteria = new AcceptanceCriteria(acTextArea.getText().trim());
            command = new CreateAcceptanceCriteriaCommand(acceptanceCriteria, story);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!acceptanceCriteria.getShortName().equals(acTextArea.getText())) {
                changes.add(new EditCommand<>(acceptanceCriteria, "criteria", acTextArea.getText()));
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
    public void populateFields(AcceptanceCriteria acceptanceCriteria) throws RuntimeException {
        this.acceptanceCriteria = acceptanceCriteria;
        if (acceptanceCriteria == null) {
            // We are creating a new allocation (for an existing project)
            stage.setTitle("Create Acceptance Criteria");
            okButton.setText("Create Acceptance Criteria");
        } else {
            // edit an existing allocation
            stage.setTitle("Edit Acceptance Criteria");
            okButton.setText("Save");
            acTextArea.setText(acceptanceCriteria.criteria.get());
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
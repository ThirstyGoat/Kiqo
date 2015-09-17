package com.thirstygoat.kiqo.gui.formControllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.util.FxUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreateAcceptanceCriteriaCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by Amy on 23/04/15.
 */
public class AcceptanceCriteriaFormController extends FormController<AcceptanceCriteria> {
    private Stage stage;
    private boolean valid;
    private Command command;
    private Organisation organisation;
    private Story story;
    private AcceptanceCriteria acceptanceCriteria;
    private MainController mainController;

    // Begin FXML Injections
    @FXML
    private Label heading;
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
        okButton.disableProperty().bind(Bindings.length(acTextArea.textProperty()).isEqualTo(0));
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            setCommand();
            stage.close();
        });
        cancelButton.setOnAction(event -> cancel());

        FxUtils.enableShiftEnter(acTextArea, okButton::fire);
    }

    private void cancel() {
        valid = false;
        stage.close();
    }

    private void setCommand() {
        if (acceptanceCriteria == null) {
            acceptanceCriteria = new AcceptanceCriteria(acTextArea.getText().trim(), story);
            command = new CreateAcceptanceCriteriaCommand(acceptanceCriteria, story);
            valid = true;
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            if (!acceptanceCriteria.getShortName().equals(acTextArea.getText())) {
                changes.add(new EditCommand<>(acceptanceCriteria, "criteria", acTextArea.getText()));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit AC", changes);
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command getCommand() {
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
            okButton.setText("Done");
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

    @Override
    public StringProperty headingProperty() {
        return heading.textProperty();
    }
}
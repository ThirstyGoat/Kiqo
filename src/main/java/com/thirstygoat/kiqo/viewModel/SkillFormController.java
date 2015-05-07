package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreateSkillCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by james on 20/03/15.
 */
public class SkillFormController implements Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public PopOver errorPopOver = new PopOver();
    private Skill skill;
    private String shortName;
    private String description;
    private boolean valid = false;
    private Stage stage;
    private Command command;
    private boolean shortNameModified = false;

    private Organisation organisation;


    // FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private SkillFormController formController;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setErrorPopOvers();
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Platform.runLater(shortNameTextField::requestFocus);
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        descriptionTextField.setPromptText("Describe this skill.");
    }

    /**
     * Sets the TextFields displayed in the dialog to the Skill that will be edited.
     * @param skill the Skill that is loaded
     */
    public void loadSkill(final Skill skill) {
        this.skill = skill;

        if (skill == null) {
            // Then we are creating a new one
            stage.setTitle("Create Skill");
            okButton.setText("Create Skill");
        } else {
            // We are editing an existing skill
            stage.setTitle("Edit Skill");
            okButton.setText("Save");

            shortNameTextField.setText(skill.getShortName());
            descriptionTextField.setText(skill.getDescription());

        }
    }
    /**
     * Sets the listener on the shortName field so that the shortName is populated in real time
     * up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Auto populate short name text field
            if (!Objects.equals(newValue, shortNameTextField.getText().substring(0,
                    Math.min(shortNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified = true;
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
                errorPopOver.setContentNode(new Label("Short name must be under " + SHORT_NAME_MAX_LENGTH +
                        " characters"));
                errorPopOver.show(shortNameTextField);
            }
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                errorPopOver.hide(Duration.millis(0));
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }


    private void setCommand() {
        if (skill == null) {
            Skill s = new Skill(shortNameTextField.getText(), descriptionTextField.getText());
            command = new CreateSkillCommand(s, organisation);
        } else {
            final ArrayList<Command<?>> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(skill.getShortName())) {
                changes.add(new EditCommand<>(skill, "shortName", shortNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(skill.getDescription())) {
                changes.add(new EditCommand<>(skill, "description", descriptionTextField.getText()));
            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Skill", changes);

           // stage.close();
        }
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     */
    public boolean validate() {
        if(shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }

        if (skill != null) {
            if (shortNameTextField.getText().equals(skill.getShortName())) {
                valid = true;
                setCommand();
                return true;
            }
        }

        // shortname must be unique
        for (final Skill s : organisation.getSkills()) {
            if (shortNameTextField.getText().equals(s.getShortName())) {
                errorPopOver.setContentNode(new Label("Short name must be unique"));
                errorPopOver.show(shortNameTextField);
                return false;
            }
        }

        valid = true;
        setCommand();
        return true;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValid() {
        return valid;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Command<?> getCommand() { return command; }

    /**
     * Sets focus listeners on text fields so PopOvers are hidden upon focus
     */
    private void setErrorPopOvers() {
        // Set PopOvers as not detachable so we don't have floating PopOvers
        errorPopOver.setDetachable(false);

        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
            } else {
                errorPopOver.hide();
            }
        });
    }


    /**
     * Warms if the short name of a person is not unique
     */
    public void warnShortnameNotUnique() {
        errorPopOver.setContentNode(new Label("Short name must be unique"));
        errorPopOver.show(shortNameTextField);
    }
}

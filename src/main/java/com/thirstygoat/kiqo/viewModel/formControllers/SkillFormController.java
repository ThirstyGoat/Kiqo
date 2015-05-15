package com.thirstygoat.kiqo.viewModel.formControllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreateSkillCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.Utilities;

/**
 * Created by james on 20/03/15.
 */
public class SkillFormController implements Initializable, IFormController<Skill> {
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Skill skill;
    private String shortName;
    private String description;
    private boolean valid = false;
    private Stage stage;
    private Command<?> command;
    private Organisation organisation;

    // Begin FXML Injections
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
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Platform.runLater(shortNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                Utilities.shortnameIsUnique(shortNameTextField.getText(), skill, organisation.getSkills());

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty."));

        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Then invalid, disable ok button
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        descriptionTextField.setPromptText("Describe this skill.");
    }

    /**
     * Sets the TextFields displayed in the dialog to the Skill that will be edited.
     * @param skill the Skill that is loaded
     */
    @Override
    public void populateFields(final Skill skill) {
        this.skill = skill;

        if (skill == null) {
            // Then we are creating a new one
            stage.setTitle("Create Skill");
            okButton.setText("Done");
        } else {
            // We are editing an existing skill
            stage.setTitle("Edit Skill");
            okButton.setText("Done");

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
            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });
        cancelButton.setOnAction(event -> stage.close());
    }


    private void setCommand() {
        if (skill == null) {
            final Skill s = new Skill(shortNameTextField.getText(), descriptionTextField.getText());
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
        }
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     * @return all fields are valid
     */
    private boolean validate() {
        if (validationSupport.isInvalid()) {
            return false;
        } else {
            valid = true;
        }
        setCommand();
        return true;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public Command<?> getCommand() { return command; }

}

package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreateProjectCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class ProjectFormController implements Initializable, IFormController<Project> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public String longName;
    public String shortName;
    public String description;
    private Project project;
    private Command<?> command;
    private boolean shortNameModified = false;
    private boolean valid = false;
    private Stage stage;
    private Organisation organisation;
    private final ValidationSupport validationSupport = new ValidationSupport();

    // Begin FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        setShortNameSuggester();
        Platform.runLater(ProjectFormController.this.longNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                Utilities.shortnameIsUnique(shortNameTextField.getText(), project, organisation.getProjects());

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty."));

        validationSupport.registerValidator(longNameTextField,
                Validator.createEmptyValidator("Long name can not be empty", Severity.ERROR));

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
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this project.");

    }

    @Override
    public void populateFields(final Project project) {
        this.project = project;

        if (project == null) {
            // Then we are creating a new one
            stage.setTitle("Create Project");
            okButton.setText("Done");
        } else {
            // We are editing an existing project
            stage.setTitle("Edit Project");
            okButton.setText("Done");
            shortNameModified = true;

            longNameTextField.setText(project.getLongName());
            shortNameTextField.setText(project.getShortName());
            descriptionTextField.setText(project.getDescription());
        }
    }

    private void setCommand() {
        if (project == null) {
            final Project p = new Project(shortNameTextField.getText(),longNameTextField.getText(), descriptionTextField.getText());
            command = new CreateProjectCommand(p, organisation);
        } else {
            final ArrayList<Command<?>> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(project.getShortName())) {
                changes.add(new EditCommand<>(project, "shortName", shortNameTextField.getText()));
            }
            if (!longNameTextField.getText().equals(project.getLongName())) {
                changes.add(new EditCommand<>(project, "longName", longNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(project.getDescription())) {
                changes.add(new EditCommand<>(project, "description", descriptionTextField.getText()));
            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Project", changes);
        }
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            stage.close();
        });
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

    /**
     * Sets the listener on the nameTextField so that the shortNameTextField is
     * populated in real time up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // Set up short name suggester
                    if (!Objects.equals(newValue, longNameTextField.getText().substring(0, Math.min(
                            longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                        shortNameModified = true;
                    }

                    // Restrict length of short name text field
                    if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                        shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
                    }
                });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name
     * can be populated with a suggestion
     */
    public void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name
        // character by character up to specified characters
        longNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
            }
        });
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
    @Override
    public Command<?> getCommand() { return command; }

    @Override
    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
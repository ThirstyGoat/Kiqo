package com.thirstygoat.kiqo.viewModel;

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
 * Created by Bradley, James on 13/03/15.
 */
public class ProjectFormController implements Initializable {
    public final PopOver errorPopOver = new PopOver();
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public String longName;
    public String shortName;
    public String description;
    private Project project;
    private Command<?> command;
    // FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private Button openButton;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    private boolean shortNameModified = false;
    private boolean valid = false;
    private Stage stage;
    private Organisation organisation;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setShortNameHandler();
        setErrorPopOvers();
        setPrompts();
        setButtonHandlers();
        setShortNameSuggester();
        Platform.runLater(ProjectFormController.this.longNameTextField::requestFocus);
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Goats");
        descriptionTextField.setPromptText("Describe this project.");

    }

    public void loadProject(final Project project) {
        this.project = project;

        if (project == null) {
            // Then we are creating a new one
            stage.setTitle("Create Project");
            okButton.setText("Create Project");
        } else {
            // We are editing an existing project
            stage.setTitle("Edit Project");
            okButton.setText("Save");

            longNameTextField.setText(project.getLongName());
            shortNameTextField.setText(project.getShortName());
            descriptionTextField.setText(project.getDescription());
        }
    }

    /**
     * Sets focus listeners on text fields so PopOvers are hidden upon focus
     */
    private void setErrorPopOvers() {
        // Set PopOvers as not detachable so we don't have floating PopOvers
        errorPopOver.setDetachable(false);

        // Set handlers so that popovers are hidden on field focus
        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
            }
        });
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
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


    /**
     * Performs validation checks and displays error popovers where applicable
     */
    public boolean validate() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }
        else if (longNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Long name must not be empty"));
            errorPopOver.show(longNameTextField);
            return false;
        }

        if (project != null) {
            if (shortNameTextField.getText().equals(project.getShortName())) {
                valid = true;
                setCommand();
                return true;
            }
        }

        if (!Utilities.shortnameIsUnique(shortNameTextField.getText(), organisation.getProjects())) {
            errorPopOver.setContentNode(new Label("Short name must be unique"));
            errorPopOver.show(shortNameTextField);
            return false;
        }

        valid = true;
        setCommand();
        return true;
    }

    /**
     * Checks to make sure the short name is valid
     *
     * @return Whether or not the short name is valid
     */
    private boolean checkShortName() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }
        // TODO Check for uniqueness
        // if (!UNIQUE CHECKER) {
        // shortNamePopOver.setContentNode(new
        // Label("Short name must be unique"));
        // shortNamePopOver.show(shortNameTextField);
        // shortNameTextField.requestFocus();
        // }
        return true;
    }

    /**
     * Checks to make sure the long name is valid
     *
     * @return Whether or not the long name is valid
     */
    private boolean checkName() {
        if (longNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Name must not be empty"));
            errorPopOver.show(longNameTextField);
            return false;
        }
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

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
    public Command<?> getCommand() { return command; }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    public boolean isValid() {
        return valid;
    }
}

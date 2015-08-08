package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.create.CreateReleaseCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Created by james on 11/04/15.
 */
public class ReleaseFormController extends FormController<Release> {
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Organisation organisation;
    private Release release;
    private Command command;
    private boolean valid = false;
    private Stage stage;
    private Project project;

    // Begin FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        setShortNameLengthRestrictor();
        setPrompts();
        Platform.runLater(shortNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> {
            if (s.length() == 0) {
                return false;
            }
            if (project == null) {
                return true;
            }
            return Utilities.shortnameIsUnique(shortNameTextField.getText(), release, project.getReleases());
        };

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty."));

        final Predicate<String> projectValidation = s -> {
            for (final Project p : organisation.getProjects()) {
                if (p.getShortName().equals(projectTextField.getText())) {
                    project = p;
                    // Redo validation for shortname text field
                    final String snt = shortNameTextField.getText();
                    shortNameTextField.setText("");
                    shortNameTextField.setText(snt);
                    return true;
                }
            }
            return false;
        };

        validationSupport.registerValidator(projectTextField, Validator.createPredicateValidator(projectValidation,
                "Project must already exist"));

        validationSupport.registerValidator(releaseDatePicker,
                Validator.createEmptyValidator("Release date must be valid", Severity.ERROR));

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
        projectTextField.setPromptText("Project this release is associated with.");
        descriptionTextField.setPromptText("Describe this release.");
    }

    /**
     * Sets up a listener on the name field of team to restrict it to the predefined maximum length
     */
    private void setShortNameLengthRestrictor() {
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

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    public void setCommand() {
        if (release == null) {
            // new release command
            release = new Release(shortNameTextField.getText(), project, releaseDatePicker.getValue(),
                    descriptionTextField.getText());
            command = new CreateReleaseCommand(release);
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            if (!shortNameTextField.getText().equals(release.getShortName())) {
                changes.add(new EditCommand<>(release, "shortName", shortNameTextField.getText()));
            }
            if (!project.equals(release.getProject())) {
                changes.add(new MoveItemCommand<>(release, release.getProject().observableReleases(), project.observableReleases()));
                changes.add(new EditCommand<>(release, "project", project));
            }
            if (!releaseDatePicker.getValue().equals(release.getDate())) {
                changes.add(new EditCommand<>(release, "date", releaseDatePicker.getValue()));
                System.out.println(release + "," + releaseDatePicker.getValue());
            }
            if (!descriptionTextField.getText().equals(release.getDescription())) {
                changes.add(new EditCommand<>(release, "description", descriptionTextField.getText()));
            }




            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Release", changes);
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        setTextFieldSuggester(projectTextField, organisation.getProjects());
    }

    @Override
    public void populateFields(Release release) {
        this.release = release;
        okButton.setText("Done");

        if (release == null) {
            // create a release
            releaseDatePicker.setPromptText("dd/mm/yyyy");
        } else {
            // edit an existing release
            shortNameTextField.setText(release.getShortName());
            projectTextField.setText(release.getProject().getShortName());
            releaseDatePicker.setValue(release.getDate());
            descriptionTextField.setText(release.getDescription());
        }
    }
}
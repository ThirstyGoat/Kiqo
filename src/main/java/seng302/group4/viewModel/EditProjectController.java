package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import seng302.group4.Project;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.EditCommand;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Bradley on 13/03/15.
 */
public class EditProjectController implements Initializable {
    private Stage stage;
    private File projectLocation;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveChangesButton;
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField projectLocationTextField;
    @FXML
    private Button openButton;
    @FXML
    private TextField descriptionTextField;

    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private boolean shortNameModified = false;

    private Project project;

    private PopOver errorPopOver = new PopOver();

    public boolean valid = false;
    public CompoundCommand command;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCancelButton();
        setSaveButton();
        setOpenButton();

        setErrorPopOvers();
    }

    /**
     * Populates the fields with project data to enable editing
     * @param project
     */
    public void loadProject(Project project) {
        this.project = project;
        longNameTextField.setText(project.getLongName());
        shortNameTextField.setText(project.getShortName());
        projectLocationTextField.setText(project.getSaveLocation().getAbsolutePath());
        descriptionTextField.setText(project.getDescription());

        projectLocation = project.getSaveLocation();
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
        projectLocationTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
            }
        });
    }

    /**
     * Sets the event handler for the New Project Button, performs validation checks and instantiates the new project
     * if applicable
     */
    private void setSaveButton() {
        saveChangesButton.setOnAction(event -> {

            // Hide existing error message if there is one
            errorPopOver.hide();

            // Perform validity checks and create project
            if (checkName() && checkShortName() && checkSaveLocation()) {
                valid = checkChanged();

                EditCommand<Project, String> longNameChange = new EditCommand<>(
                        project, "longName", longNameTextField.getText()
                );

                EditCommand<Project, String> shortNameChange = new EditCommand<>(
                        project, "shortName", shortNameTextField.getText()
                );

                EditCommand<Project, File> saveLocationChange = new EditCommand<>(
                        project, "saveLocation", projectLocation
                );

                EditCommand<Project, String> descriptionChange = new EditCommand<>(
                        project, "description", descriptionTextField.getText()
                );

                ArrayList<EditCommand> changes = new ArrayList<>();
                changes.add(longNameChange);
                changes.add(shortNameChange);
                changes.add(saveLocationChange);
                changes.add(descriptionChange);

                command = new CompoundCommand(changes);

                // Close the new project dialog (this window)
                stage.close();
            }
        });
    }

    /**
     * Returns a boolean whether or not the project's details changed.
     * @return Whether or not the project's details have been changed
     */
    private boolean checkChanged() {
        return (!longNameTextField.getText().equals(project.getLongName()) ||
                !shortNameTextField.getText().equals(project.getShortName()) ||
                projectLocation != project.getSaveLocation() ||
                !descriptionTextField.getText().equals(project.getDescription()));
    }

    /**
     * Checks to make sure that the save location has been set, and it is writable by the user
     * @return Whether or not the save location is valid/readable/writable
     */
    private boolean checkSaveLocation() {
        if (this.projectLocation == null) {
            // Then the user hasn't selected a project directory, alert them!
            this.errorPopOver.setContentNode(new Label("Please select a Project Location"));
            this.errorPopOver.show(this.projectLocationTextField);
            return false;
        }
        // Confirm read/write access
        final File equalPermissionsFile = this.projectLocation.exists() ? this.projectLocation : this.projectLocation.getParentFile();
        if (!equalPermissionsFile.canRead()) {
            // Then we can't read from the directory, what's the point!
            this.errorPopOver.setContentNode(new Label("Can't read from the specified directory"));
            this.errorPopOver.show(this.projectLocationTextField);
            return false;
        }
        if (!equalPermissionsFile.canWrite()) {
            // Then we can't write to the directory
            this.errorPopOver.setContentNode(new Label("Can't write to the specified directory"));
            this.errorPopOver.show(this.projectLocationTextField);
            return false;
        }

        return true;
    }

    /**
     * Checks to make sure the short name is valid
     * @return Whether or not the short name is valid
     */
    private boolean checkShortName() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }
//        TODO Check for uniqueness
//        if (!UNIQUE CHECKER) {
//            shortNamePopOver.setContentNode(new Label("Short name must be unique"));
//            shortNamePopOver.show(shortNameTextField);
//            shortNameTextField.requestFocus();
//        }
        return true;
    }

    /**
     * Checks to make sure the long name is valid
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
     * Sets the open dialog functionality including getting the path chosen by the user.
     */
    private void setOpenButton() {
        final String EXTENSION = ".json";
        this.openButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*" + EXTENSION));
            File selectedFile = fileChooser.showSaveDialog(this.stage);
            if (selectedFile != null) {
                // ensure file has .json extension
                final String selectedFilename = selectedFile.getName();
                if (!selectedFilename.endsWith(EXTENSION)) {
                    // append extension
                    selectedFile = new File(selectedFile.getParentFile(), selectedFilename + EXTENSION);
                }
                // store selected file
                this.projectLocationTextField.setText(selectedFile.getAbsolutePath());
                this.projectLocation = selectedFile;
            }

        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the cancel button functionality
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> stage.close());
    }

    public Project getProject() {
        return project;
    }
}

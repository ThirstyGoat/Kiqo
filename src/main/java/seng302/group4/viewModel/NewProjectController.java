package seng302.group4.viewModel;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;

import seng302.group4.Project;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class NewProjectController implements Initializable {
    private Stage stage;
    private File projectLocation;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button newProjectButton;
    @FXML
    private TextField nameTextField;
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

    private final PopOver errorPopOver = new PopOver();

    public Project getProject() {
        return this.project;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.setCancelButton();
        this.setNewButton();
        this.setOpenButton();
        this.setShortNameSuggester();
        this.setShortNameHandler();

        this.setErrorPopOvers();
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Checks to make sure the long name is valid
     *
     * @return Whether or not the long name is valid
     */
    private boolean checkName() {
        if (this.nameTextField.getText().length() == 0) {
            this.errorPopOver.setContentNode(new Label("Name must not be empty"));
            this.errorPopOver.show(this.nameTextField);
            return false;
        }
        return true;
    }

    /**
     * Checks to make sure that the save location has been set, and it is
     * writable by the user
     *
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
     *
     * @return Whether or not the short name is valid
     */
    private boolean checkShortName() {
        if (this.shortNameTextField.getText().length() == 0) {
            this.errorPopOver.setContentNode(new Label("Short name must not be empty"));
            this.errorPopOver.show(this.shortNameTextField);
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
     * Sets the cancel button functionality
     */
    private void setCancelButton() {
        System.out.println("called close");
        this.cancelButton.setOnAction(event -> this.stage.close());
    }

    /**
     * Sets focus listeners on text fields so PopOvers are hidden upon focus
     */
    private void setErrorPopOvers() {
        // Set PopOvers as not detachable so we don't have floating PopOvers
        this.errorPopOver.setDetachable(false);

        // Set handlers so that popovers are hidden on field focus
        this.nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.errorPopOver.hide();
            }
        });
        this.shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.errorPopOver.hide();
            }
        });
        this.projectLocationTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.errorPopOver.hide();
            }
        });
    }

    /**
     * Sets the event handler for the New Project Button, performs validation
     * checks and instantiates the new project if applicable
     */
    private void setNewButton() {
        this.newProjectButton.setOnAction(event -> {

            // Hide existing error message if there is one
                this.errorPopOver.hide();

                // Perform validity checks and create project
                if (this.checkName() && this.checkShortName() && this.checkSaveLocation()) {

                    this.project = new Project(this.shortNameTextField.getText(), this.nameTextField.getText(), this.projectLocation,
                            this.descriptionTextField.getText());

                    // Close the new project dialog (this window)
                this.stage.close();
            }
        });
    }

    /**
     * Sets the open dialog functionality including getting the path chosen by
     * the user.
     */
    private void setOpenButton() {
        final String EXTENSION = ".json";
        this.openButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*" + EXTENSION));
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

    /**
     * Sets the listener on the nameTextField so that the shortNameTextField is
     * populated in real time up to a certain number of characters
     */
    private void setShortNameHandler() {
        this.shortNameTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!Objects.equals(
                            newValue,
                            this.nameTextField.getText().substring(0,
                                    Math.min(this.nameTextField.getText().length(), this.SHORT_NAME_SUGGESTED_LENGTH)))) {
                        this.shortNameModified = true;
                    }
                });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name
     * can be populated with a suggestion
     */
    private void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name
        // character by character up to specified characters
        this.nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), this.SHORT_NAME_SUGGESTED_LENGTH));
            if (!this.shortNameModified) {
                this.shortNameTextField.setText(suggestedShortName);
            }
        });
    }
}

package seng302.group4.viewModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.group4.Project;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCancelButton();
        setNewButton();
        setOpenButton();
        setShortNameSuggester();
        setShortNameHandler();
    }

    /**
     * Sets the event handler for the New Project Button, performs validation checks and instantiates the new project
     * if applicable
     */
    private void setNewButton() {
        newProjectButton.setOnAction(event -> {
            // TODO Validation
            project= new Project(shortNameTextField.getText(), nameTextField.getText(),
                    projectLocation, descriptionTextField.getText());

            // Close the new project dialog (this window)
            stage.close();
        });
    }

    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(newValue, nameTextField.getText().substring(0,
                    Math.min(nameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified = true;
            }
        });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name can be populated with a suggestion
     */
    private void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name character by character up to specified characters
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
            }
        });
    }

    /**
     * Sets the open dialog functionality including getting the path chosen by the user.
     */
    private void setOpenButton() {
        openButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            projectLocation = selectedDirectory;
            if (selectedDirectory != null) {
                projectLocationTextField.setText(selectedDirectory.getAbsolutePath());
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
        System.out.println("called close");
        cancelButton.setOnAction(event -> {
            stage.close();
        });
    }


    public Project getProject() {
        return project;
    }
}

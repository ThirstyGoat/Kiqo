package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import seng302.group4.Person;
import seng302.group4.Project;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by james on 20/03/15.
 */
public class PersonFormController implements Initializable {
    public String shortName;
    public String longName;
    public String description;
    public String userID;
    public String emailAddress;
    public String phoneNumber;
    public String department;
    private boolean valid = false;


    private Stage stage;
    private Person person;
    private PopOver errorPopOver = new PopOver();
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private boolean shortNameModified = false;

    private Project project;
    // FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField userIDTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField departmentTextField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setShortNameSuggester();
        setShortNameHandler();
        setErrorPopOvers();
        Platform.runLater(() -> PersonFormController.this.longNameTextField.requestFocus());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     */
    public void validate() {
        // Hide existing error message if there is one
        this.errorPopOver.hide();
        // Perform validity checks and create project
        if (checkName() && checkShortName()) {
            // Set project properties
            longName = longNameTextField.getText();
            shortName = shortNameTextField.getText();
            description = descriptionTextField.getText();
            userID = userIDTextField.getText();
            emailAddress = emailTextField.getText();
            phoneNumber = phoneTextField.getText();
            department = departmentTextField.getText();

            valid = true;
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

    public void setProject(Project project) {
        this.project = project;
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
        for (Person person : project.getPeople()) {
            if (shortNameTextField.getText().equals(person.getShortName())) {
                errorPopOver.setContentNode(new Label("Short name must be unique"));
                errorPopOver.show(shortNameTextField);
                return false;
            }
        }
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
     * Sets the listener on the nameTextField so that the shortNameTextField is populated in real time
     * up to a certain number of characters
     */
    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(newValue, longNameTextField.getText().substring(0,
                    Math.min(longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified = true;
            }
        });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name can be populated with a suggestion
     */
    private void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name character by character up to specified characters
        longNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
            }
        });
    }



    /**
     * Creates a Person that has any ignored fields from the dialog set to null
     * @return a Person object created by the new Person dialog
     */
    private Person createPerson() {
        String description = null;
        String userID = null;
        String emailAddress = null;
        String phoneNumber = null;
        String department = null;
        if (!descriptionTextField.getText().equals("")) {
            description = descriptionTextField.getText();
        }
        if (!userIDTextField.getText().equals("")) {
            userID = userIDTextField.getText();
        }
        if (!emailTextField.getText().equals("")) {
            emailAddress = emailTextField.getText();
        }
        if (!phoneTextField.getText().equals("")) {
            phoneNumber = phoneTextField.getText();
        }
        if (!departmentTextField.getText().equals("")) {
            department = departmentTextField.getText();
        }
        return new Person(shortNameTextField.getText(), longNameTextField.getText(), description, userID, emailAddress,
                phoneNumber, department);
    }

    /**
     * Returns the Person object created by the dialog box
     * @return the Person created by the dialog box
     */
    Person getPerson() {
        return person;
    }

    public boolean isValid() {
        return valid;
    }
}

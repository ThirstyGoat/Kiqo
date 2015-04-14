package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import seng302.group4.Project;
import seng302.group4.Skill;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by james on 20/03/15.
 */
public class SkillFormController implements Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public PopOver errorPopOver = new PopOver();
    private String shortName;
    private String description;
    private boolean valid = false;
    private Stage stage;
    private boolean shortNameModified = false;

    private Project project;


    // FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setErrorPopOvers();
        setShortNameHandler();
        Platform.runLater(shortNameTextField::requestFocus);
    }

    /**
     * Sets the TextFields displayed in the dialog to the Skill that will be edited.
     * @param skill the Skill that is loaded
     */
    public void loadSkill(final Skill skill) {
        shortNameTextField.setText(skill.getShortName());
        descriptionTextField.setText(skill.getDescription());
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     */
    public void validate() {
        // Hide existing error message if there is one
        this.errorPopOver.hide();
        // Perform validity checks and create project
        if (checkShortName()) {
            // Set project properties
            shortName = shortNameTextField.getText();
            description = descriptionTextField.getText();
            valid = true;
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
        // check for uniqueness inside the project

        // >>>>>>>>>>>>>>>>

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

    public void setProject(Project project) {
        this.project = project;
    }

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

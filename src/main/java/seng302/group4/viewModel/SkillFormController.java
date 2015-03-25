package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.PopOver;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by james on 20/03/15.
 */
public class SkillFormController implements Initializable {
    private String shortName;
    private String description;
    private boolean valid = false;


    private Stage stage;
    private PopOver errorPopOver = new PopOver();
    private final int SHORT_NAME_MAX_LENGTH = 20;

    private Project project;
    // FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setErrorPopOvers();
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

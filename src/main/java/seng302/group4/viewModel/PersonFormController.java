package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;
import seng302.group4.customNodes.GoatListSelectionView;

/**
 * Created by james on 20/03/15.
 */
public class PersonFormController implements Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public PopOver errorPopOver = new PopOver();
    ArrayList<Skill> skills = new ArrayList<Skill>();
    private String shortName;
    private String longName;
    private String description;
    private String userID;
    private String emailAddress;
    private String phoneNumber;
    private String department;
    private final ObservableList<Skill> targetSkills = FXCollections.observableArrayList();
    private boolean valid = false;
    private Stage stage;
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
    @FXML
    private GoatListSelectionView<Skill> skillsSelectionView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setShortNameHandler();
        setErrorPopOvers();
        Platform.runLater(longNameTextField::requestFocus);
    }

    /**
     * Sets the skills list data and formatting
     */
    private void setUpSkillsList() {
        skillsSelectionView.setSourceHeader(new Label("Skills Available:"));
        skillsSelectionView.setTargetHeader(new Label("Skills Selected:"));

        skillsSelectionView.setPadding(new Insets(0, 0, 0, 0));

        // Set the custom cell factory for the skills lists
        // Thank GoatListSelectionView for this fabulous method
        skillsSelectionView.setCellFactories(view -> {
            final ListCell<Skill> cell = new ListCell<Skill>() {
                @Override
                public void updateItem(Skill item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getShortName() : null);
                }
            };
            return cell;
        });
    }

    public void setUpSkillsListSelectionView() {
        final ObservableList<Skill> sourceSkills = FXCollections.observableArrayList();

        sourceSkills.addAll(project.getSkills());

        // Remove all skills from sourceSkills that are currently in targetSkills
        sourceSkills.removeAll(targetSkills);

        project.getSkills().addListener((ListChangeListener<Skill>) c -> {
            c.next();
            // We remove skills from the sourceSkills that were removed from the project.
            // Note that this shouldn't actually be possible since undo/redo should be disabled
            sourceSkills.removeAll(c.getRemoved());
            targetSkills.removeAll(c.getRemoved());
            sourceSkills.addAll(c.getAddedSubList());
        });

        skillsSelectionView.getSourceListView().setItems(sourceSkills);
        skillsSelectionView.getTargetListView().setItems(targetSkills);
    }


    /**
     * Sets the TextFields displayed in the dialog to the Person that will be edited.
     * @param person the Person that is loaded
     */
    public void loadPerson(final Person person) {
        longNameTextField.setText(person.getLongName());
        shortNameTextField.setText(person.getShortName());
        descriptionTextField.setText(person.getDescription());
        userIDTextField.setText(person.getUserID());
        emailTextField.setText(person.getEmailAddress());
        phoneTextField.setText(person.getPhoneNumber());
        departmentTextField.setText(person.getDepartment());

        // Load existing skills into skill list
        targetSkills.setAll(person.getSkills());
        setUpSkillsListSelectionView();
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     */
    public void validate() {
        // Hide existing error message if there is one
        errorPopOver.hide();
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

            skills.clear();
            skills.addAll(targetSkills);

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
            // Auto populate short name text field
            if (!Objects.equals(newValue, longNameTextField.getText().substring(0,
                    Math.min(longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified = true;
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name can be populated with a suggestion
     */
    public void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name character by character up to specified characters
        longNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
            }
        });
    }


    // ------- is this used?? -----------

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
                phoneNumber, department, getSkills());
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public boolean isValid() {
        return valid;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProject(Project project) {
        this.project = project;
        setUpSkillsList();
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


    /**
     * Warms if the short name of a person is not unique
     */
    public void warnShortnameNotUnique() {
        errorPopOver.setContentNode(new Label("Short name must be unique"));
        errorPopOver.show(shortNameTextField);
    }
}

package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreatePersonCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.nodes.GoatListSelectionView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by james on 20/03/15.
 */
public class PersonFormController implements Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    public PopOver errorPopOver = new PopOver();
    private Stage stage;
    private Organisation organisation;
    private Person person;
    private boolean valid = false;
    private boolean shortNameModified = false;
    private Command command;
    private final ObservableList<Skill> targetSkills = FXCollections.observableArrayList();


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
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setShortNameHandler();
        setErrorPopOvers();
        setPrompts();
        setButtonHandlers();
        setShortNameSuggester();
        Platform.runLater(longNameTextField::requestFocus);
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Bill Goat");
        descriptionTextField.setPromptText("Describe this awesome person");
        userIDTextField.setPromptText("Identify this person!");
        emailTextField.setPromptText("hello@example.com");
        phoneTextField.setPromptText("A phone number would be good too.");
        departmentTextField.setPromptText("What department do they work for?");
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

    private void setSkillsListSelectionViewData() {
        final ObservableList<Skill> sourceSkills = FXCollections.observableArrayList();

        sourceSkills.addAll(organisation.getSkills());
        if (person != null) {
            sourceSkills.removeAll(person.getSkills());
            targetSkills.addAll(person.getSkills());
        }

//        organisation.getSkills().addListener((ListChangeListener<Skill>) c -> {
//            c.next();
//            // We remove skills from the sourceSkills that were removed from the project.
//            // Note that this shouldn't actually be possible since undo/redo should be disabled
//            sourceSkills.removeAll(c.getRemoved());
//            targetSkills.removeAll(c.getRemoved());
//            sourceSkills.addAll(c.getAddedSubList());
//        });

        skillsSelectionView.getSourceListView().setItems(sourceSkills);
        skillsSelectionView.getTargetListView().setItems(targetSkills);
    }

    /**
     * Sets the TextFields displayed in the dialog to the Person that will be edited.
     * @param person the Person that is loaded
     */
    public void setPerson(final Person person) {
        this.person = person;

        if (person == null) {
            // Then we are creating a new one
            stage.setTitle("Create Person");
            okButton.setText("Create Person");
        } else {
            // We are editing an existing Person
            stage.setTitle("Edit Person");
            okButton.setText("Save");

            longNameTextField.setText(person.getLongName());
            shortNameTextField.setText(person.getShortName());
            descriptionTextField.setText(person.getDescription());
            userIDTextField.setText(person.getUserID());
            emailTextField.setText(person.getEmailAddress());
            phoneTextField.setText(person.getPhoneNumber());
            departmentTextField.setText(person.getDepartment());
        }

        // Load skills into skill lists
        setSkillsListSelectionViewData();
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
        final ArrayList<Skill> skills = new ArrayList<>();
        skills.addAll(targetSkills);

        if (person == null) {
            Person p = new Person(shortNameTextField.getText(), longNameTextField.getText(),
                    descriptionTextField.getText(), userIDTextField.getText(), emailTextField.getText(),
                    phoneTextField.getText(), departmentTextField.getText(), skills);
            command = new CreatePersonCommand(p, organisation);
        } else {
            final ArrayList<Command<?>> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(person.getShortName())) {
                changes.add(new EditCommand<>(person, "shortName", shortNameTextField.getText()));
            }
            if (!longNameTextField.getText().equals(person.getLongName())) {
                changes.add(new EditCommand<>(person, "longName", longNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(person.getDescription())) {
                changes.add(new EditCommand<>(person, "description", descriptionTextField.getText()));
            }
            if (!userIDTextField.getText().equals(person.getUserID())) {
                changes.add(new EditCommand<>(person, "userID", userIDTextField.getText()));
            }
            if (!emailTextField.getText().equals(person.getEmailAddress())) {
                changes.add(new EditCommand<>(person, "emailAddress", emailTextField.getText()));
            }
            if (!phoneTextField.getText().equals(person.getPhoneNumber())) {
                changes.add(new EditCommand<>(person, "phoneNumber", phoneTextField.getText()));
            }
            if (!departmentTextField.getText().equals(person.getDepartment())) {
                changes.add(new EditCommand<>(person, "department", departmentTextField.getText()));
            }
            if (!(skills.containsAll(person.getSkills())
                    && person.getSkills().containsAll(skills))) {
                changes.add(new EditCommand<>(person, "skills", skills));
            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Person", changes);
        }
    }
    
    
    
    private boolean validate() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }

        if (person != null) {
            // we're editing
            if (shortNameTextField.getText().equals(person.getShortName())) {
                // then that's fine
                valid = true;
                setCommand();
                return true;
            }
        }

        // shortname must be unique
        for (final Person p : organisation.getPeople()) {
            if (shortNameTextField.getText().equals(p.getShortName())) {
                errorPopOver.setContentNode(new Label("Short name must be unique"));
                errorPopOver.show(shortNameTextField);
                return false;
            }
        }

        valid = true;
        setCommand();
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
                errorPopOver.setContentNode(new Label("Short name must be under " + SHORT_NAME_MAX_LENGTH +
                        " characters"));
                errorPopOver.show(shortNameTextField);
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

    public boolean isValid() {
        return valid;
    }

    public Command<?> getCommand() { return command; }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
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
                errorPopOver.hide(Duration.millis(0));
            }
        });
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide(Duration.millis(0));
            } else {
                errorPopOver.hide(Duration.millis(0));
            }
        });
    }
}

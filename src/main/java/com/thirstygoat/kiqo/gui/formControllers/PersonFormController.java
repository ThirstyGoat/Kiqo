package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreatePersonCommand;
import com.thirstygoat.kiqo.gui.nodes.GoatDialog;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
 * Created by james on 20/03/15.
 */
public class PersonFormController extends FormController<Person> {
//    private final ObservableList<Skill> targetSkills = FXCollections.observableArrayList(); TODO
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Organisation organisation;
    private Person person;
    private boolean valid = false;
    private Command command;
    private boolean poOfTeam;
    private boolean smOfTeam;
    private boolean usingPoSkillInBacklog;
    private ArrayList<Backlog> backlogsOwned = new ArrayList<>();
    // Begin FXML Injections
    @FXML
    private Label heading;
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField userIdTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField departmentTextField;
//    @FXML TODO
//    private GoatFilteredListSelectionView<Skill> skillsSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPrompts();
        setButtonHandlers();
        Utilities.initShortNameSuggester(longNameTextField.textProperty(), shortNameTextField.textProperty());
        Platform.runLater(() -> {
            // wait for textfields to exist
            setValidationSupport();
            longNameTextField.requestFocus();
        });
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                Utilities.shortnameIsUnique(shortNameTextField.getText(), person, organisation.getPeople());

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty"));

        validationSupport.registerValidator(longNameTextField,
                Validator.createEmptyValidator("Name must not be empty", Severity.ERROR));

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
        shortNameTextField.setPromptText("Must be under " + Utilities.SHORT_NAME_MAX_LENGTH + " characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this person");
        userIdTextField.setPromptText("Identify this person!");
        emailTextField.setPromptText("hello@example.com");
        phoneTextField.setPromptText("A phone number would be good too.");
        departmentTextField.setPromptText("What department do they work for?");
    }
// TODO
//    private void setSkillsListSelectionViewData() {
//        final ObservableList<Skill> sourceSkills = FXCollections.observableArrayList();
//
//        sourceSkills.addAll(organisation.getSkills());
//        if (person != null) {
//            sourceSkills.removeAll(person.getSkills());
//            targetSkills.addAll(person.getSkills());
//        }
//
//        skillsSelectionView.bindAllItems(sourceSkills);
//        skillsSelectionView.setTargetItems(targetSkills);
//        skillsSelectionView.setStringPropertyCallback(skill -> skill.shortNameProperty());
//
//    }

    /**
     * Sets the TextFields displayed in the dialog to the Person that will be edited.
     * @param person the Person that is loaded
     */
    @Override
    public void populateFields(final Person person) {
        this.person = person;

//        setSkillsListSelectionViewData(); TODO

        if (person != null) {
            // We are editing an existing Person

            longNameTextField.setText(person.getLongName());
            shortNameTextField.setText(person.getShortName());
            descriptionTextField.setText(person.getDescription());
            userIdTextField.setText(person.getUserId());
            emailTextField.setText(person.getEmailAddress());
            phoneTextField.setText(person.getPhoneNumber());
            departmentTextField.setText(person.getDepartment());

            setSkillRemovalHandlers();
            okButton.setText("Done");
        } else {
            okButton.setText("Create Person");
        }
    }

    private void setSkillRemovalHandlers() {
        // Is this person USING their PO skill (either as a PO for a team, or a PO for a backlog)?
        boolean usingPOBacklog = false;
        backlogsOwned.clear();

        // Check if they are the PO of any backlogs
        for (Project project : organisation.getProjects()) {
            for (Backlog backlog : project.observableBacklogs()) {
                if (backlog.getProductOwner() == person) {
                    usingPOBacklog = true;
                    backlogsOwned.add(backlog);
                }
            }
        }

        poOfTeam = person.getTeam() != null && person.getTeam().getProductOwner() == person;
        smOfTeam = person.getTeam() != null && person.getTeam().getScrumMaster() == person;
        usingPoSkillInBacklog = usingPOBacklog;
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> stage.close());
    }

    private void setCommand() {
        final ArrayList<Skill> skills = new ArrayList<>();
//        TODO skills.addAll(targetSkills);

        if (person == null) {
            final Person p = new Person(shortNameTextField.getText(), longNameTextField.getText(),
                    descriptionTextField.getText(), userIdTextField.getText(), emailTextField.getText(),
                    phoneTextField.getText(), departmentTextField.getText(), skills);
            command = new CreatePersonCommand(p, organisation);
        } else {
            final ArrayList<Command> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(person.getShortName())) {
                changes.add(new EditCommand<>(person, "shortName", shortNameTextField.getText()));
            }
            if (!longNameTextField.getText().equals(person.getLongName())) {
                changes.add(new EditCommand<>(person, "longName", longNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(person.getDescription())) {
                changes.add(new EditCommand<>(person, "description", descriptionTextField.getText()));
            }
            if (!userIdTextField.getText().equals(person.getUserId())) {
                changes.add(new EditCommand<>(person, "userId", userIdTextField.getText()));
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


//            if (!(skills.containsAll(person.getSkills()) TODO
//                    && person.getSkills().containsAll(skills))) {
//                changes.add(new EditCommand<>(person, "skills", skills));
//            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Person", changes);
        }
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     * @return all fields are valid
     */
    private boolean validate() {
        if (validationSupport.isInvalid()) {
            return false;
        } else if (person != null) {
            Skill poSkill = organisation.getPoSkill();
            Skill smSkill = organisation.getSmSkill();

            // Check if the user has removed either PO/SM skills and they are using that skill

//            ArrayList<Skill> removedSkills = new ArrayList<>();
//            removedSkills.addAll(person.getSkills());
//            removedSkills.removeAll(skillsSelectionView.getTargetItems());
//
//            if (removedSkills.contains(poSkill)) {
//                // Then they are trying to remove the PO skill
//                if (poOfTeam || usingPoSkillInBacklog) {
//                    // Then they are a product owner, and owner of 1 or more backlogs
//                    final String teamLine = (poOfTeam) ? "Team: " + person.getTeam().getShortName() + "\n" : "";
//                    final String backlogsLine = (usingPoSkillInBacklog) ?
//                            Utilities.pluralise(backlogsOwned.size(), "Backlog", "Backlogs") + ": " +
//                                    Utilities.concatenateItemsList(backlogsOwned, 5) : "";
//                    GoatDialog.showAlertDialog(
//                            stage,
//                            "Can't remove skill",
//                            "PO Skill can't be removed",
//                            person.getShortName() + " is currently the PO of:\n" +
//                                    teamLine + backlogsLine
//                    );
//                    return false;
//                }
//            }
//            if (removedSkills.contains(smSkill) && smOfTeam) {
//                GoatDialog.showAlertDialog(
//                        stage,
//                        "Can't remove skill",
//                        "SM Skill can't be removed",
//                        person.getShortName() + " is currently the SM of Team: " + person.getTeam().getShortName()
//                );
//                return false;
//            } TODO

            valid = true;
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
    public Command getCommand() { return command; }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public StringProperty headingProperty() {
        return heading.textProperty();
    }
}

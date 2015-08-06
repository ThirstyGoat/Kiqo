package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreatePersonCommand;
import com.thirstygoat.kiqo.gui.nodes.GoatDialog;
import com.thirstygoat.kiqo.gui.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Created by james on 20/03/15.
 */
public class PersonFormController extends FormController<Person> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ObservableList<Skill> targetSkills = FXCollections.observableArrayList();
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Organisation organisation;
    private Person person;
    private boolean valid = false;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private Command command;
    // Begin FXML Injections
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
    @FXML
    private GoatListSelectionView<Skill> skillsSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        Platform.runLater(longNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                Utilities.shortnameIsUnique(shortNameTextField.getText(), person, organisation.getPeople());

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty."));

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
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this person");
        userIdTextField.setPromptText("Identify this person!");
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
        skillsSelectionView.setCellFactories(view -> new ListCell<Skill>() {
            @Override
            public void updateItem(Skill item, boolean empty) {
                super.updateItem(item, empty);
                setText(item != null ? item.getShortName() : null);
            }
        });
    }

    private void setSkillsListSelectionViewData() {
        final ObservableList<Skill> sourceSkills = FXCollections.observableArrayList();

        sourceSkills.addAll(organisation.getSkills());
        if (person != null) {
            sourceSkills.removeAll(person.getSkills());
            targetSkills.addAll(person.getSkills());
        }

        skillsSelectionView.getSourceListView().setItems(sourceSkills);
        skillsSelectionView.getTargetListView().setItems(targetSkills);
    }

    /**
     * Sets the TextFields displayed in the dialog to the Person that will be edited.
     * @param person the Person that is loaded
     */
    @Override
    public void populateFields(final Person person) {
        this.person = person;
        okButton.setText("Done");

        setSkillsListSelectionViewData();

        if (person != null) {
            // We are editing an existing Person
            shortNameModified.set(true);

            longNameTextField.setText(person.getLongName());
            shortNameTextField.setText(person.getShortName());
            descriptionTextField.setText(person.getDescription());
            userIdTextField.setText(person.getUserId());
            emailTextField.setText(person.getEmailAddress());
            phoneTextField.setText(person.getPhoneNumber());
            departmentTextField.setText(person.getDepartment());

            setSkillRemovalHandlers();
        }
    }

    private void setSkillRemovalHandlers() {
        // Is this person USING their PO skill (either as a PO for a team, or a PO for a backlog)?
        boolean usingPoSkillInBacklog = false;
        final List<Backlog> backlogsOwned = new ArrayList<>();

        // Check if they are the PO of any backlogs
        for (Project project : organisation.getProjects()) {
            for (Backlog backlog : project.observableBacklogs()) {
                if (backlog.getProductOwner() == person) {
                    usingPoSkillInBacklog = true;
                    backlogsOwned.add(backlog);
                }
            }
        }

        Skill poSkill = organisation.getPoSkill();
        Skill smSkill = organisation.getSmSkill();

        // Intercept move to source action from GoatListSelectionView to detect if the user is removing a used skill
        final boolean poOfTeam = person.getTeam() != null && person.getTeam().getProductOwner() == person;
        final boolean smOfTeam = person.getTeam() != null && person.getTeam().getScrumMaster() == person;
        final boolean finalUsingPoSkillInBacklog = usingPoSkillInBacklog;

        ListView<Skill> targetListView = skillsSelectionView.getTargetListView();

        skillsSelectionView.skin.getMoveToSourceButton().setOnAction(event -> {
            if (targetListView.getSelectionModel().getSelectedItems().contains(poSkill)) {
                // Then they are trying to remove the PO skill
                if (poOfTeam || finalUsingPoSkillInBacklog) {
                    // Then they are a product owner, and owner of 1 or more backlogs
                    final String teamLine = (poOfTeam) ? "Team: " + person.getTeam().getShortName() + "\n" : "";
                    final String backlogsLine = (finalUsingPoSkillInBacklog) ?
                            Utilities.pluralise(backlogsOwned.size(), "Backlog", "Backlogs") + ": " +
                            Utilities.concatenateItemsList(backlogsOwned, 5) : "";
                    GoatDialog.showAlertDialog(
                            stage,
                            "Can't remove skill",
                            "PO Skill can't be removed",
                            person.getShortName() + " is currently the PO of:\n" +
                            teamLine + backlogsLine
                    );
                    return;
                }
            }
            if (targetListView.getSelectionModel().getSelectedItems().contains(smSkill) && smOfTeam) {
                GoatDialog.showAlertDialog(
                        stage,
                        "Can't remove skill",
                        "SM Skill can't be removed",
                        person.getShortName() + " is currently the SM of Team: " + person.getTeam().getShortName()
                );
                return;
            }
            skillsSelectionView.skin.moveToSource();
        });

        skillsSelectionView.skin.getMoveToSourceAllButton().setOnAction(event -> {
            if (targetListView.getItems().contains(poSkill)) {
                // Then they are trying to remove the PO skill
                if (poOfTeam || finalUsingPoSkillInBacklog) {
                    // Then they are a product owner, and owner of 1 or more backlogs
                    final String teamLine = (poOfTeam) ? "Team: " + person.getTeam().getShortName() + "\n" : "";
                    final String backlogsLine = (finalUsingPoSkillInBacklog) ?
                            Utilities.pluralise(backlogsOwned.size(), "Backlog", "Backlogs") + ": " +
                                    Utilities.concatenateItemsList(backlogsOwned, 5) : "";
                    GoatDialog.showAlertDialog(
                            stage,
                            "Can't remove skill",
                            "PO Skill can't be removed",
                            person.getShortName() + " is currently the PO of:\n" +
                                    teamLine + backlogsLine
                    );
                    return;
                }
            }
            if (targetListView.getSelectionModel().getSelectedItems().contains(smSkill) && smOfTeam) {
                GoatDialog.showAlertDialog(
                        stage,
                        "Can't remove skill",
                        "SM Skill can't be removed",
                        person.getShortName() + " is currently the SM of Team: " + person.getTeam().getShortName()
                );
                return;
            }
            skillsSelectionView.skin.moveToSourceAll();
        });
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
        skills.addAll(targetSkills);

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
            if (!(skills.containsAll(person.getSkills())
                    && person.getSkills().containsAll(skills))) {
                changes.add(new EditCommand<>(person, "skills", skills));
            }

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
        } else {
            valid = true;
        }
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
                shortNameModified.set(true);
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
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
        setUpSkillsList();
    }
}
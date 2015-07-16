package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Carina on 15/05/2015.
 */
public class StoryFormController extends FormController<Story> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Story story;
    private Person creator;
    private Project project;
    private Backlog backlog;
    private Organisation organisation;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private boolean valid = false;
    private Command<?> command;
    // Begin FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField creatorTextField;
    @FXML
    private  TextField projectTextField;
    @FXML
    private TextField priorityTextField;
    @FXML
    private ComboBox<Scale> estimationScaleComboBox;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        priorityTextField.setText(Integer.toString(Story.DEFAULT_PRIORITY));
        Platform.runLater(longNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this story.");

        // Populate Estimation Scale ComboBox
        estimationScaleComboBox.setItems(FXCollections.observableArrayList(
                Scale.FIBONACCI,
                Scale.TSHIRT_SIZE,
                Scale.DOG_BREEDS
        ));
        estimationScaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default
    }

    @Override
    public void populateFields(final Story story) {
        this.story = story;

        if (story == null) {
            // Then we are creating a new one
            stage.setTitle("Create Story");
            okButton.setText("Done");
        } else {
            // We are editing an existing story
            stage.setTitle("Edit Story");
            okButton.setText("Done");
            shortNameModified.set(true);

            longNameTextField.setText(story.getLongName());
            shortNameTextField.setText(story.getShortName());
            descriptionTextField.setText(story.getDescription());
            creatorTextField.setText(story.getCreator().getShortName());
            // Creator field isn't meant to be changeable
            creatorTextField.setDisable(true);
            projectTextField.setText(story.getProject().getShortName());
            priorityTextField.setText(Integer.toString(story.getPriority()));
            estimationScaleComboBox.getSelectionModel().select(story.getScale());
        }
    }
    private void setValidationSupport() {
    // Validation for short name
        final Predicate<String> shortNameValidation = s -> {
            if (s.length() == 0) {
                return false;
            }
            if (project == null) {
                return true;
            }
            Collection<Collection<? extends Item>> existingBacklogs = new ArrayList<>();
            existingBacklogs.add(project.getUnallocatedStories());
            existingBacklogs.addAll(project.getBacklogs().stream().map(Backlog::observableStories).collect(Collectors.toList()));

            return Utilities.shortnameIsUniqueMultiple(shortNameTextField.getText(), story, existingBacklogs);
        };

        final Predicate<String> personValidation = s -> {
            for (final Person p : organisation.getPeople()) {
                if (p.getShortName().equals(s)) {
                    creator = p;
                    return true;
                }
            }
            return false;
        };

        final Predicate<String> projectValidation = s -> {
            for (final Project p : organisation.getProjects()) {
                if (p.getShortName().equals(projectTextField.getText())) {
                    project = p;
                    // Redo validation for shortname text field
                    final String snt = shortNameTextField.getText();
                    shortNameTextField.setText("");
                    shortNameTextField.setText(snt);
                    return true;
                }
            }
            return false;
        };

        final Predicate<String> priorityValidation = s -> {
            try {
                int i = Integer.parseInt(s);
                if (i < Story.MIN_PRIORITY || i > Story.MAX_PRIORITY) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        };

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty."));

        validationSupport.registerValidator(longNameTextField,
                Validator.createEmptyValidator("Long name must not be empty", Severity.ERROR));

        validationSupport.registerValidator(creatorTextField, Validator.createPredicateValidator(personValidation,
                        "Person must already exist"));

        validationSupport.registerValidator(projectTextField, Validator.createPredicateValidator(projectValidation,
                "Project must already exist"));

        validationSupport.registerValidator(priorityTextField,
                Validator.createPredicateValidator(priorityValidation, "Priority must be an integer between "
                                + Story.MIN_PRIORITY + " and " + Story.MAX_PRIORITY));

        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue);
        });
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


    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> stage.close());
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
    public boolean isValid() { return valid; }

    @Override
    public Command<?> getCommand() { return command; }

    public void setCommand() {
        if (story == null) {
            // new story command
            story = new Story(shortNameTextField.getText(), longNameTextField.getText(), descriptionTextField.getText(), creator,
                     project, backlog, Integer.parseInt(priorityTextField.getText()), 0, estimationScaleComboBox.getValue());
            command = new CreateStoryCommand(story);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!longNameTextField.getText().equals(story.getLongName())) {
                changes.add(new EditCommand<>(story, "longName", longNameTextField.getText()));
            }
            if (!shortNameTextField.getText().equals(story.getShortName())) {
                changes.add(new EditCommand<>(story, "shortName", shortNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(story.getDescription())) {
                changes.add(new EditCommand<>(story, "description", descriptionTextField.getText()));
            }
//            Creator can't be changed
//            if (!creator.equals(story.getCreator())) {
//                changes.add(new EditCommand<>(story, "creator", creator));
//            }
            if (!project.equals(story.getProject())) {
                if (story.getBacklog() != null) {
                    changes.add(new MoveItemCommand<>(story, story.getBacklog().observableStories(), project.observableUnallocatedStories()));
                } else {
                    changes.add(new MoveItemCommand<>(story, story.getProject().observableUnallocatedStories(), project.observableUnallocatedStories()));
                }
                // If story is changing projects, then it shouldn't be in any backlog
                changes.add(new EditCommand<>(story, "backlog", null));
                changes.add(new EditCommand<>(story, "project", project));
            }

            if (Integer.parseInt(priorityTextField.getText()) != story.getPriority()) {
                changes.add(new EditCommand<>(story, "priority", Integer.parseInt(priorityTextField.getText())));
            }

            if (estimationScaleComboBox.getValue() != story.getScale()) {
                changes.add(new EditCommand<>(story, "scale", estimationScaleComboBox.getValue()));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Release", changes);
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        setTextFieldSuggester(creatorTextField, organisation.getPeople());
        setTextFieldSuggester(projectTextField, organisation.getProjects());
    }
}
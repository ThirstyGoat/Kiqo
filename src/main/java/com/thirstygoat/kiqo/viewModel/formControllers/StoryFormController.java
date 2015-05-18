package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Carina on 15/05/2015.
 */
public class StoryFormController implements Initializable, IFormController<Story> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Story story;
    private Person creator;
    private Project project;
    private Organisation organisation;
    private boolean shortNameModified = false;
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
    private TextField priorityTextField ;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        setShortNameSuggester();
        setCreatorTextFieldSuggester();
        setProjectTextFieldSuggester();
        priorityTextField.setText(Integer.toString(Story.DEFAULT_PRIORITY));
        Platform.runLater(longNameTextField::requestFocus);

        setValidationSupport();
    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this story.");
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
            shortNameModified = true;

            longNameTextField.setText(story.getLongName());
            shortNameTextField.setText(story.getShortName());
            descriptionTextField.setText(story.getDescription());
            creatorTextField.setText(story.getCreator().getShortName());
            // Creator field isn't meant to be changeable
            creatorTextField.setDisable(true);
            projectTextField.setText(story.getProject().getShortName());
            priorityTextField.setText(Integer.toString(story.getPriority()));
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
            return Utilities.shortnameIsUnique(shortNameTextField.getText(), story, project.getStories());
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
            if (newValue) {
                // Then invalid, disable ok button
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
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

        cancelButton.setOnAction(event -> {
            stage.close();
        });
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

    private void setCreatorTextFieldSuggester() {
        // use a callback to get an up-to-date creator list, instead of just whatever exists at initialisation.
        // use a String converter so that the Creator's short name is used.
        final AutoCompletionBinding<Person> binding = TextFields.bindAutoCompletion(creatorTextField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Person>>() {
            @Override
            public Collection<Person> call(AutoCompletionBinding.ISuggestionRequest request) {
                // filter based on input string
                final Collection<Person> persons = organisation.getPeople().stream()
                        .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                        .collect(Collectors.toList());
                return persons;
            }
        }, new StringConverter<Person>() {
            @Override
            public Person fromString(String string) {
                for (final Person creator : organisation.getPeople()) {
                    if (project.getShortName().equals(string)) {
                        return creator;
                    }
                }
                return null;
            }

            @Override
            public String toString(Person creator) {
                return creator.getShortName();
            }
        });

        creatorTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // forces suggestion list to show
                binding.setUserInput("");
            }
        });

    }

    private void setProjectTextFieldSuggester() {
        // use a callback to get an up-to-date project list, instead of just whatever exists at initialisation.
        // use a String converter so that the Project's short name is used.
        final AutoCompletionBinding<Project> binding = TextFields.bindAutoCompletion(projectTextField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Project>>() {
            @Override
            public Collection<Project> call(AutoCompletionBinding.ISuggestionRequest request) {
                // filter based on input string
                final Collection<Project> projects = organisation.getProjects().stream()
                        .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                        .collect(Collectors.toList());
                return projects;
            }

        }, new StringConverter<Project>() {
            @Override
            public Project fromString(String string) {
                for (final Project project : organisation.getProjects()) {
                    if (project.getShortName().equals(string)) {
                        return project;
                    }
                }
                return null;
            }

            @Override
            public String toString(Project project) {
                return project.getShortName();
            }
        });

        projectTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // forces suggestion list to show
                binding.setUserInput("");
            }
        });
    }

    /**
     * Sets up the listener for changes in the long name, so that the short name
     * can be populated with a suggestion
     */
    public void setShortNameSuggester() {
        // Listen for changes in the long name, and populate the short name
        // character by character up to specified characters
        longNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedShortName = newValue.substring(0, Math.min(newValue.length(), SHORT_NAME_SUGGESTED_LENGTH));
            if (!shortNameModified) {
                shortNameTextField.setText(suggestedShortName);
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
                     project,Integer.parseInt(priorityTextField.getText()));
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
                changes.add(new MoveItemCommand<>(story, story.getProject().observableStories(), project.observableStories()));
                changes.add(new EditCommand<>(story, "project", project));
            }

            if (Integer.parseInt(priorityTextField.getText()) != story.getPriority()) {
                changes.add(new EditCommand<>(story, "priority", Integer.parseInt(priorityTextField.getText())));
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
    }

}
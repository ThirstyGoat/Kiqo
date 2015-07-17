package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.StoryFormViewModel;
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
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Carina on 15/05/2015.
 */
public class StoryFormController extends FormController<Story> {
    private StoryFormViewModel viewModel;
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
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
        viewModel = new StoryFormViewModel();
        bindFields();
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        priorityTextField.setText(Integer.toString(Story.DEFAULT_PRIORITY));
        Platform.runLater(longNameTextField::requestFocus);

    }

    private void bindFields() {
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        estimationScaleComboBox.valueProperty().bindBidirectional(viewModel.scaleObjectProperty());
        projectTextField.textProperty().bindBidirectional(viewModel.projectNameProperty());
        priorityTextField.textProperty().bindBidirectional(viewModel.priorityProperty());

    }

    private void setPrompts() {
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        longNameTextField.setPromptText("Billy Goat");
        descriptionTextField.setPromptText("Describe this story.");

        // Populate Estimation Scale ComboBox
        estimationScaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        estimationScaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default
    }

    @Override
    public void populateFields(final Story story) {

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
        // checks that length of the shortName isn't 0 and that it its unique
//        final Predicate<String> shortNameValidation = s -> {
//            if (s.length() == 0) {
//                return false;
//            }
//            if (project == null) {
//                return true;
//            }
//            Collection<Collection<? extends Item>> existingBacklogs = new ArrayList<>();
//            existingBacklogs.add(project.getUnallocatedStories());
//            existingBacklogs.addAll(project.getBacklogs().stream().map(Backlog::observableStories).collect(Collectors.toList()));
//
//            return Utilities.shortnameIsUniqueMultiple(shortNameTextField.getText(), story, existingBacklogs);
//        };
//`
//        final Predicate<String> personValidation = s -> {
//            for (final Person p : organisation.getPeople()) {
//                if (p.getShortName().equals(s)) {
//                    creator = p;
//                    return true;
//                }
//            }
//            return false;
//        };
//
//        final Predicate<String> projectValidation = s -> {
//            for (final Project p : organisation.getProjects()) {
//                if (p.getShortName().equals(projectTextField.getText())) {
//                    project = p;
//                    // Redo validation for shortname text field
//                    final String snt = shortNameTextField.getText();
//                    shortNameTextField.setText("");
//                    shortNameTextField.setText(snt);
//                    return true;
//                }
//            }
//            return false;
//        };
//
//        final Predicate<String> priorityValidation = s -> {
//            try {
//                int i = Integer.parseInt(s);
//                if (i < Story.MIN_PRIORITY || i > Story.MAX_PRIORITY) {
//                    return false;
//                }
//            } catch (NumberFormatException e) {
//                return false;
//            }
//            return true;
//        };

            validationSupport.registerValidator(shortNameTextField,
                    Validator.createPredicateValidator(viewModel.getShortNameValidation(),
                            "Short name must be unique and not empty."));

            validationSupport.registerValidator(longNameTextField,
                    Validator.createEmptyValidator("Long name must not be empty", Severity.ERROR));

            validationSupport.registerValidator(creatorTextField, Validator.createPredicateValidator(
                    viewModel.getPersonValidation(),
                    "Person must already exist"));


            validationSupport.registerValidator(projectTextField, Validator.createPredicateValidator(
                    viewModel.getProjectValidation(),
                    "Project must already exist"));

            validationSupport.registerValidator(priorityTextField,
                    Validator.createPredicateValidator(
                            viewModel.getPriorityValidation(), "Priority must be an integer between "
                                    + Story.MIN_PRIORITY + " and " + Story.MAX_PRIORITY));

            validationSupport.registerValidator(estimationScaleComboBox,
                    Validator.createEmptyValidator("Estimation Scale must not be empty", Severity.ERROR));

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
    public Command<?> getCommand() { return viewModel.getCommand(); }

    public void setCommand() {
        viewModel.setCommand();

    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @Override
    public void setOrganisation(Organisation organisation) {
        viewModel.setOrganisation(organisation);
        setTextFieldSuggester(creatorTextField, organisation.getPeople());
        setTextFieldSuggester(projectTextField, organisation.getProjects());
        setValidationSupport();
    }
}
package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
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
 * Created by lih18 on 20/05/15.
 */
public class BacklogFormController implements Initializable, IFormController<Backlog> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ObservableList<Story> targetStories = FXCollections.observableArrayList();
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private Organisation organisation;
//    private Project project;
    private ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private Backlog backlog;
    private Person productOwner;
    private boolean valid = false;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private Command<?> command;
    // Begin FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private TextField productOwnerTextField;
    @FXML
    private GoatListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(final URL location, ResourceBundle resources) {
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        setProjectTextFieldSuggester();
        setProductOwnerTextFieldSuggester();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        Platform.runLater(longNameTextField::requestFocus);

        setValidationSupport();
        setListeners();
    }

    private void setListeners() {
        project.addListener(((observable, oldValue, newValue) -> {
            storySelectionView.getTargetListView().getItems().clear();
            setStoryListSelectionViewData();
        }));
    }

    private void setValidationSupport() {
        // Validation for short name
        final Predicate<String> shortNameValidation = s -> {
            if (s.length() == 0) {
                return false;
            }
            if (project.get() == null) {
                return true;
            }
            return Utilities.shortnameIsUnique(shortNameTextField.getText(), backlog, project.get().getBacklogs());
        };

        final Predicate<String> projectValidation = s -> {
            for (final Project p : organisation.getProjects()) {
                if (p.getShortName().equals(projectTextField.getText())) {
                    project.set(p);
                    // Redo validation for shortname text field
                    final String snt = shortNameTextField.getText();
                    shortNameTextField.setText("");
                    shortNameTextField.setText(snt);
                    return true;
                }
            }
            return false;
        };

        final Predicate<String> personValidation = s -> {
            for (final Person p : organisation.getPeople()) {
                if (p.getShortName().equals(s)) {
                    productOwner = p;
                    return true;
                }
            }
            return false;
        };

        validationSupport.registerValidator(longNameTextField,
                Validator.createEmptyValidator("Long name must not be empty", Severity.ERROR));

        validationSupport.registerValidator(shortNameTextField, Validator.createPredicateValidator(shortNameValidation,
                "Short name must be unique and not empty"));

        validationSupport.registerValidator(projectTextField, Validator.createPredicateValidator(projectValidation,
                "Project must already exist"));

        validationSupport.registerValidator(productOwnerTextField, Validator.createPredicateValidator(personValidation,
                "Person must already exist"));

        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue);
        });
    }

    private void setPrompts() {
        longNameTextField.setPromptText("Paddock");
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        descriptionTextField.setPromptText("Describe this backlog");
    }

    private void setupStoriesList() {
        storySelectionView.setSourceHeader(new Label("Stories Available:"));
        storySelectionView.setTargetHeader(new Label("Stories in Backlog"));

        storySelectionView.setPadding(new Insets(0, 0, 0, 0));

        storySelectionView.setCellFactories(view -> {
            final ListCell<Story> cell = new ListCell<Story>() {
                @Override
                public void updateItem(Story item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getShortName() : null);
                }
            };
            return cell;
        });
    }

    private void setStoryListSelectionViewData() {
        final ObservableList<Story> sourceStories = FXCollections.observableArrayList();

        if (project.get() != null) {
            sourceStories.addAll(project.get().getStories());
            if (backlog != null) {
                sourceStories.removeAll(backlog.getStories());
                targetStories.addAll(backlog.getStories());
            }

            storySelectionView.getSourceListView().setItems(sourceStories);
            storySelectionView.getTargetListView().setItems(targetStories);
        }

    }

    @Override
    public void populateFields(final Backlog backlog) {
        this.backlog = backlog;
        okButton.setText("Done");

        if (backlog == null) {
            // We are creating a new backlog
            stage.setTitle("Create Backlog");
        } else {
            // We are editing an existing backlog
            stage.setTitle("Edit Backlog");
            shortNameModified.set(true);

            shortNameTextField.setText(backlog.getShortName());
            longNameTextField.setText(backlog.getLongName());
            descriptionTextField.setText(backlog.getDescription());
            projectTextField.setText(backlog.getProject().getShortName());
            productOwnerTextField.setText(backlog.getProductOwner().getShortName());
        }
        setStoryListSelectionViewData();
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

    private void setCommand() {
        final ArrayList<Story> stories = new ArrayList<>();
        stories.addAll(targetStories);

        if (backlog == null) {
            final Backlog b = new Backlog(shortNameTextField.getText(), longNameTextField.getText(),
                    descriptionTextField.getText(), productOwner, project.get(), stories);
            command = new CreateBacklogCommand(b);
        } else {
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!longNameTextField.getText().equals(backlog.getLongName())) {
                changes.add(new EditCommand<>(backlog, "longName", longNameTextField.getText()));
            }
            if (!shortNameTextField.getText().equals(backlog.getShortName())) {
                changes.add(new EditCommand<>(backlog, "shortName", shortNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(backlog.getDescription())) {
                changes.add(new EditCommand<>(backlog, "description", descriptionTextField.getText()));
            }
            if (!project.get().equals(backlog.getProject())) {
                changes.add(new MoveItemCommand<>(backlog, backlog.getProject().observableBacklogs(),
                        project.get().observableBacklogs()));
                changes.add(new EditCommand<>(backlog, "project", project.get()));
            }
            if (!productOwner.equals(backlog.getProductOwner())) {
                changes.add(new EditCommand<>(backlog, "productOwner", productOwner));
            }

            // Stories being added to the backlog
            final ArrayList<Story> addedStories = new ArrayList(targetStories);
            addedStories.removeAll(backlog.getStories());

            // Stories being removed from the backlog
            final ArrayList<Story> removedStories = new ArrayList(backlog.getStories());
            removedStories.removeAll(targetStories);

            if (!addedStories.isEmpty()) {
                for (Story story : addedStories) {
                    changes.add(new MoveItemCommand<>(story, project.get().observableStories(),
                            backlog.observableStories()));
                }
            }
            if (!removedStories.isEmpty()) {
                for (Story story : removedStories) {
                    changes.add(new MoveItemCommand<>(story, backlog.observableStories(),
                            project.get().observableStories()));
                }
            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Backlog", changes);
        }
    }

    private boolean validate() {
        if (validationSupport.isInvalid()) {
            return false;
        } else {
            valid = true;
        }
        setCommand();
        return true;
    }

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

    private void setProjectTextFieldSuggester() {
        // use a callback to get an up-to-date project list, instead of just whatever exists at initialisation.
        // use a String converter so that the Project's short name is used.
        final AutoCompletionBinding<Project> binding = TextFields.bindAutoCompletion(projectTextField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Project>>() {
            @Override
            public Collection<Project> call(AutoCompletionBinding.ISuggestionRequest request) {
                // filter based on input string
                if(projectTextField.isFocused()) {
                    final Collection<Project> projects = organisation.getProjects().stream()
                            .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                            .collect(Collectors.toList());
                    return projects;
                } else {
                    return null;
                }

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

    private void setProductOwnerTextFieldSuggester() {
        // use a callback to get an up-to-date person list, instead of just whatever exists at initialisation.
        // use a String converter so that the Product Owner's short name is used.
        final AutoCompletionBinding<Person> binding = TextFields.bindAutoCompletion(productOwnerTextField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Person>>() {
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
                for (final Person productOwner: organisation.getPeople()) {
                    if (project.get().getShortName().equals(string)) {
                        return productOwner;
                    }
                }
                return null;
            }

            @Override
            public String toString(Person productOwner) {
                return productOwner.getShortName();
            }
        });

        productOwnerTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // forces suggestion list to show
                binding.setUserInput("");
            }
        });

    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command<?> getCommand() {
        return command;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        setupStoriesList();
    }



}

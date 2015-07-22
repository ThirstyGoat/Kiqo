package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.formControllers.FormController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
* Created by Carina Blair on 19/07/2015.
*/
public class BacklogFormViewModel extends FormController<Backlog> {
    private Backlog backlog;
    private Person productOwner;
    private Organisation organisation;
    private ObjectProperty<Project> projectProperty = new SimpleObjectProperty<>();
    private Command<?> command;
    private boolean valid = false;


    private StringProperty shortNameProperty = new SimpleStringProperty("");
    private StringProperty longNameProperty = new SimpleStringProperty("");
    private StringProperty descriptionProperty = new SimpleStringProperty("");
    private StringProperty productOwnerNameProperty = new SimpleStringProperty("");
    private StringProperty projectNameProperty = new SimpleStringProperty("");
    private ObjectProperty<Scale> scaleProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<Story>> targetStoriesProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<Story>> sourceStoriesProperty = new SimpleObjectProperty<>();


    public BacklogFormViewModel() {
    }

    /**
     * Validation for short name.
     * Checks that length of the shortName isn't 0 or greater than 20 and that it its unique.
     *
     * @return predicate for determining validity
     */

    public Predicate<String> getShortNameValidation() {
        return s -> {
            if (s.length() == 0|| s.length() > 20) {
                return false;
            }
            final Project project = projectProperty.get();
            if (project == null) {
                return true;
            }
            return Utilities.shortnameIsUnique(s, null, project.getBacklogs());
        };
    }

    /**
     * Validation for long name
     * Checks that the long name isn't empty
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getLongNameValidation() {
        return s -> {
            return s != null && !s.isEmpty();
        };
    }

    /**
     * Validation for description
     * Always valid as description isn't required and has no constraints
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getDescriptionValidation() {
        return s -> {
            // always valid
            return true;
        };
    }

    /**
     * Validation for product owner
     * Checks that the person exists within the organisation and has the PO skill
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getProductOwnerValidation() {
        return s -> {
            for (final Person p : organisation.getPeople()) {
                if (p.getSkills().contains(organisation.getPoSkill())) {
                    productOwner = p;
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Validation for project
     * Checks that the project exists and is set
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getProjectValidation() {
        return s -> {
            return projectProperty.get() != null;
        };
    }

    public Predicate<Scale> getScaleValidation() {
        return Utilities.createEmptyValidation();
    }

    public StringProperty shortNameProperty() { return shortNameProperty; }

    public StringProperty longNameProperty() {
        return longNameProperty;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public StringProperty productOwnerNameProperty() {return productOwnerNameProperty; }

    public StringProperty projectNameProperty() {
        return projectNameProperty;
    }

    public ObjectProperty<Scale> scaleProperty() { return scaleProperty; }

    public ObjectProperty<ObservableList<Story>> targetStoriesProperty() { return targetStoriesProperty;}

    public  ObjectProperty<ObservableList<Story>> sourceStoriesProperty() { return sourceStoriesProperty;}

    private void setStoryListProperties() {
        if (projectProperty.get() != null) {
            sourceStoriesProperty.get().addAll(projectProperty.get().getUnallocatedStories());
            if (backlog != null) {
                sourceStoriesProperty.get().removeAll(backlog.getStories());
                targetStoriesProperty.get().addAll(backlog.getStories());
            }
        }
    }

    private void setListeners() {
        projectProperty.addListener(((observable, oldValue, newValue) -> {
            targetStoriesProperty.get().clear();
            sourceStoriesProperty.get().clear();
            setStoryListProperties();
        }));
    }

    private void reloadFromModel() {
        targetStoriesProperty.set(FXCollections.observableArrayList());
        sourceStoriesProperty.set(FXCollections.observableArrayList());

        if (backlog != null) {
            // We are editing an existing backlog
            shortNameProperty.set(backlog.getShortName());
            longNameProperty.set(backlog.getLongName());
            descriptionProperty.set(backlog.getDescription());
            productOwnerNameProperty.set(backlog.getProductOwner().getShortName());
            scaleProperty.setValue(backlog.getScale());

            if (backlog.getProject() != null) {
                projectProperty.set(backlog.getProject());
            }
        }

        setStoryListProperties();
        setListeners();
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
        reloadFromModel();
    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        projectNameProperty.bindBidirectional(projectProperty, StringConverters.projectStringConverter(organisation));


    }

    @Override
    public void populateFields(Backlog backlog) {

    }

    public List<Story> getConflictingScales() {
    // add any story without the same scale to the array
    List<Story> conflicts = new ArrayList<>();

    for (Story story : targetStoriesProperty.get()) {
        if (story.getScale() != scaleProperty().get()) {
            conflicts.add(story);
        }
    }
        return conflicts;
    }

    @Override
    public Command<?> getCommand() { return command; }

    public void setCommand() {
        final ArrayList<Story> stories = new ArrayList<>();
        stories.addAll(targetStoriesProperty.get());

        if (backlog == null) {
            final Backlog backlog = new Backlog(shortNameProperty.getValue(), longNameProperty.getValue(),
                    descriptionProperty.getValue(), productOwner, projectProperty.get(), stories, scaleProperty.getValue());
            command = new CreateBacklogCommand(backlog);
        } else {
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!longNameProperty.getValue().equals(backlog.getLongName())) {
                changes.add(new EditCommand<>(backlog, "longName", longNameProperty.getValue()));
            }
            if (!shortNameProperty.getValue().equals(backlog.getShortName())) {
                changes.add(new EditCommand<>(backlog, "shortName", shortNameProperty.getValue()));
            }
            if (!descriptionProperty.getValue().equals(backlog.getDescription())) {
                changes.add(new EditCommand<>(backlog, "description", descriptionProperty.getValue()));
            }

            if (!productOwner.equals(backlog.getProductOwner())) {
                changes.add(new EditCommand<>(backlog, "productOwner", productOwner));
            }

            if (scaleProperty.getValue() != backlog.getScale()) {
                changes.add(new EditCommand<>(backlog, "scale", scaleProperty.getValue()));
            }

            // Stories being added to the backlog
            final ArrayList<Story> addedStories = new ArrayList<>(targetStoriesProperty.get());
            addedStories.removeAll(backlog.getStories());

            // Stories being removed from the backlog
            final ArrayList<Story> removedStories = new ArrayList<>(backlog.getStories());
            removedStories.removeAll(targetStoriesProperty.get());

            for (Story story : addedStories) {
                if (story.getScale() != scaleProperty().get()) {
                    changes.add(new EditCommand<>(story, "estimate", 0));
                    changes.add(new EditCommand<>(story, "scale", scaleProperty().get()));
                }
                changes.add(new MoveItemCommand<>(story, projectProperty.get().observableUnallocatedStories(),
                        backlog.observableStories()));
                changes.add(new EditCommand<>(story, "backlog", backlog));
            }
            // get the remaining stories and change their scales - might be a better way to do this rather than 2 loops
            for (Story story : backlog.getStories()) {
                changes.add(new EditCommand<>(story, "estimate", 0));
                changes.add(new EditCommand<>(story, "scale", scaleProperty().get()));
            }

            if (!projectProperty.get().equals(backlog.getProject())) {
                changes.add(new MoveItemCommand<>(backlog, backlog.getProject().observableBacklogs(),
                        projectProperty.get().observableBacklogs()));
                changes.add(new EditCommand<>(backlog, "project", projectProperty.get()));
                // If backlog moved to a different project we need to update the back references of the stories
                // in that backlog.
                for (Story story : backlog.observableStories()) {
                    changes.add(new EditCommand<>(story, "project", projectProperty.get()));
                    changes.add(new EditCommand<>(story, "backlog", backlog));
                }
            }

            for (Story story : removedStories) {
                changes.add(new MoveItemCommand<>(story, backlog.observableStories(),
                        projectProperty.get().observableUnallocatedStories()));
                changes.add(new EditCommand<>(story, "backlog", null));
            }

            valid = !changes.isEmpty();

            command = new CompoundCommand("Edit Backlog", changes);
        }
    }

    @Override
    public boolean isValid() { return valid; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


}

package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.formControllers.FormController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Created by samschofield on 16/07/15.
 */
public class StoryFormViewModel extends FormController<Story> {
    private Story story;
    private Person creator;
    private Project project;
    private Backlog backlog;
    private Organisation organisation;
    private Command<?> command;
    private boolean valid = false;

    private StringProperty storyShortName = new SimpleStringProperty();
    private StringProperty storyLongName = new SimpleStringProperty();
    private StringProperty storyDescription = new SimpleStringProperty();
    private StringProperty storyCreator = new SimpleStringProperty();
    private StringProperty storyPriority = new SimpleStringProperty();
    private StringProperty projectName = new SimpleStringProperty();

    private ObjectProperty<Scale> storyEstimate = new SimpleObjectProperty<>();



    // Validation for short name
    // checks that length of the shortName isn't 0 and that it its unique
        public Predicate<String> getShortNameValidation() {
            return s -> {
                if (s.length() == 0) {
                    return false;
                }
                if (project == null) {
                    return true;
                }
                Collection<Collection<? extends Item>> existingBacklogs = new ArrayList<>();
                existingBacklogs.add(project.getUnallocatedStories());
                existingBacklogs.addAll(project.getBacklogs().stream().map(Backlog::observableStories).collect(Collectors.toList()));

                return Utilities.shortnameIsUniqueMultiple(storyShortName.get(), story, existingBacklogs);
            };
        }

        public Predicate<String> getPersonValidation() {
            return s -> {
                for (final Person p : organisation.getPeople()) {
                    if (p.getShortName().equals(s)) {
                        creator = p;
                        return true;
                    }
                }
                return false;
            };
        }

        public Predicate<String> getProjectValidation() {
            return s -> {
                for (final Project p : organisation.getProjects()) {
                    if (p.getShortName().equals(projectName.get())) {
                        project = p;
                        // Redo validation for shortname text field
                        final String snt = storyShortName.get();
                        storyShortName.setValue("");
                        storyShortName.setValue(snt);
                        return true;
                    }
                }
                return false;
            };
        }

        public Predicate<String> getPriorityValidation() {
            return s -> {
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
        }

    public StringProperty storyShortNameProperty() {
        return storyShortName;
    }

    public StringProperty storyLongNameProperty() {
        return storyLongName;
    }

    public StringProperty storyDescriptionProperty() {
        return storyDescription;
    }

    public StringProperty storyPriorityProperty() {
        return storyPriority;
    }

    public StringProperty projectNameProperty() {
        return projectName;
    }

    public ObjectProperty<Scale> storyEstimateProperty() {
        return storyEstimate;
    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;

    }

    @Override
    public void populateFields(Story story) {

    }

    @Override
    public Command<?> getCommand() { return command; }

    public void setCommand() {
        if (story == null) {
            // new story command
            story = new Story(storyShortName.getValue(), storyLongName.getValue(), storyDescription.getValue(), creator,
                    project, backlog, Integer.parseInt(storyPriority.getValue()), 0, storyEstimate.getValue());
            command = new CreateStoryCommand(story);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!storyLongName.getValue().equals(story.getLongName())) {
                changes.add(new EditCommand<>(story, "longName", storyLongName.getValue()));
            }
            if (!storyShortName.getValue().equals(story.getShortName())) {
                changes.add(new EditCommand<>(story, "shortName", storyShortName.getValue()));
            }
            if (!storyDescription.getValue().equals(story.getDescription())) {
                changes.add(new EditCommand<>(story, "description", storyDescription.getValue()));
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

            if (Integer.parseInt(storyPriority.getValue()) != story.getPriority()) {
                changes.add(new EditCommand<>(story, "priority", Integer.parseInt(storyPriority.getValue())));
            }

            if (storyEstimate.getValue() != story.getScale()) {
                changes.add(new EditCommand<>(story, "scale", storyEstimate.getValue()));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Release", changes);
        }
    }

    @Override
    public boolean isValid() { return valid; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

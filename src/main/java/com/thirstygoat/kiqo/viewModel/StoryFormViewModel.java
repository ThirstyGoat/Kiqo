package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.formControllers.FormController;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Created by samschofield on 16/07/15.
 */
public class StoryFormViewModel extends FormController<Story> {
    private Story story;
    private Person creator;
    private ObjectProperty<Project> projectProperty = new SimpleObjectProperty<>();
    private Backlog backlog; // not a property because not editable from form
    private Organisation organisation;
    private Command<?> command;
    private boolean valid = false;

    private StringProperty shortNameProperty = new SimpleStringProperty("");
    private StringProperty longNameProperty = new SimpleStringProperty("");
    private StringProperty descriptionProperty = new SimpleStringProperty("");
    private StringProperty creatorNameProperty = new SimpleStringProperty("");
    private StringProperty projectNameProperty = new SimpleStringProperty("");
    private StringProperty priorityProperty = new SimpleStringProperty("");
    private ObjectProperty<Scale> scaleProperty = new SimpleObjectProperty<>();
    private IntegerProperty estimateProperty = new SimpleIntegerProperty();
    private ObjectProperty<ObservableList<Story>> targetStoriesProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<Story>> sourceStoriesProperty = new SimpleObjectProperty<>();

    private BooleanProperty creatorEditable = new SimpleBooleanProperty(true);

    public StoryFormViewModel() {
        projectNameProperty.bindBidirectional(projectProperty, new StringConverter<Project>() {
            @Override
            public Project fromString(String shortName) {
                for (final Project p : organisation.getProjects()) {
                    if (p.getShortName().equals(shortName)) {
                        return p;
                    }
                }
                return null;
            }

            @Override
            public String toString(Project project) {
                return project != null ? project.getShortName() : "";
            }
        });

        projectProperty.addListener((observable, oldValue, newValue) -> {
            setStoryListProperties();
        });
    }
    
    private void setStoryListProperties() {
        targetStoriesProperty.get().clear();
        sourceStoriesProperty.get().clear();
        if (story != null) {
            if (projectProperty.get() != null) {
                targetStoriesProperty.get().addAll(story.getDependencies());
                if (story.getBacklog() != null) {
                    sourceStoriesProperty.get().addAll(story.getBacklog().getStories());
                } else {
                    sourceStoriesProperty.get().addAll(projectProperty.get().getUnallocatedStories()); 
                }
            }
            sourceStoriesProperty.get().removeAll(story.getDependencies());
            sourceStoriesProperty.get().remove(story); // cannot depend on itself
        } else {
            if (projectProperty.get() != null) {
                sourceStoriesProperty.get().addAll(projectProperty.get().getUnallocatedStories());
            }
        }
    }

    /**
     * Sets all properties to be that of model. So for example if you change the story using,
     * setStory(), and you want to update the text fields with the new stories data, then you
     * should call this method.
     */
    private void reloadFromModel() {
        targetStoriesProperty.set(FXCollections.observableArrayList());
        sourceStoriesProperty.set(FXCollections.observableArrayList());
        
        if (story != null) {
            shortNameProperty.set(story.getShortName());
            longNameProperty.set(story.getLongName());
            descriptionProperty.set(story.getDescription());
            creatorNameProperty.set(story.getCreator().getShortName());
            projectNameProperty.set(story.getProject().getShortName());
            priorityProperty.set(Integer.toString(story.getPriority()));
            scaleProperty.set(story.getScale());
            estimateProperty.set(story.getEstimate());
            backlog = story.getBacklog();

            creatorEditable.set(false);
        }

        setStoryListProperties();
        setListeners();
    }

    private void setListeners() {
        projectProperty.addListener(((observable, oldValue, newValue) -> {
            setStoryListProperties();
        }));
    }

    /** 
     * Validation for short name.
     * Checks that length of the shortName isn't 0 or greater than 20 and that it its unique.
     * 
     * @return predicate for determining validity
     */
    public Predicate<String> getShortNameValidation() {
        return s -> {
            if (s.length() == 0 || s.length() > 20) {
                return false;
            }

            final Project project = projectProperty.get();
            if (project == null) {
                return true;
            } else {
                Collection<Collection<? extends Item>> existingStories = new ArrayList<>();
                existingStories.add(project.getUnallocatedStories());
                existingStories.addAll(project.getBacklogs().stream().map(Backlog::observableStories).collect(Collectors.toList()));
    
                return Utilities.shortnameIsUniqueMultiple(s, story, existingStories);
            }
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
     * Validation for creator
     * Checks that the creator exists within the organisation and is set
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getCreatorValidation() {
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

    /**
     * Validation for project
     * Checks that the project exists and is set
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getProjectValidation() {
        return s -> {
            // Force re-validation for shortname
            final String snt = shortNameProperty.get();
            shortNameProperty.setValue("");
            shortNameProperty.setValue(snt);
            
            return projectProperty.get() != null;
        };
    }

    /**
     * Validation for backlog
     *
     *
     * @return predicate for determining validity
     */
    public Predicate<String> getBacklogValidation() {
        return s -> {
            // TODO implement validation
            return false;
        };
    }

    /**
     * Validation for priority
     *
     *
     * @return predicate for determining validity
     */
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

    public StringProperty shortNameProperty() {
        return shortNameProperty;
    }

    public StringProperty longNameProperty() {
        return longNameProperty;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }
    
    public StringProperty creatorNameProperty() {
        return creatorNameProperty;
    }

    public StringProperty projectNameProperty() {
        return projectNameProperty;
    }
    
    public ObjectProperty<Project> projectProperty() {
        return projectProperty;
    }

    public StringProperty priorityProperty() {
        return priorityProperty;
    }

    public ObjectProperty<Scale> scaleProperty() {
        return scaleProperty;
    }

    public IntegerProperty estimateProperty() {
        return estimateProperty;
    }
    
    public ObjectProperty<ObservableList<Story>> targetStoriesProperty() { 
        return targetStoriesProperty;
    }

    public  ObjectProperty<ObservableList<Story>> sourceStoriesProperty() { 
        return sourceStoriesProperty;
    }

    public BooleanProperty getCreatorEditable () {
        return creatorEditable;
    }

    public void setStory(Story story) {
        this.story = story;
        reloadFromModel();
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
            story = new Story(shortNameProperty.getValue(), longNameProperty.getValue(), descriptionProperty.getValue(), creator,
                    projectProperty.get(), backlog, Integer.parseInt(priorityProperty.getValue()), scaleProperty.getValue(), estimateProperty.getValue(), targetStoriesProperty.get());
            command = new CreateStoryCommand(story);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!longNameProperty.getValue().equals(story.getLongName())) {
                changes.add(new EditCommand<>(story, "longName", longNameProperty.getValue()));
            }
            if (!shortNameProperty.getValue().equals(story.getShortName())) {
                changes.add(new EditCommand<>(story, "shortName", shortNameProperty.getValue()));
            }
            if (!descriptionProperty.getValue().equals(story.getDescription())) {
                changes.add(new EditCommand<>(story, "description", descriptionProperty.getValue()));
            }
            // creator can't be changed
            
            if (!projectProperty.get().equals(story.getProject())) {
                if (story.getBacklog() != null) {
                    changes.add(new MoveItemCommand<>(story, story.getBacklog().observableStories(), projectProperty.get().observableUnallocatedStories()));
                } else {
                    changes.add(new MoveItemCommand<>(story, story.getProject().observableUnallocatedStories(), projectProperty.get().observableUnallocatedStories()));
                }
                // If story is changing projects, then it shouldn't be in any backlog
                changes.add(new EditCommand<>(story, "backlog", null));
                changes.add(new EditCommand<>(story, "project", projectProperty.get()));
            }

            if (Integer.parseInt(priorityProperty.getValue()) != story.getPriority()) {
                changes.add(new EditCommand<>(story, "priority", Integer.parseInt(priorityProperty.getValue())));
            }

            if (scaleProperty.getValue() != story.getScale()) {
                changes.add(new EditCommand<>(story, "scale", scaleProperty.getValue()));
            }
            
//            // Stories being added as dependencies
//            final ArrayList<Story> addedStories = new ArrayList<>(targetStoriesProperty.get());
//            addedStories.removeAll(story.getDependencies());
//
//            // Stories being removed as dependencies
//            final ArrayList<Story> removedStories = new ArrayList<>(story.getDependencies());
//            removedStories.removeAll(targetStoriesProperty.get());

            if (!(targetStoriesProperty.get().containsAll(story.getDependencies())
                    && story.getDependencies().containsAll(targetStoriesProperty.get()))) {
                changes.add(new EditCommand<>(story, "dependencies", targetStoriesProperty.get()));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Release", changes);
        }
    }

    /**
     * @return if the story being created has any cyclic dependencies
     */
    public boolean hasCyclicDependency() {
        for (Story dependency : targetStoriesProperty.get()) {
            if (checkCyclicDependency(dependency)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a depth first search on the given dependency to see if it can reach itself
     * No need to worry about cycles up further up the graph because we check for cycles before they can be added
     * @param dependency the dependency to check
     * @return boolean true if a cycle has been found
     */
    private boolean checkCyclicDependency(Story dependency) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node(dependency));

        while (!stack.empty()) {
            Node n = stack.pop();
            if (n.label.equals(story.getShortName())) {
                return true;
            }
            if (!n.visited) {
                n.visited = true;
                for (Story d : n.dependencies) {
                    stack.push(new Node(d));
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid() { return valid; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private class Node {
        public String label;
        public boolean visited;
        public List<Story> dependencies;

        public Node(Story dependency) {
            this.label = dependency.getShortName();
            this.dependencies = dependency.getDependencies();
            this.visited = false;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "label='" + label + '\'' +
                    '}';
        }
    }
}

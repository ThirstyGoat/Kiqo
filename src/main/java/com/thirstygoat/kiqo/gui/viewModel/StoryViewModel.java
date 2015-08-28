package com.thirstygoat.kiqo.gui.viewModel;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.create.CreateStoryCommand;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;


/**
 * Created by samschofield on 16/07/15.
 */
public class StoryViewModel implements ViewModel {
    private ModelWrapper<Story> storyWrapper = new GoatModelWrapper<>();

    private Organisation organisation;
    private Command command;
    private Story story;
    private boolean valid = false;

    private ObjectProperty<Organisation> organisationProperty = new SimpleObjectProperty<>();
    private ListProperty<Story> dependenciesProperty = new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));
    private ListProperty<Story> eligibleDependencies = new SimpleListProperty<>();

    private BooleanProperty creatorEditable = new SimpleBooleanProperty(true);

    private ObservableRuleBasedValidator shortNameValidator;
    private FunctionBasedValidator longNameValidator;
    private FunctionBasedValidator descriptionValidator;
    private FunctionBasedValidator creatorValidator;
    private FunctionBasedValidator projectValidator;
    private FunctionBasedValidator priorityValidator;
    private FunctionBasedValidator scaleValidator;
    private CompositeValidator allValidator;

    public StoryViewModel() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() ->
            {
                List<Story> stories = new ArrayList();
                if (projectProperty().get() != null) {
                    stories.addAll(projectProperty().get().getUnallocatedStories());
                    for (Backlog backlog : projectProperty().get().getBacklogs()) {
                        stories.addAll(backlog.getStories());
                    }
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), story, stories);
                } else {
                    return true; // no project means this isn't for real yet.
                }
            },
            shortNameProperty(), projectProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within project"));

        longNameValidator = new FunctionBasedValidator<>(longNameProperty(),
            Utilities.emptinessPredicate(),
            ValidationMessage.error("Name must not be empty."));

        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty(),
            // Always valid as description isn't required and has no constraints
            s -> true,
            ValidationMessage.error(""));

        creatorValidator = new FunctionBasedValidator<>(creatorProperty(),
            // Checks that the creator exists within the organisation and is set
            s -> {
                if (organisationProperty().get() != null) {
                    for (final Person p : organisationProperty().get().getPeople()) {
                        if (p.getShortName().equals(s.getShortName())) {
                            return true;
                        }
                    }
                }
                return false;
            },
            ValidationMessage.error("Person must already exist"));

        projectValidator = new FunctionBasedValidator<>(projectProperty(),
            // Checks that the project exists and is set
            Utilities.emptinessPredicate(),
            ValidationMessage.error("Project must already exist"));

        priorityValidator = new FunctionBasedValidator<>(priorityProperty(),
            i -> {
                try {
                    if (i.intValue() < Story.MIN_PRIORITY || i.intValue() > Story.MAX_PRIORITY) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            },
            ValidationMessage.error("Priority must be an integer between "
                + Story.MIN_PRIORITY + " and " + Story.MAX_PRIORITY));

        scaleValidator = new FunctionBasedValidator<>(scaleProperty(),
            Utilities.emptinessPredicate(),
            ValidationMessage.error("Estimation Scale must not be empty"));

        allValidator = new CompositeValidator();
        allValidator.addValidators(shortNameValidator, longNameValidator, descriptionValidator, creatorValidator,
                projectValidator, priorityValidator, scaleValidator);
    }
    
    private void setStoryListProperties() {
        dependenciesProperty.get().clear();
        eligibleDependencies.get().clear();
        if (storyWrapper.get() != null) {
            if (projectProperty() != null) {
                dependenciesProperty.get().addAll(storyWrapper.get().getDependencies());
                if (storyWrapper.get().getBacklog() != null) {
                    eligibleDependencies.get().addAll(storyWrapper.get().getBacklog().getStories());
                } else {
                    eligibleDependencies.get().addAll(projectProperty().get().getUnallocatedStories());
                }

                ArrayList<Story> toRemove = new ArrayList<>();
                for (Story story : eligibleDependencies.get()) {
                    if (checkCyclicDependency(story)) {
                        toRemove.add(story);
                    }
                }
                eligibleDependencies.get().removeAll(toRemove);
            }
            eligibleDependencies.get().removeAll(storyWrapper.get().getDependencies());
            eligibleDependencies.get().remove(storyWrapper.get()); // cannot depend on itself
        } else {
            if (projectProperty() != null) {
                eligibleDependencies.get().addAll(projectProperty().get().getUnallocatedStories());
            }
        }
    }

    private void setListeners() {
        projectProperty().addListener(((observable, oldValue, newValue) -> {
            setStoryListProperties();
        }));
    }

    public void load(Story story, Organisation organisation) {
        organisationProperty().set(organisation);
        if(story != null) {
            storyWrapper.set(story);
            this.story = story;
            dependenciesProperty.setAll(story.getDependencies());
        } else {
            storyWrapper.set(new Story());
            storyWrapper.reset();
            storyWrapper.commit();
            dependenciesProperty.clear();
        }
        storyWrapper.reload();
    }

    public void reload() {
        storyWrapper.reload();
        dependenciesProperty.setAll(story.getDependencies());
    }

    public Supplier<List<Project>> projectSupplier() {
        return () -> organisationProperty().get().getProjects();
    }

    public Supplier<List<Person>> creatorSupplier() {
        return () -> organisationProperty().get().getPeople();
    }

    public StringProperty shortNameProperty() {
        return storyWrapper.field("shortName", Story::getShortName, Story::setShortName, "");
    }

    public StringProperty longNameProperty() {
        return storyWrapper.field("longName", Story::getLongName, Story::setLongName, "");
    }

    public StringProperty descriptionProperty() {
        return storyWrapper.field("description", Story::getDescription, Story::setDescription, "");
    }

    public ObjectProperty<Person> creatorProperty() {
        return storyWrapper.field("creator", Story::getCreator, Story::setCreator, null);
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return storyWrapper.field("backlog", Story::getBacklog, Story::setBacklog, null);
    }

    public ObjectProperty<Project> projectProperty() {
        return storyWrapper.field("project", Story::getProject, Story::setProject, null);
    }

    public IntegerProperty priorityProperty() {
        return storyWrapper.field("priority", Story::getPriority, Story::setPriority, 0);
    }

    public ObjectProperty<Scale> scaleProperty() {
        return storyWrapper.field("scale", Story::getScale, Story::setScale, Scale.FIBONACCI );
    }

    public IntegerProperty estimateProperty() {
        return storyWrapper.field("estimate", Story::getEstimate, Story::setEstimate, 0);
    }

    protected Story getStory() {
        return story;
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }

    public ListProperty<Story> dependenciesProperty() {
        return dependenciesProperty;
    }

    public  ListProperty<Story> eligibleDependencies() {
        return eligibleDependencies;
    }

    public BooleanProperty getCreatorEditable () {
        return creatorEditable;
    }

    public ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }

    public ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
    }

    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus creatorValidation() {
        return creatorValidator.getValidationStatus();
    }

    public ValidationStatus projectValidation() {
        return projectValidator.getValidationStatus();
    }

    public ValidationStatus priorityValidation() {
        return priorityValidator.getValidationStatus();
    }

    public ValidationStatus scaleValidation() {
        return scaleValidator.getValidationStatus();
    }

    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }

    public Command getCommand() {
        if (story == null) {
            // new story command
            Story story = new Story(shortNameProperty().getValue(), longNameProperty().getValue(), descriptionProperty().getValue(), creatorProperty().get(),
                    projectProperty().get(), null, priorityProperty().getValue(), scaleProperty().getValue(), estimateProperty().getValue(), false, false);
            command = new CreateStoryCommand(story);
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            if (!longNameProperty().getValue().equals(story.getLongName())) {
                changes.add(new EditCommand<>(story, "longName", longNameProperty().getValue()));
            }
            if (!shortNameProperty().getValue().equals(story.getShortName())) {
                changes.add(new EditCommand<>(story, "shortName", shortNameProperty().getValue()));
            }
            if (!descriptionProperty().getValue().equals(story.getDescription())) {
                changes.add(new EditCommand<>(story, "description", descriptionProperty().getValue()));
            }
            // creator can't be changed
            
            if (!projectProperty().get().equals(story.getProject())) {
                if (story.getBacklog() != null) {
                    changes.add(new MoveItemCommand<>(story, story.getBacklog().observableStories(), projectProperty().get().observableUnallocatedStories()));
                } else {
                    changes.add(new MoveItemCommand<>(story, story.getProject().observableUnallocatedStories(), projectProperty().get().observableUnallocatedStories()));
                }
                // If story is changing projects, then it shouldn't be in any backlog
                changes.add(new EditCommand<>(story, "backlog", null));
                changes.add(new EditCommand<>(story, "project", projectProperty().get()));
            }

            if (priorityProperty().getValue() != story.getPriority()) {
                changes.add(new EditCommand<>(story, "priority",priorityProperty().getValue()));
            }

            if (scaleProperty().getValue() != story.getScale()) {
                changes.add(new EditCommand<>(story, "scale", scaleProperty().getValue()));
            }

            if (!(dependenciesProperty.get().containsAll(story.getDependencies())
                    && story.getDependencies().containsAll(dependenciesProperty.get()))) {
                changes.add(new EditCommand<>(story, "dependencies", dependenciesProperty.get()));
            }

            // So that the table view gets update we need to remove a story from the observable lists
            // it is in and then re-add it. Checking if project and backlog are null to avoid null
            // pointer exception.
            if (story.getProject() != null && story.getProject().getUnallocatedStories().contains(story)) {
                story.getProject().getUnallocatedStories().remove(story);
                story.getProject().getUnallocatedStories().add(story);
            } else if (story.getBacklog() != null && story.getBacklog().getStories().contains(story)) {
                story.getBacklog().getStories().remove(story);
                story.getBacklog().getStories().add(story);
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Story", changes);
        }
        return command;
    }

    /**
     * @return if the story being created has any cyclic dependencies
     */
    public boolean hasCyclicDependency() {
        for (Story dependency : dependenciesProperty.get()) {
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

package com.thirstygoat.kiqo.gui.viewModel;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.create.CreateStoryCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by samschofield on 16/07/15.
 */
public class StoryViewModel extends ModelViewModel<Story> {
    private Command command;

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
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> {
                List<Story> stories = new ArrayList();
                if (projectProperty().get() != null) {
                    stories.addAll(projectProperty().get().getUnallocatedStories());
                    for (Backlog backlog : projectProperty().get().getBacklogs()) {
                        stories.addAll(backlog.getStories());
                    }
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(), stories);
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
                if (s == null) return false;
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

    @Override
    protected Supplier<Story> modelSupplier() {
        return Story::new;
    }

    @Override
    public void afterLoad() {
        // Don't need to do anything
    }

    public Supplier<List<Project>> projectSupplier() {
        return () -> organisationProperty().get().getProjects();
    }

    public Supplier<List<Person>> creatorSupplier() {
        return () -> organisationProperty().get().getPeople();
    }

    /** Fields **/

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Story::getShortName, Story::setShortName, "");
    }

    public StringProperty longNameProperty() {
        return modelWrapper.field("longName", Story::getLongName, Story::setLongName, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Story::getDescription, Story::setDescription, "");
    }

    public ObjectProperty<Person> creatorProperty() {
        return modelWrapper.field("creator", Story::getCreator, Story::setCreator, null);
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return modelWrapper.field("backlog", Story::getBacklog, Story::setBacklog, null);
    }

    public ObjectProperty<Project> projectProperty() {
        return modelWrapper.field("project", Story::getProject, Story::setProject, null);
    }

    public IntegerProperty priorityProperty() {
        return modelWrapper.field("priority", Story::getPriority, Story::setPriority, 0);
    }

    public ObjectProperty<Scale> scaleProperty() {
        return modelWrapper.field("scale", Story::getScale, Story::setScale, Scale.FIBONACCI );
    }

    public IntegerProperty estimateProperty() {
        return modelWrapper.field("estimate", Story::getEstimate, Story::setEstimate, 0);
    }

    public ListProperty<Story> dependenciesProperty() {
        return modelWrapper.field("dependencies", Story::getDependencies, Story::setDependencies, new ArrayList<Story>());
    }

    /** Other Fields **/

    public  ListProperty<Story> eligibleDependencies() {
        ListProperty<Story> eligibleDependencies = new SimpleListProperty<>(FXCollections.observableArrayList());
        Supplier<List<Story>> eligibleDependenciesSupplier = () -> {
            if (projectProperty().get() == null) {
                return new ArrayList<Story>();
            } else {
                Stream<Story> unallocatedStories = projectProperty().get().getUnallocatedStories().stream();
                Stream<Story> storiesInBacklog = projectProperty().get().getBacklogs().stream()
                                .flatMap(backlog -> backlog.getStories().stream());
                return Stream.concat(unallocatedStories, storiesInBacklog)
                        .filter(story -> !createsCycle(story))
                        .collect(Collectors.toList());
            }
        };

        eligibleDependencies.setAll(eligibleDependenciesSupplier.get());
        projectProperty().addListener(change -> {
            eligibleDependencies.setAll(eligibleDependenciesSupplier.get());
        });

        return eligibleDependencies;
    }

    public BooleanProperty getCreatorEditable () {
        return creatorEditable;
    }
    
    /** Validation **/

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

    @Override
    public Command getCommand() {

        if (!allValidation().isValid()) {
            LOGGER.log(Level.WARNING, "Fields are invalid, no command will be returned.");
            return null;
        } else if (!modelWrapper.isDirty()) {
            LOGGER.log(Level.WARNING, "Nothing changed. No command will be returned");
            return null;
        }

        if (modelWrapper.get().getShortName() == "") { // Must be a new story
            Story story = new Story(shortNameProperty().getValue(), longNameProperty().getValue(), descriptionProperty().getValue(), creatorProperty().get(),
                    projectProperty().get(), null, priorityProperty().getValue(), scaleProperty().getValue(), estimateProperty().getValue(), false, false);
            command = new CreateStoryCommand(story);
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            addEditCommands.accept(changes);

            if (!projectProperty().get().equals(modelWrapper.get().getProject())) {
                if (modelWrapper.get().getBacklog() != null) {
                    changes.add(new MoveItemCommand<>(modelWrapper.get(), modelWrapper.get().getBacklog().observableStories(), projectProperty().get().observableUnallocatedStories()));
                } else {
                    changes.add(new MoveItemCommand<>(modelWrapper.get(), modelWrapper.get().getProject().observableUnallocatedStories(), projectProperty().get().observableUnallocatedStories()));
                }
                // If story is changing projects, then it shouldn't be in any backlog
                changes.add(new EditCommand<>(modelWrapper.get(), "backlog", null));
                changes.add(new EditCommand<>(modelWrapper.get(), "project", projectProperty().get()));
            }

            if (!(dependenciesProperty().get().containsAll(modelWrapper.get().getDependencies())
                    && modelWrapper.get().getDependencies().containsAll(dependenciesProperty().get()))) {
                changes.add(new EditCommand<>(modelWrapper.get(), "dependencies", dependenciesProperty().get()));
            }

            // So that the table view gets update we need to remove a story from the observable lists
            // it is in and then re-add it. Checking if project and backlog are null to avoid null
            // pointer exception.
            if (modelWrapper.get().getProject() != null && modelWrapper.get().getProject().getUnallocatedStories().contains(modelWrapper.get())) {
                modelWrapper.get().getProject().getUnallocatedStories().remove(modelWrapper.get());
                modelWrapper.get().getProject().getUnallocatedStories().add(modelWrapper.get());
            } else if (modelWrapper.get().getBacklog() != null && modelWrapper.get().getBacklog().getStories().contains(modelWrapper.get())) {
                modelWrapper.get().getBacklog().getStories().remove(modelWrapper.get());
                modelWrapper.get().getBacklog().getStories().add(modelWrapper.get());
            }
            command = new CompoundCommand("Edit Story", changes);
        }
        return command;
    }

    /**
     * @return if the story being created has any cyclic dependencies
     */
    public boolean hasCyclicDependency() {
        for (Story dependency : dependenciesProperty().get()) {
            if (createsCycle(dependency)) {
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
    private boolean createsCycle(Story dependency) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node(dependency));

        while (!stack.empty()) {
            Node n = stack.pop();
            if (n.label.equals(modelWrapper.get().getShortName())) {
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

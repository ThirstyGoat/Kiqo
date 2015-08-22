package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.command.create.CreateBacklogCommand;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatCollectors;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Model ViewModel
 * @author amy
 *
 */
public class BacklogViewModel implements ViewModel {
    private final ObjectProperty<Organisation> organisationProperty;

    private GoatModelWrapper<Backlog> backlogWrapper = new GoatModelWrapper<>();
    private final ListProperty<Story> stories;

    private final ListProperty<Story> eligableStories;

    private final ObservableRuleBasedValidator shortNameValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<String> descriptionValidator;
    private final FunctionBasedValidator<Person> productOwnerValidator;
    private final FunctionBasedValidator<Project> projectValidator;
    private final CompositeValidator allValidator;

    public BacklogViewModel() {
        organisationProperty = new SimpleObjectProperty<>();
        stories = new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));

        eligableStories = new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));
        // Update list of eligible stories when project changes.
        projectProperty().addListener((observable -> {
            // If project is changed to original project, then add the original stories back again
            if (projectProperty().get() != null
                    && projectProperty().get().getShortName().equals(backlogWrapper.get().getProject().getShortName())) {
                stories().setAll(backlogWrapper.get().getStories());
            }
            eligableStories.setAll(storySupplier().get());
        }));
        
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding rule1 = shortNameProperty().isNotNull();
        BooleanBinding rule2 = shortNameProperty().length().greaterThan(0);
        BooleanBinding rule3 = shortNameProperty().length().lessThan(20);
        BooleanBinding rule4 = Bindings.createBooleanBinding(
                () -> {
                    if (projectProperty().get() == null) {
                        return true;
                    }
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), backlogWrapper.get(),
                            projectProperty().get().getBacklogs());
                },
                shortNameProperty(), projectProperty());
        shortNameValidator.addRule(rule1, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule2, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule3, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule4, ValidationMessage.error("Short name must be unique and not empty"));

        longNameValidator = new FunctionBasedValidator<>(longNameProperty(),
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Name must not be empty."));
        
        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty(),
                string -> { 
                    return true; 
                },
                ValidationMessage.error("Description is not valid."));
        
        productOwnerValidator = new FunctionBasedValidator<>(productOwnerProperty(),
                person -> {
                    return person != null && person.getSkills().contains(organisationProperty.get().getPoSkill());
                },
                ValidationMessage.error("Product Owner must exist and possess the PO Skill."));
        
        projectValidator = new FunctionBasedValidator<>(projectProperty(),
                project -> {
                    return project != null;
                },
                ValidationMessage.error("Project must exist."));
        
        allValidator = new CompositeValidator(shortNameValidator, longNameValidator, descriptionValidator,
                productOwnerValidator, projectValidator);
    }

    public void load(Backlog backlog, Organisation organisation) {
        organisationProperty().set(organisation);

        if (backlog != null) {
            backlogWrapper.set(backlog);
            stories().setAll(backlogWrapper.get().observableStories());
        } else {
            backlogWrapper.set(new Backlog());
            backlogWrapper.reset();
            backlogWrapper.commit();
            stories().clear();
        }
        backlogWrapper.reload();

        // Listen for changes on objects ObservableLists. This could be removed if ModelWrapper supported Lists.
        backlogWrapper.get().observableStories()
                .addListener((ListChangeListener) c -> stories().setAll(backlogWrapper.get().getStories()));

        eligableStories.setAll(storySupplier().get());
        projectProperty().addListener(observable -> {
            eligableStories.setAll(storySupplier().get());
        });
    }

    public void reset() {
        backlogWrapper.reset();
        stories().clear();
    }

    public void reload() {
        backlogWrapper.reload();
        stories.setAll(backlogWrapper.get().getStories());
    }

    protected Supplier<List<Project>> projectSupplier() {
        return () -> organisationProperty().get().getProjects();
    }

    protected Supplier<List<Person>> productOwnerSupplier() {
        return () -> organisationProperty().get().getPeople().stream()
                .filter(p -> p.getSkills().contains((organisationProperty().get().getPoSkill())))
                .collect(Collectors.toList());
    }

    protected Supplier<ObservableList<Story>> storySupplier() {
        return () -> {
            if (projectProperty().get() == null) {
                return FXCollections.observableArrayList();
            } else {
                return projectProperty().get().getUnallocatedStories()
                        .stream().filter(story -> (!stories().contains(story)))
                        .collect(GoatCollectors.toObservableList());
            }
        };
    }

    public Command getCommand() {
        final Command command;
        if (!allValidation().isValid()) {
            // Properties are not valid
            return null;
        }
        if (!backlogWrapper.isDifferent() && !stories().containsAll(backlogWrapper.get().getStories())
                && !backlogWrapper.get().getStories().containsAll(stories)) {
            // Nothing changed
            return null;
        }
        final ArrayList<Story> stories = new ArrayList<>();
        stories.addAll(stories());

        if (backlogWrapper.get().getShortName() == "") { // Must be a new backlog
            final Backlog backlog = new Backlog(shortNameProperty().get(), longNameProperty().get(),
                    descriptionProperty().get(), productOwnerProperty().get(), projectProperty().get(), stories(),
                    scaleProperty().get());
            command = new CreateBacklogCommand(backlog);
        } else {
            final ArrayList<Command> changes = new ArrayList<>();
            if (!longNameProperty().getValue().equals(backlogWrapper.get().getLongName())) {
                changes.add(new EditCommand<>(backlogWrapper.get(), "longName", longNameProperty().getValue()));
            }
            if (!shortNameProperty().getValue().equals(backlogWrapper.get().getShortName())) {
                changes.add(new EditCommand<>(backlogWrapper.get(), "shortName", shortNameProperty().getValue()));
            }
            if (!descriptionProperty().getValue().equals(backlogWrapper.get().getDescription())) {
                changes.add(new EditCommand<>(backlogWrapper.get(), "description", descriptionProperty().getValue()));
            }

            if (!productOwnerProperty().equals(backlogWrapper.get().getProductOwner())) {
                changes.add(new EditCommand<>(backlogWrapper.get(), "productOwner", productOwnerProperty().get()));
            }

            if (scaleProperty().getValue() != backlogWrapper.get().getScale()) {
                changes.add(new EditCommand<>(backlogWrapper.get(), "scale", scaleProperty().getValue()));
            }

            // Stories being added to the backlog
            final ArrayList<Story> addedStories = new ArrayList<>(stories());
            addedStories.removeAll(backlogWrapper.get().getStories());

            // Stories being removed from the backlog
            final ArrayList<Story> removedStories = new ArrayList<>(backlogWrapper.get().getStories());
            removedStories.removeAll(stories());

            for (Story story : addedStories) {
                if (story.getScale() != scaleProperty().get()) {
                    changes.add(new EditCommand<>(story, "estimate", 0));
                    changes.add(new EditCommand<>(story, "scale", scaleProperty().get()));
                }
                changes.add(new MoveItemCommand<>(story, projectProperty().get().observableUnallocatedStories(),
                        backlogWrapper.get().observableStories()));
                changes.add(new EditCommand<>(story, "backlog", backlogWrapper.get()));
            }
            // get the remaining stories and change their scales - might be a better way to do this rather than 2 loops
            for (Story story : backlogWrapper.get().getStories()) {
                if (story.getScale() != scaleProperty().get()) {
                    changes.add(new EditCommand<>(story, "estimate", 0));
                    changes.add(new EditCommand<>(story, "scale", scaleProperty().get()));
                }
            }

            if (projectProperty().get().getShortName() != "" && !projectProperty().get().equals(backlogWrapper.get().getProject())) {
                changes.add(new MoveItemCommand<>(backlogWrapper.get(), backlogWrapper.get().getProject().observableBacklogs(),
                        projectProperty().get().observableBacklogs()));
                changes.add(new EditCommand<>(backlogWrapper.get(), "project", projectProperty().get()));
                // If backlog moved to a different project we need to update the back references of the stories
                // in that backlog.
                for (Story story : backlogWrapper.get().observableStories()) {
                    changes.add(new EditCommand<>(story, "project", projectProperty().get()));
                    changes.add(new EditCommand<>(story, "backlog", backlogWrapper.get()));
                }
            }

            for (Story story : removedStories) {
                changes.add(new RemoveStoryFromBacklogCommand(story, backlogWrapper.get()));
            }
            command = new CompoundCommand("Edit Backlog", changes);
        }
        return command;
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }

    public StringProperty shortNameProperty() {
        return backlogWrapper.field("shortName", Backlog::getShortName, Backlog::setShortName, "");
    }

    public StringProperty longNameProperty() {
        return backlogWrapper.field("longName", Backlog::getLongName, Backlog::setLongName, "");
    }

    public StringProperty descriptionProperty() {
        return backlogWrapper.field("description", Backlog::getDescription, Backlog::setDescription, "");
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return backlogWrapper.field("productOwner", Backlog::getProductOwner, Backlog::setProductOwner, new Person());
    }

    public ObjectProperty<Project> projectProperty() {
        return backlogWrapper.field("project", Backlog::getProject, Backlog::setProject, new Project());
    }

    public ObjectProperty<Scale> scaleProperty() {
        return backlogWrapper.field("backlog", Backlog::getScale, Backlog::setScale, Scale.FIBONACCI);
    }

    public ListProperty<Story> eligableStories() {
        return eligableStories;
    }

    public ListProperty<Story> stories() {
        return stories;
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
    
    public ValidationStatus productOwnerValidation() {
        return productOwnerValidator.getValidationStatus();
    }
    
    public ValidationStatus projectValidation() {
        return projectValidator.getValidationStatus();
    }
    
    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}

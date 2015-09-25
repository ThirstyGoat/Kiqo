package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.command.create.CreateBacklogCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatCollectors;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Model ViewModel
 * @author amy
 *
 */
public class BacklogViewModel extends ModelViewModel<Backlog> {
    private ObservableRuleBasedValidator shortNameValidator;
    private FunctionBasedValidator<String> longNameValidator;
    private FunctionBasedValidator<String> descriptionValidator;
    private FunctionBasedValidator<Person> productOwnerValidator;
    private FunctionBasedValidator<Project> projectValidator;
    private CompositeValidator allValidator;
	private ListProperty<Story> eligibleStories;

    public BacklogViewModel() {
    	eligibleStories = new SimpleListProperty<>(FXCollections.observableArrayList());
    	eligibleStories.bind(Bindings.createObjectBinding(() -> {
        	if (projectProperty().get() != null) {
        		List<Story> list = new ArrayList<>();
        		list.addAll(projectProperty().get().getUnallocatedStories()); // not in any backlog in model
        		list.addAll(modelWrapper.get().observableStories()); // in THIS backlog in model
        		list.addAll(stories()); // in THIS backlog in viewModel
        		return list.stream().distinct().collect(GoatCollectors.toObservableList());
        	} else {
        		return FXCollections.observableArrayList();
        	}
        }, projectProperty(), stories()));
        
        attachValidators();
    }
    
    private void attachValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding rule1 = shortNameProperty().isNotNull();
        BooleanBinding rule2 = shortNameProperty().length().greaterThan(0);
        BooleanBinding rule3 = shortNameProperty().length().lessThan(Utilities.SHORT_NAME_MAX_LENGTH);
        BooleanBinding rule4 = Bindings.createBooleanBinding(
                () -> {
                    if (projectProperty().get() == null) {
                        return true;
                    }
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(),
                            projectProperty().get().getBacklogs());
                },
                shortNameProperty(), projectProperty());
        shortNameValidator.addRule(rule1, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule2, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule3, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule4, ValidationMessage.error("Short name must be unique and not empty"));

        longNameValidator = new FunctionBasedValidator<>(longNameProperty(),
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Name must not be empty"));

        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty(),
                string -> {
                    return true;
                },
                ValidationMessage.error("Description is not valid."));

        productOwnerValidator = new FunctionBasedValidator<>(productOwnerProperty(),
                person -> {
                    return person != null && person.observableSkills().contains(organisationProperty().get().getPoSkill());
                },
                ValidationMessage.error("Product owner must exist and possess the PO skill"));

        projectValidator = new FunctionBasedValidator<>(projectProperty(),
                project -> {
                    return project != null;
                },
                ValidationMessage.error("Project must exist"));

        allValidator = new CompositeValidator(shortNameValidator, longNameValidator, descriptionValidator,
                productOwnerValidator, projectValidator);
    }

    @Override
    protected Supplier<Backlog> modelSupplier() {
        return Backlog::new;
    }

    /**
     * @return A list of valid projects that this backlog can belong to.
     */
    protected Supplier<List<Project>> projectSupplier() {
        return () -> organisationProperty().get().getProjects();
    }

    /**
     * @return A collection of valid product owners that can own this backlog.
     */
    protected Supplier<List<Person>> productOwnerSupplier() {
        return () -> organisationProperty().get().getPeople().stream()
                .filter(p -> p.observableSkills().contains((organisationProperty().get().getPoSkill())))
                .collect(Collectors.toList());
    }

    @Override
    public Command getCommand() {
        final Command command;

        if (!allValidation().isValid()) {
            LOGGER.log(Level.WARNING, "Fields are invalid, no command will be returned.");
            return null;
        } else if (!modelWrapper.isDirty()) {
            LOGGER.log(Level.WARNING, "Nothing changed. No command will be returned");
            return null;
        }

        if (modelWrapper.get().getShortName() == "") { // Must be a new backlog
            final Backlog backlog = new Backlog(shortNameProperty().get(), longNameProperty().get(),
                    descriptionProperty().get(), productOwnerProperty().get(), projectProperty().get(), stories(),
                    scaleProperty().get());
            command = new CreateBacklogCommand(backlog);
        } else {
            final ArrayList<Command> changes = new ArrayList<>();
            super.addEditCommands.accept(changes);

            // Stories being added to the backlog
            final ArrayList<Story> addedStories = new ArrayList<>(stories());
            addedStories.removeAll(modelWrapper.get().getStories());

            // Stories being removed from the backlog
            final ArrayList<Story> removedStories = new ArrayList<>(modelWrapper.get().getStories());
            removedStories.removeAll(stories());

            for (Story story : addedStories) {
                if (story.getScale() != scaleProperty().get()) {
                    changes.add(new EditCommand<>(story, "estimate", 0));
                    changes.add(new EditCommand<>(story, "scale", scaleProperty().get()));
                }
                changes.add(new MoveItemCommand<>(story, projectProperty().get().getUnallocatedStories(),
                        modelWrapper.get().observableStories()));
                changes.add(new EditCommand<>(story, "backlog", modelWrapper.get()));
            }
            // get the remaining stories and change their scales - might be a better way to do this rather than 2 loops
            for (Story story : modelWrapper.get().getStories()) {
                if (story.getScale() != scaleProperty().get()) {
                    changes.add(new EditCommand<>(story, "estimate", 0));
                    changes.add(new EditCommand<>(story, "scale", scaleProperty().get()));
                }
            }

            if (projectProperty().get().getShortName() != "" && !projectProperty().get().equals(modelWrapper.get().getProject())) {
                changes.add(new MoveItemCommand<>(modelWrapper.get(), modelWrapper.get().getProject().observableBacklogs(),
                        projectProperty().get().observableBacklogs()));
                changes.add(new EditCommand<>(modelWrapper.get(), "project", projectProperty().get()));
                // If backlog moved to a different project we need to update the back references of the stories
                // in that backlog.
                for (Story story : modelWrapper.get().observableStories()) {
                    changes.add(new EditCommand<>(story, "project", projectProperty().get()));
                    changes.add(new EditCommand<>(story, "backlog", modelWrapper.get()));
                }
            }

            for (Story story : removedStories) {
                changes.add(new RemoveStoryFromBacklogCommand(story, modelWrapper.get()));
            }
            command = changes.size() == 1 ? changes.get(0) : new CompoundCommand("Edit Backlog", changes);
        }
        return command;
    }

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Backlog::getShortName, Backlog::setShortName, "");
    }

    public StringProperty longNameProperty() {
        return modelWrapper.field("longName", Backlog::getLongName, Backlog::setLongName, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Backlog::getDescription, Backlog::setDescription, "");
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return modelWrapper.field("productOwner", Backlog::getProductOwner, Backlog::setProductOwner, new Person());
    }

    public ObjectProperty<Project> projectProperty() {
        return modelWrapper.field("project", Backlog::getProject, Backlog::setProject, new Project());
    }

    public ObjectProperty<Scale> scaleProperty() {
        return modelWrapper.field("scale", Backlog::getScale, Backlog::setScale, Scale.FIBONACCI);
    }

    public ListProperty<Story> stories() {
        return modelWrapper.field("stories", Backlog::getStories, Backlog::setStories, Collections.<Story>emptyList());
    }

    public ListProperty<Story> eligibleStories() {
        return eligibleStories;
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

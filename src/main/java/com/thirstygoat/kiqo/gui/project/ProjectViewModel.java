package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.create.CreateProjectCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Created by leroy on 7/09/15.
 */
public class ProjectViewModel extends ModelViewModel<Project> {
    private final ObservableRuleBasedValidator  shortNameValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final ObservableRuleBasedValidator descriptionValidator;
    private final CompositeValidator allValidator;

    private final ListProperty<Allocation> eligableAllocations =
            new SimpleListProperty<>(FXCollections.observableArrayList()); // Allocation does not extend Item.
    private final ListProperty<Release> eligableReleases =
            new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));
    private final ListProperty<Backlog> eligableBacklogs =
            new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));
    private final ListProperty<Story> eligableStories =
            new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));

    public ProjectViewModel() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding rule1 = shortNameProperty().isNotNull();
        BooleanBinding rule2 = shortNameProperty().length().greaterThan(0);
        BooleanBinding rule3 = shortNameProperty().length().lessThan(20);
        BooleanBinding rule4 = Bindings.createBooleanBinding(
                () -> {
                    if (organisationProperty().get() == null) {
                        return true;
                    }
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(),
                            organisationProperty().get().getProjects());
                },
                shortNameProperty());
        shortNameValidator.addRule(rule1, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule2, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule3, ValidationMessage.error("Short name must be unique and not empty"));
        shortNameValidator.addRule(rule4, ValidationMessage.error("Short name must be unique and not empty"));

        longNameValidator = new FunctionBasedValidator<String>(longNameProperty(),
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Long name must not be empty."));

        descriptionValidator = new ObservableRuleBasedValidator();

        allValidator = new CompositeValidator();
        allValidator.addValidators(shortNameValidator, longNameValidator, descriptionValidator);
    }

    @Override
    protected Supplier<Project> modelSupplier() {
        return Project::new;
    }

    @Override
    protected void afterLoad() {

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

        if (modelWrapper.get().getShortName() == "") { // Must be a new project
            final Project p = new Project(shortNameProperty().get(), longNameProperty().get(),
                    descriptionProperty().get());
            command = new CreateProjectCommand(p, organisationProperty().get());
        } else {
            final ArrayList<Command> changes = new ArrayList<>();
            super.addEditCommands.accept(changes);
            command = changes.size() == 1 ? changes.get(0) : new CompoundCommand("Edit Project", changes);
        }
        return command;
    }


    /** Model Properties **/

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Project::shortNameProperty, "");
    }

    public StringProperty longNameProperty() {
        return modelWrapper.field("longName", Project::longNameProperty, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Project::descriptionProperty, "");
    }

    public ListProperty<Release> releases() {
        return modelWrapper.field("releases", Project::getReleases, Project::setReleases, new ArrayList<Release>());
    }

    public ListProperty<Story> unallocatedStories() {
        return modelWrapper.field("unallocatedStories", Project::getUnallocatedStories, Project::setUnallocatedStories,
                new ArrayList<Story>());
    }

    public ListProperty<Allocation> allocations() {
        return modelWrapper.field("allocations", Project::getAllocations, Project::setAllocations,
                new ArrayList<Allocation>());
    }

    public ListProperty<Backlog> backlogs() {
        return modelWrapper.field("backlogs", Project::getBacklogs, Project::setBacklogs, new ArrayList<Backlog>());
    }


    /** Validation Statuses **/

    public ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }

    public ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
    }

    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}

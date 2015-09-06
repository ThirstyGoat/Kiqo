package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.create.CreateProjectCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Created by leroy on 6/09/15.
 */
public class ProjectViewModel extends ModelViewModel<Project> {

    private final ObservableRuleBasedValidator shortNameValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<String> descriptionValidator;

    public ProjectViewModel() {
    }

    @Override
    protected Supplier<Project> modelSupplier() {
        return Project::new;
    }

    @Override
    protected void afterLoad() {
        // Do nothing
    }

    @Override
    public Command getCommand() {
        Command command;
        if (modelWrapper.get().getShortName() == "") {
            final Project p = new Project(shortNameProperty().get(), longNameProperty().get(),
                    descriptionProperty().get());
            command = new CreateProjectCommand(p, organisationProperty().get());
        } else {
            final ArrayList<Command> changes = new ArrayList<>();
            super.addEditCommands.accept(changes);
            command = changes.size() == 1 ? changes.get(0) : new CompoundCommand("Edit Backlog", changes);
        }
        return command;
    }

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Project::shortNameProperty, "");
    }

    public StringProperty longNameProperty() {
        return modelWrapper.field("longName", Project::longNameProperty, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Project::descriptionProperty, "");
    }

    public ListProperty releases() {
        return modelWrapper.field("releases", Project::getReleases, Project::setReleases, Collections.emptyList());
    }

    public ListProperty unallocatedStories() {
        return modelWrapper.field("unallocatedStories", Project::getUnallocatedStories, Project::setUnallocatedStories);
    }

    public ListProperty allocations() {
        return modelWrapper.field("allocations", Project::getAllocations, Project::setAllocations,
                Collections.emptyList());
    }

    public ListProperty backlogs() {
        return modelWrapper.field("backlogs", Project::getBacklogs, Project::setBacklogs);
    }

    public ListProperty sprints() {
        return modelWrapper.field("sprints", Project::getSprints, Project::setSprints, Collections.emptyList());
    }
}

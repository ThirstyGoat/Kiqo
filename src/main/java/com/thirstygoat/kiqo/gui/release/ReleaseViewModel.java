package com.thirstygoat.kiqo.gui.release;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.create.CreateReleaseCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReleaseViewModel extends ModelViewModel<Release> {
    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;
    private ObservableRuleBasedValidator projectValidator;
    private ObservableRuleBasedValidator dateValidator;

    public ReleaseViewModel() {
        createValidators();
    }

    public Supplier<Release> modelSupplier() {
        return Release::new;
    }
    
    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                Project project = projectProperty().get();
                if (project != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(), project.getReleases());
                } else {
                    return true; // no project means this isn't for real yet.
                }
            }, 
            shortNameProperty(), projectProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(Utilities.SHORT_NAME_MAX_LENGTH), ValidationMessage.error("Name must be less than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within project"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
        
        projectValidator = new ObservableRuleBasedValidator();
        projectValidator.addRule(projectProperty().isNotNull(), ValidationMessage.error("Project must exist"));
        
        dateValidator = new ObservableRuleBasedValidator();
        BooleanBinding isAfterAllSprintsAreFinished = Bindings.createBooleanBinding(() -> {
            if (modelWrapper.get() != null) { // new releases don't have sprints
                LocalDate releaseDate = dateProperty().get();
                if (releaseDate != null) {
                    return modelWrapper.get().getSprints().stream().allMatch(sprint -> {
                        return releaseDate.isAfter(sprint.getEndDate()) || releaseDate.isEqual(sprint.getEndDate());
                    });
                }
            }
            return true;
        }, dateProperty());
        dateValidator.addRule(dateProperty().isNotNull(), ValidationMessage.error("Release date must not be empty"));
        dateValidator.addRule(isAfterAllSprintsAreFinished, ValidationMessage.error("Release date must fall after any sprint within."));
        
        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, projectValidator, dateValidator);
    }

    @Override
    public Command getCommand() {
        final Command command;
        if (modelWrapper.get().getShortName() != "") { // edit
            final ArrayList<Command> changes = new ArrayList<>();
            super.addEditCommands.accept(changes);

            if (projectProperty().get() != null && modelWrapper.get().getProject() != null && !projectProperty().get().equals(modelWrapper.get().getProject())) {
                changes.add(new MoveItemCommand<>(modelWrapper.get(), modelWrapper.get().getProject().observableReleases(), projectProperty().get().observableReleases()));
            }

            if (changes.size() > 0) {
                command = new CompoundCommand("Edit Release", changes);
            } else {
                command = null;
            }
        } else { // new
            final Release release = new Release(shortNameProperty().get(), projectProperty().get(), dateProperty().get(), descriptionProperty().get());
            command = new CreateReleaseCommand(release);
        }
        return command;
    }

    protected Supplier<List<Project>> projectsSupplier() {
        return () -> {
            List<Project> list = new ArrayList<>();
            if (organisationProperty().get() != null) {
                list.addAll(organisationProperty().get().getProjects());
            }
            return list;
        };
    }
    
    protected StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Release::getShortName, Release::setShortName, "");
    }

    protected StringProperty descriptionProperty() {
        return modelWrapper.field("description", Release::getDescription, Release::setDescription, "");
    }
    
    protected ObjectProperty<Project> projectProperty() {
        return modelWrapper.field("project", Release::getProject, Release::setProject, null);
    }

    protected ObjectProperty<LocalDate> dateProperty() {
        return modelWrapper.field("date", Release::getDate, Release::setDate, null);
    }

    protected ListProperty<Sprint> sprints() {
        return modelWrapper.field("sprint", Release::getSprints, Release::setSprints, new ArrayList<>());
    }

    /** Validation statuses **/
    
    protected ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }
    
    protected ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }
    
    protected ValidationStatus projectValidation() {
        return projectValidator.getValidationStatus();
    }
    
    protected ValidationStatus dateValidation() {
        return dateValidator.getValidationStatus();
    }

    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}

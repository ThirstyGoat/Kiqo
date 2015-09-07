package com.thirstygoat.kiqo.gui.release;

import java.time.LocalDate;
import java.util.ArrayList;

import javafx.beans.binding.*;
import javafx.beans.property.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.command.create.CreateReleaseCommand;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.*;

public class ReleaseViewModel implements Loadable<Release>, ViewModel {
    private ModelWrapper<Release> modelWrapper;
    private ObjectProperty<Release> release;
    private ObjectProperty<Organisation> organisation;
    
    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;
    private ObservableRuleBasedValidator projectValidator;
    private ObservableRuleBasedValidator dateValidator;

    protected ReleaseViewModel() {
        modelWrapper = new GoatModelWrapper<>();
        release = new SimpleObjectProperty<>(null);
        organisation = new SimpleObjectProperty<>(null);
        createValidators();
    }
    
    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                if (organisation.get() != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), release.get(), organisation.get().getSkills());
                } else {
                    return true; // no organisation means this isn't for real yet.
                }
            }, 
            shortNameProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
        
        projectValidator = new ObservableRuleBasedValidator();
        projectValidator.addRule(projectProperty().isNotNull(), ValidationMessage.error("Project must not be empty"));
        
        dateValidator = new ObservableRuleBasedValidator(); // TODO always true
        
        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator);
    }
    
    @Override
    public void load(Release release, Organisation organisation) {
        this.release.set(release);
        modelWrapper.set(release != null ? release : new Release());
        modelWrapper.reload();
    }

    protected Command createCommand() {
        final Command command;
        if (release.get() != null) { // edit
            final ArrayList<Command> changes = new ArrayList<>();
    
            if (shortNameProperty().get() != null && !shortNameProperty().get().equals(release.get().getShortName())) {
                changes.add(new EditCommand<>(release.get(), "shortName", shortNameProperty().get()));
            }
            if (descriptionProperty().get() != null && !descriptionProperty().get().equals(release.get().getDescription())) {
                changes.add(new EditCommand<>(release.get(), "description", descriptionProperty().get()));
            }
            if (projectProperty().get() != null && !projectProperty().get().equals(release.get().getProject())) {
                changes.add(new EditCommand<>(release.get(), "project", projectProperty().get()));
            }
            if (dateProperty().get() != null && !dateProperty().get().equals(release.get().getDate())) {
                changes.add(new EditCommand<>(release.get(), "date", dateProperty().get()));
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

    protected void reload() {
        modelWrapper.reload();
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

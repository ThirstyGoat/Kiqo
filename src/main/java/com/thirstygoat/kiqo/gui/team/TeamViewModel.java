package com.thirstygoat.kiqo.gui.team;

import javafx.beans.binding.*;
import javafx.beans.property.*;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.*;

public class TeamViewModel implements Loadable<Team>, ViewModel {
    private ModelWrapper<Team> modelWrapper;
    private ObjectProperty<Team> team;
    private ObjectProperty<Organisation> organisation;
    
    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;

    public TeamViewModel() {
        team = new SimpleObjectProperty<>(null);
        organisation = new SimpleObjectProperty<>(null);
        modelWrapper = new GoatModelWrapper<>();
        createValidators();
    }
    
    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                if (organisation.get() != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), team.get(), organisation.get().getTeams());
                } else {
                    return true; // no project means this isn't for real yet.
                }
            }, 
            shortNameProperty(), organisationProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
       
        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator);

    }

    @Override
    public void load(Team team, Organisation organisation) {
        this.team.set(team);
        this.organisation.set(organisation);
        modelWrapper.set(team != null ? team : new Team());
        modelWrapper.reload();
    }
    
    protected StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Team::getShortName, Team::setShortName, "");
    }

    protected StringProperty descriptionProperty() {
        return modelWrapper.field("description", Team::getDescription, Team::setDescription, "");
    }
    
    protected ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    } 
    
    protected ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }
    
    protected ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }
    
    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}

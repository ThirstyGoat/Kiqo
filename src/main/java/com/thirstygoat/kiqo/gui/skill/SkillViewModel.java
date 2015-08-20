package com.thirstygoat.kiqo.gui.skill;

import java.util.ArrayList;

import javafx.beans.binding.*;
import javafx.beans.property.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.*;

public class SkillViewModel implements Loadable<Skill>, ViewModel {
    private ModelWrapper<Skill> modelWrapper = new GoatModelWrapper<>();
    
    private ObjectProperty<Skill> skill = new SimpleObjectProperty<>(null);
    private ObjectProperty<Command> command = new SimpleObjectProperty<>(null);

    private ObjectProperty<Organisation> organisation = new SimpleObjectProperty<>(null);

    private ObservableRuleBasedValidator nameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    
    public SkillViewModel() {
        command.bind(Bindings.createObjectBinding(this::createCommand, modelWrapper.differentProperty()));
        createValidators();
    }
    
    private void createValidators() {
        nameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { return skill.get() != null // null checks to prevent NPE
                    && organisationProperty().get() != null
                    && Utilities.shortnameIsUnique(nameProperty().get(), skill.get(), organisationProperty().get().getSkills()); 
            }, 
            nameProperty());
        nameValidator.addRule(nameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        nameValidator.addRule(nameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        nameValidator.addRule(nameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        nameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator();
    }

    /**
     * @param skill model object to be displayed
     * @param organisation (ignored)
     */
    @Override
    public void load(Skill skill, Organisation organisation) {
        skillProperty().set(skill);
        organisationProperty().set(organisation);
        modelWrapper.set(skill);
    }

    protected Command createCommand() {
        final Command command;
        if (skill.get() != null) {
            final ArrayList<Command> changes = new ArrayList<>();
    
            if (!nameProperty().get().equals(skill.get().getShortName())) {
                changes.add(new EditCommand<>(skill.get(), "shortName", nameProperty().get()));
            }
            if (!descriptionProperty().get().equals(skill.get().getDescription())) {
                changes.add(new EditCommand<>(skill.get(), "description", descriptionProperty().get()));
            }
            command = new CompoundCommand("Edit Skill", changes);
        } else {
            command = null;
        }
        return command;
    }

    public StringProperty nameProperty() {
        return modelWrapper.field("shortName", Skill::getShortName, Skill::setShortName, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Skill::getDescription, Skill::setDescription, "");
    }

    public ObjectProperty<Skill> skillProperty() {
        return skill;
    }
    
    public ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }

    public ObjectProperty<Command> commandProperty() {
        return command;
    }
    
    public ValidationStatus nameValidation() {
        return nameValidator.getValidationStatus();
    }
    
    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }
}

package com.thirstygoat.kiqo.gui.skill;

import java.util.ArrayList;

import com.thirstygoat.kiqo.validation.ShortNameValidator;
import javafx.beans.binding.*;
import javafx.beans.property.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.command.create.CreateSkillCommand;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.*;

public class SkillViewModel implements Loadable<Skill>, ViewModel {
    private ModelWrapper<Skill> modelWrapper;    
    private ObjectProperty<Skill> skill;
    private ObjectProperty<Organisation> organisation;

    private ObservableRuleBasedValidator nameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;
    
    public SkillViewModel() {
        modelWrapper = new GoatModelWrapper<>();
        skill = new SimpleObjectProperty<>(null);
        organisation = new SimpleObjectProperty<>(null);
        createValidators();
    }
    
    private void createValidators() {
        nameValidator = new ShortNameValidator<Skill>(nameProperty(), modelWrapper.get(),
                organisationProperty().get()::getSkills, "organisation");

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
        
        allValidator = new CompositeValidator(nameValidator, descriptionValidator);
    }

    /**
     * @param skill model object to be displayed
     * @param organisation (ignored)
     */
    @Override
    public void load(Skill skill, Organisation organisation) {
        skillProperty().set(skill);
        organisationProperty().set(organisation);
        if (skill != null) {
            modelWrapper.set(skill);
        } else {
            modelWrapper.set(new Skill());
        }
        modelWrapper.reload();
    }

    public void reload() {
        modelWrapper.reload();
    }

    protected Command createCommand() {
        final Command command;
        if (skill.get() != null) { // edit
            final ArrayList<Command> changes = new ArrayList<>();
    
            if (nameProperty().get() != null && !nameProperty().get().equals(skill.get().getShortName())) {
                changes.add(new EditCommand<>(skill.get(), "shortName", nameProperty().get()));
            }
            if (descriptionProperty().get() != null && !descriptionProperty().get().equals(skill.get().getDescription())) {
                changes.add(new EditCommand<>(skill.get(), "description", descriptionProperty().get()));
            }
            
            if (changes.size() > 0) {
                command = new CompoundCommand("Edit Skill", changes);
            } else {
                command = null;
            }
        } else { // new
            final Skill s = new Skill(nameProperty().get(), descriptionProperty().get());
            command = new CreateSkillCommand(s, organisationProperty().get());
        }
        return command;
    }

    public StringProperty nameProperty() {
        return modelWrapper.field("shortName", Skill::getShortName, Skill::setShortName, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Skill::getDescription, Skill::setDescription, "");
    }

    protected ObjectProperty<Skill> skillProperty() {
        return skill;
    }
    
    protected ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }
    
    public ValidationStatus nameValidation() {
        return nameValidator.getValidationStatus();
    }
    
    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}

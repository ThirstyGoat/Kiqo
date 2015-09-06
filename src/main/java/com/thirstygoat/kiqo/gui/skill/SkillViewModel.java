package com.thirstygoat.kiqo.gui.skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

import com.thirstygoat.kiqo.gui.ModelViewModel;
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

public class SkillViewModel extends ModelViewModel<Skill> {
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

    @Override
    protected Supplier<Skill> modelSupplier() {
        return Skill::new;
    }

    private void createValidators() {
        nameValidator = new ShortNameValidator<Skill>(nameProperty(), modelWrapper.get(),
                organisationProperty().get() != null ? organisationProperty().get()::getSkills : ArrayList<Skill>::new,
                "organisation");
        descriptionValidator = new ObservableRuleBasedValidator(); // always true
        
        allValidator = new CompositeValidator(nameValidator, descriptionValidator);
    }

    public void afterLoad() {
        // Do nothing
    }

    @Override
    public Command createCommand() {
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

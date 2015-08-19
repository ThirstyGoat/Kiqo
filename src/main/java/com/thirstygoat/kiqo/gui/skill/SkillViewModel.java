package com.thirstygoat.kiqo.gui.skill;

import javafx.beans.property.StringProperty;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.GoatModelWrapper;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;

public class SkillViewModel implements Loadable<Skill>, ViewModel {
    private ModelWrapper<Skill> modelWrapper = new GoatModelWrapper<>();
    
    /**
     * @param skill model object to be displayed
     * @param organisation (ignored)
     */
    @Override
    public void load(Skill skill, Organisation organisation) {
        modelWrapper.set(skill);
        modelWrapper.reload();
    }

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Skill::getShortName, Skill::setShortName, "");
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Skill::getDescription, Skill::setDescription, "");
    }
}

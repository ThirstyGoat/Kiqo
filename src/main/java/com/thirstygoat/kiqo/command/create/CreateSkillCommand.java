package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;


/**
 * Command to add a Skill to a Project
 */
public class CreateSkillCommand extends CreateCommand {
    private final Skill skill;
    private final Organisation organisation;

    /**
     * @param skill        Skill created
     * @param organisation organisation that the skill is to be associated with
     */
    public CreateSkillCommand(final Skill skill, final Organisation organisation) {
        super(skill);
        this.skill = skill;
        this.organisation = organisation;
    }

    @Override
    public void addToModel() {
        organisation.getSkills().add(skill);
    }

    @Override
    public void removeFromModel() {
        // Goodbye skill
        organisation.getSkills().remove(skill);
    }

    @Override
    public String toString() {
        return "<Create Skill: \"" + skill.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Create Skill";
    }

}

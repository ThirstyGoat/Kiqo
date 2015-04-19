package seng302.group4.undo;

import seng302.group4.Project;
import seng302.group4.Skill;


/**
 * Command to add a Skill to a Project
 *
 */
public class CreateSkillCommand extends Command<Skill> {
    private final Skill skill;
    private final Project project;

    /**
     * @param skill Skill created
     * @param project Project that the skill is to be associated with
     */
    public CreateSkillCommand(final Skill skill, final Project project) {
        this.skill = skill;
        this.project = project;
    }

    @Override
    public Skill execute() {
        project.getSkills().add(skill);
        return skill;
    }

    @Override
    public void undo() {
        // Goodbye skill
        project.getSkills().remove(skill);
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

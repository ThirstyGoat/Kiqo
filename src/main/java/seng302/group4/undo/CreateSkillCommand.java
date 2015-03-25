package seng302.group4.undo;

import seng302.group4.Skill;


public class CreateSkillCommand extends Command<Skill> {
    private final String shortName;
    private final String description;

    private Skill skill = null;

    /**
     * @param shortName
     * @param description
     */
    public CreateSkillCommand(final String shortName, final String description) {
        this.shortName = shortName;
        this.description = description;
    }

    @Override
    public Skill execute() {
        if (skill == null) {
            skill = new Skill(shortName, description);
        }
        return skill;
    }

    @Override
    public void undo() {

    }

    @Override
    public String toString() {
        return "<Create Skill: \"" + shortName + "\">";
    }

    public Skill getSkill() {
        return skill;
    }

    public String getType() {
        return "Create Skill";
    }

}

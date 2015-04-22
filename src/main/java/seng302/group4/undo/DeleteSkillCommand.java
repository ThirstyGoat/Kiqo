package seng302.group4.undo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seng302.group4.Person;
import seng302.group4.Organisation;
import seng302.group4.Skill;

/**
 * Command to delete a skill from a project.
 *
 */
public class DeleteSkillCommand extends Command<Skill> {

    private final Organisation organisation;
    private final Skill skill;
    private final List<Person> peopleWithSkill = new ArrayList<>();

    /**
     * @param skill Skill to be deleted
     * @param organisation Project to which the skill belongs
     */
    public DeleteSkillCommand(final Skill skill, final Organisation organisation) {
        this.skill = skill;
        this.organisation = organisation;

        setPeopleWithSkill();
    }

    /**
     * Loops through all the people in the project, and adds the people who have the skill
     * to the ArrayList that contains only people who have that skill
     */
    private void setPeopleWithSkill() {
        peopleWithSkill.addAll(organisation.getPeople().stream().filter(person ->
                person.getSkills().contains(skill)).collect(Collectors.toList()));
    }

    /**
     * @return all people in the project who have the skill that is marked for deletion
     */
    public List<Person> getPeopleWithSkill() {
        return peopleWithSkill;
    }

    @Override
    public Skill execute() {
        // Remove the skill from any people in the project who have the skill
        for (final Person person : peopleWithSkill) {
            person.getSkills().remove(skill);
        }
        organisation.getSkills().remove(skill);
        return skill;
    }

    @Override
    public void undo() {
        // Add the skill back to wherever it was
        organisation.getSkills().add(skill);

        for (final Person person : peopleWithSkill) {
            person.getSkills().add(skill);
        }
    }

    @Override
    public String toString() {
        return "<Delete Skill: \"" + skill.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Skill";
    }
}

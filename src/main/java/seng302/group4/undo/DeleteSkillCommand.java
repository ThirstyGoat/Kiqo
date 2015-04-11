package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DeleteSkillCommand extends Command<Skill> {

    private Project project;
    private Skill skill;
    private ArrayList<Person> peopleWithSkill = new ArrayList<>();

    /**
     * @param skill Skill to be deleted
     * @param project Project to which the skill belongs
     */
    public DeleteSkillCommand(final Skill skill, final Project project) {
        this.skill = skill;
        this.project = project;

        setPeopleWithSkill();
    }

    /**
     * Loops through all the people in the project, and adds the people who have the skill
     * to the ArrayList that contains only people who have that skill
     */
    private void setPeopleWithSkill() {
        peopleWithSkill.addAll(project.getPeople().stream().filter(person ->
                person.getSkills().contains(skill)).collect(Collectors.toList()));
    }

    public ArrayList<Person> getPeopleWithSkill() {
        return peopleWithSkill;
    }

    @Override
    public Skill execute() {
        // Remove the skill from any people in the project who have the skill
        for (Person person : peopleWithSkill) {
            person.getSkills().remove(skill);
        }
        project.getSkills().remove(skill);
//        callbackFunction.call();
        return skill;
    }

    @Override
    public void undo() {
        // Add the skill back to wherever it was
        project.getSkills().add(skill);

        for (Person person : getPeopleWithSkill()) {
            person.getSkills().add(skill);
        }
    }

    @Override
    public String toString() {
        return "<Delete Skill: \"" + skill.getShortName() + "\">";
    }

    public Skill getSkill() {
        return skill;
    }

    public String getType() {
        return "Delete Skill";
    }
}

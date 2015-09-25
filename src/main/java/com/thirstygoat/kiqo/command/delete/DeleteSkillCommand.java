package com.thirstygoat.kiqo.command.delete;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;

/**
 * Command to delete a skill from a project.
 *
 */
public class DeleteSkillCommand extends DeleteCommand {

    private final Organisation organisation;
    private final Skill skill;
    // Hash map of people with skills, and the index at which the skill appears in their skills list
    private final Map<Integer, Person> peopleWithSkill = new LinkedHashMap<>();

    private int organisationIndex;

    /**
     * @param skill Skill to be deleted
     * @param organisation organisation to which the skill belongs
     */
    public DeleteSkillCommand(final Skill skill, final Organisation organisation) {
        super(skill);
        this.skill = skill;
        this.organisation = organisation;

        setPeopleWithSkill();
    }

    /**
     * Loops through all the people in the project, and adds the people who have the skill
     * to the ArrayList that contains only people who have that skill
     */
    private void setPeopleWithSkill() {
        for (Person person : organisation.getPeople()) {
            if (person.observableSkills().contains(skill)) {
                peopleWithSkill.put(person.observableSkills().indexOf(skill), person);
            }
        }
    }

    /**
     * @return all people in the project who have the skill that is marked for deletion
     */
    public ArrayList<Person> getPeopleWithSkill() {
        ArrayList<Person> arrayList = new ArrayList<>();
        for (Person person : peopleWithSkill.values()) {
            arrayList.add(person);
        }
        return arrayList;
    }

    @Override
    public void removeFromModel() {
        // Remove the skill from any people in the project who have the skill
        for (Person person : peopleWithSkill.values()) {
            person.observableSkills().remove(skill);
        }

        organisationIndex = organisation.getSkills().indexOf(skill);
        organisation.getSkills().remove(skill);
    }

    @Override
    public void addToModel() {
        // Add the skill back to wherever it was
        organisation.getSkills().add(organisationIndex, skill);

        for (Map.Entry<Integer, Person> entry : peopleWithSkill.entrySet()) {
            entry.getValue().observableSkills().add(entry.getKey(), skill);
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
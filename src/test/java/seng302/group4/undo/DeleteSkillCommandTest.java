package seng302.group4.undo;

import org.junit.Test;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;

import java.util.ArrayList;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeleteSkillCommandTest {

    private Project project;

    public void setUp() {
        project = new Project();

        // Create skills
        Skill skill1 = new Skill("Skill1", "Skill1 description");
        Skill skill2 = new Skill("Skill2", "Skill2 description");
        Skill skill3 = new Skill("Skill3", "Skill3 description");

        // Create people
        Person person1 = new Person("", "", "", "", "", "", "", new ArrayList<>());
        ArrayList<Skill> skillSet = new ArrayList<>();
        skillSet.add(skill1);
        skillSet.add(skill2);
        skillSet.add(skill3);
        Person person2 = new Person("", "", "", "", "", "", "", skillSet);
        Person person3 = new Person("", "", "", "", "", "", "", new ArrayList<>());

        // Add people to project
        project.addPerson(person1);
        project.addPerson(person2);
        project.addPerson(person3);

        // Add skills to project
        project.addSkill(skill1);
        project.addSkill(skill2);
        project.addSkill(skill3);

    }

    @Test
    public void deleteUnusedSkill_Success() {
        setUp();

        Skill unusedSkill = new Skill("Unused skill name", "Unused skill description");
        project.addSkill(unusedSkill);

        DeleteSkillCommand command = new DeleteSkillCommand(unusedSkill, project);

        command.execute();
        assert !project.getSkills().contains(unusedSkill);
    }

    @Test
    public void undoDeleteUnusedSkill_Success() {
        setUp();

        Skill unusedSkill = new Skill("Unused skill name", "Unused skill description");
        project.addSkill(unusedSkill);

        DeleteSkillCommand command = new DeleteSkillCommand(unusedSkill, project);

        command.execute();
        command.undo();

        assert project.getSkills().contains(unusedSkill);
    }

    @Test
    public void deleteUsedSkill_Success() {
        setUp();
        Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.addSkill(usedSkill);

        DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        command.execute();

        assert !project.getSkills().contains(usedSkill);
        for (Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }
    }

    @Test
    public void checkDeleteSkill() {
        setUp();
        Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.addSkill(usedSkill);

        DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        assert command.getPeopleWithSkill().contains(project.getPeople().get(0));
        assert command.getPeopleWithSkill().size() == 1;
    }

    @Test
    public void undoDeleteUsedSkill_Success() {
        setUp();
        Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.addSkill(usedSkill);

        DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        // Remove the skill from people with it, and the project
        command.execute();

        // Check to make sure nobody has the skill
        assert !project.getSkills().contains(usedSkill);
        for (Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }

        // Undo the action
        command.undo();

        // Check to make sure that the person now has the skill
        assert project.getSkills().contains(usedSkill);
        assert project.getPeople().get(0).getSkills().contains(usedSkill);
    }

    @Test
    public void redoDeleteUsedSkill_Success() {
        setUp();
        Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.addSkill(usedSkill);

        DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        // Remove the skill from people with it, and the project
        command.execute();

        // Check to make sure nobody has the skill
        assert !project.getSkills().contains(usedSkill);
        for (Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }

        // Undo the action
        command.undo();

        // Check to make sure that the person now has the skill
        assert project.getSkills().contains(usedSkill);
        assert project.getPeople().get(0).getSkills().contains(usedSkill);

        // Redo the action
        command.redo();

        // Check to make sure nobody has the skill
        assert !project.getSkills().contains(usedSkill);
        for (Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }
    }
}
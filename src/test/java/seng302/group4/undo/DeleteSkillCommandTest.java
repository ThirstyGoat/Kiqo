package seng302.group4.undo;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeleteSkillCommandTest {

    private Project project;

    /**
     * Initialises the test environment.
     */
    @Before
    public void setUp() {
        project = new Project();

        // Create skills
        final Skill skill1 = new Skill("Skill1", "Skill1 description");
        final Skill skill2 = new Skill("Skill2", "Skill2 description");
        final Skill skill3 = new Skill("Skill3", "Skill3 description");

        // Create people
        final Person person1 = new Person("", "", "", "", "", "", "", new ArrayList<>());
        final ArrayList<Skill> skillSet = new ArrayList<>();
        skillSet.add(skill1);
        skillSet.add(skill2);
        skillSet.add(skill3);
        final Person person2 = new Person("", "", "", "", "", "", "", skillSet);
        final Person person3 = new Person("", "", "", "", "", "", "", new ArrayList<>());

        // Add people to project
        project.getPeople().add(person1);
        project.getPeople().add(person2);
        project.getPeople().add(person3);

        // Add skills to project
        project.getSkills().add(skill1);
        project.getSkills().add(skill2);
        project.getSkills().add(skill3);

    }

    /**
     * Tests that a skill not assigned to any people is successfully deleted from the project.
     */
    @Test
    public void deleteUnusedSkill_Success() {
        final Skill unusedSkill = new Skill("Unused skill name", "Unused skill description");
        project.getSkills().add(unusedSkill);

        final DeleteSkillCommand command = new DeleteSkillCommand(unusedSkill, project);

        command.execute();
        assert !project.getSkills().contains(unusedSkill);
    }

    /**
     * Tests that a skill not assigned to any people is successfully re-added to the project, on undo.
     */
    @Test
    public void undoDeleteUnusedSkill_Success() {
        final Skill unusedSkill = new Skill("Unused skill name", "Unused skill description");
        project.getSkills().add(unusedSkill);

        final DeleteSkillCommand command = new DeleteSkillCommand(unusedSkill, project);

        command.execute();
        command.undo();

        assert project.getSkills().contains(unusedSkill);
    }

    /**
     * Tests that a skill assigned to a person is successfully deleted from its person and the project.
     */
    @Test
    public void deleteUsedSkill_Success() {
        final Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.getSkills().add(usedSkill);

        final DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        command.execute();

        assert !project.getSkills().contains(usedSkill);
        for (final Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }
    }

    /**
     * Tests that the {@link DeleteSkillCommand#getPeopleWithSkill()} method returns an accurate result.
     */
    @Test
    public void checkDeleteSkill() {
        final Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.getSkills().add(usedSkill);

        final DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        assert command.getPeopleWithSkill().contains(project.getPeople().get(0));
        assert command.getPeopleWithSkill().size() == 1;
    }

    /**
     * Tests that a skill assigned to people is successfully re-added to its people and the project, on undo.
     */
    @Test
    public void undoDeleteUsedSkill_Success() {
        final Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.getSkills().add(usedSkill);

        final DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        // Remove the skill from people with it, and the project
        command.execute();

        // Check to make sure nobody has the skill
        assert !project.getSkills().contains(usedSkill);
        for (final Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }

        // Undo the action
        command.undo();

        // Check to make sure that the person now has the skill
        assert project.getSkills().contains(usedSkill);
        assert project.getPeople().get(0).getSkills().contains(usedSkill);
    }

    /**
     * Tests that a skill assigned to people is successfully re-deleted from its people and the project.
     */
    @Test
    public void redoDeleteUsedSkill_Success() {
        final Skill usedSkill = new Skill("", "");
        project.getPeople().get(0).getSkills().add(usedSkill);
        project.getSkills().add(usedSkill);

        final DeleteSkillCommand command = new DeleteSkillCommand(usedSkill, project);

        // Remove the skill from people with it, and the project
        command.execute();

        // Check to make sure nobody has the skill
        assert !project.getSkills().contains(usedSkill);
        for (final Person person : project.getPeople()) {
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
        for (final Person person : project.getPeople()) {
            assert !person.getSkills().contains(usedSkill);
        }
    }
}
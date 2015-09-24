package com.thirstygoat.kiqo.command;

import java.io.File;
import java.util.ArrayList;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.command.delete.DeleteSkillCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeleteSkillCommandTest {

    private Organisation organisation;
    private Skill skill1;
    private Person person1;

    /**
     * Initialises the test environment.
     */
    @Before
    public void setUp() {
        organisation = new Organisation(new File(""));

        // create two of each, for checking that only the relevant object is modified
        person1 = new Person("person1", "", "", "", "", "", "", new ArrayList<>());
        final Person person2 = new Person("person2", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().addAll(person1, person2);

        skill1 = new Skill("skill1", "");
        final Skill skill2 = new Skill("skill2", "");
        organisation.getSkills().addAll(skill1, skill2);
    }

    /**
     * Tests that a skill1 not assigned to any people is successfully deleted from the project and successfully re-added
     * to the project on undo.
     */
    @Test
    public void testCommand_unusedSkill() {
        final DeleteSkillCommand command = new DeleteSkillCommand(skill1, organisation);

        Assert.assertThat(organisation.getSkills(), CoreMatchers.hasItem(skill1));

        command.execute();
        Assert.assertThat(organisation.getSkills(), CoreMatchers.not(CoreMatchers.hasItem(skill1)));

        command.undo();
        Assert.assertThat(organisation.getSkills(), CoreMatchers.hasItem(skill1));

        command.redo();
        Assert.assertThat(organisation.getSkills(), CoreMatchers.not(CoreMatchers.hasItem(skill1)));
    }

    /**
     * Tests that a skill assigned to a person is deleted from its people and the project, with undo and redo.
     */
    @Test
    public void testCommand_usedSkill() {
        useSkill();
        final DeleteSkillCommand command = new DeleteSkillCommand(skill1, organisation);

        Assert.assertThat(organisation.getSkills(), CoreMatchers.hasItem(skill1));
        assertSkillIsUsed();

        command.execute();
        Assert.assertThat(organisation.getSkills(), CoreMatchers.not(CoreMatchers.hasItem(skill1)));
        assertSkillIsNotUsed();

        command.undo();
        Assert.assertThat(organisation.getSkills(), CoreMatchers.hasItem(skill1));
        assertSkillIsUsed();

        command.redo();
        Assert.assertThat(organisation.getSkills(), CoreMatchers.not(CoreMatchers.hasItem(skill1)));
        assertSkillIsNotUsed();
    }

    /**
     * Tests that the {@link DeleteSkillCommand#getPeopleWithSkill()} method returns an accurate result.
     */
    @Test
    public void testGetPeopleWithSkill() {
        // unused skill
        final DeleteSkillCommand command1 = new DeleteSkillCommand(skill1, organisation);
        Assert.assertThat(command1.getPeopleWithSkill(), CoreMatchers.not(CoreMatchers.hasItem(person1)));

        // used skill
        useSkill();
        final DeleteSkillCommand command2 = new DeleteSkillCommand(skill1, organisation);
        Assert.assertThat(command2.getPeopleWithSkill(), CoreMatchers.hasItem(person1));
        Assert.assertEquals(1, command2.getPeopleWithSkill().size());
    }

    /**
     * Assigns skill1 to person1
     */
    private void useSkill() {
        person1.observableSkills().add(skill1);
    }

    /**
     * Asserts that skill1 is not assigned to any people.
     */
    private void assertSkillIsNotUsed() {
        for (final Person person : organisation.getPeople()) {
            Assert.assertThat(person.observableSkills(), CoreMatchers.not(CoreMatchers.hasItem(skill1)));
        }
    }

    /**
     * Asserts that skill1 is assigned to person1, and not to any other people.
     */
    private void assertSkillIsUsed() {
        for (final Person person : organisation.getPeople()) {
            if (person == person1) {
                Assert.assertThat(person.observableSkills(), CoreMatchers.hasItem(skill1));
            } else {
                Assert.assertThat(person.observableSkills(), CoreMatchers.not(CoreMatchers.hasItem(skill1)));
            }
        }
    }
}

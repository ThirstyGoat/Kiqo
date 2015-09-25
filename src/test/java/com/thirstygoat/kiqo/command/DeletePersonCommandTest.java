package com.thirstygoat.kiqo.command;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.command.delete.DeletePersonCommand;
import com.thirstygoat.kiqo.exceptions.InvalidPersonDeletionException;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Team;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeletePersonCommandTest {

    private Organisation organisation;
    private Team team;

    /**
     * Sets up the test environment.
     */
    @Before
    public void setUp() {
        organisation = new Organisation();
        team = new Team("", "", new ArrayList<>());
    }

    /**
     * Tests that the person is deleted from the project
     */
    @Test
    public void deletePerson_PersonRemovedFromProject() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
    }

    /**
     * Tests that the person is removed from all of their teams before being deleted
     */
    @Test
    public void deletePerson_PersonRemovedFromTeam() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.not(CoreMatchers.hasItem(person)));
    }

    /**
     * Tests that the PO is removed from their role before being deleted
     */
    @Test
    public void deletePersonWithPORole_PersonRemovedFromTeamAndPORole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.setProductOwner(person);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertNotEquals(team.getProductOwner(), person);
    }

    /**
     * Tests that a dev team member is removed from their role before being deleted
     */
    @Test
    public void deletePersonWithDevRole_PersonRemovedFromTeamAndDevRole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.getDevTeam().add(person);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getDevTeam(), CoreMatchers.not(CoreMatchers.hasItem(person)));
    }

    /**
     * Tests that the person is added back to their original project on undo.
     */
    @Test
    public void undoDeletePerson_PersonAddedBackToProject() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));

        command.undo();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person));
    }

    /**
     * Tests that the person is added back to their original team on undo.
     */
    @Test
    public void undoDeletePersonInTeam_PersonAddedBackToTeam() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.not(CoreMatchers.hasItem(person)));

        command.undo();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.hasItem(person));
    }

    /**
     * Tests that the person is added back with their original PO role, on undo.
     */
    @Test
    public void undoDeletePersonInTeamWithPORole_PersonAddedBackToTeamWithRole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.setProductOwner(person);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertNotEquals(team.getProductOwner(), person);

        command.undo();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.hasItem(person));
        Assert.assertEquals(team.getProductOwner(), person);
    }

    /**
     * Tests that the person is added back with their original dev team role, on undo.
     */
    @Test
    public void undoDeletePersonInTeamWithDevRole_PersonAddedBackToTeamWithDevRole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        organisation.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.getDevTeam().add(person);

        // Create command
        final DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException e) {
            return;
        }
        command.execute();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.not(CoreMatchers.hasItem(person)));
        Assert.assertThat(team.getDevTeam(), CoreMatchers.not(CoreMatchers.hasItem(person)));

        command.undo();

        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person));
        Assert.assertThat(team.getTeamMembers(), CoreMatchers.hasItem(person));
        Assert.assertThat(team.getDevTeam(), CoreMatchers.hasItem(person));
    }

    @Test
    public void deletePersonWhoIsProductOwnerOfBacklog_NotAllowed() {
        Project project = new Project();
        organisation.getProjects().add(project);
        Person person = new Person();

        List<Skill> skills = new ArrayList<>();
        skills.add(organisation.getPoSkill());
        person.setSkills(skills);


        Backlog backlog = new Backlog("Short name", "Name", "Description", person, project, new ArrayList<>(), Scale.FIBONACCI);
        project.observableBacklogs().add(backlog);

        try {
            DeletePersonCommand command = new DeletePersonCommand(person, organisation);
        } catch (InvalidPersonDeletionException ignored) {
            // Shouldn't be allowed to delete this person, since they are the PO of a backlog
            Assert.assertTrue(true);
            return;
        }
        Assert.assertTrue(false);
    }
}
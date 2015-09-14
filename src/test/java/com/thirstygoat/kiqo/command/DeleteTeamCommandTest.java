package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.delete.DeleteTeamCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by James on 11/04/15.
 */
public class DeleteTeamCommandTest {

    Person person1;
    Person person2;
    Person person3;
    private Organisation organisation;
    private Team teamWithMembers;
    private Team teamNoMembers;

    /**
     * Initialises the test environment.
     */
    @Before
    public void setUp() {
        organisation = new Organisation(new File(""));

        // Create people
        person1 = new Person("one", "", "", "", "", "", "", new ArrayList<>());
        person2 = new Person("two", "", "", "", "", "", "", new ArrayList<>());
        person3 = new Person("three", "", "", "", "", "", "", new ArrayList<>());

        // Add people to project
        organisation.getPeople().add(person1);
        organisation.getPeople().add(person2);
        organisation.getPeople().add(person3);

        // Create a team
        teamWithMembers = new Team("team1", "has members", new ArrayList<>());
        teamNoMembers = new Team("team2", "has no members", new ArrayList<>());

        // Populate team members
        teamWithMembers.observableTeamMembers().add(person1);
        teamWithMembers.observableTeamMembers().add(person2);

        // Add teams to project
        organisation.getTeams().add(teamWithMembers);
        organisation.getTeams().add(teamNoMembers);
    }

    /**
     * Tests that a team with no members is successfully deleted from the project.
     */
    @Test
    public void deleteEmptyTeam_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, organisation);

        command.execute();

        // Check to see if team was deleted and all people remained
        Assert.assertThat(organisation.getTeams(), CoreMatchers.not(CoreMatchers.hasItem(teamNoMembers)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with members is deleted but its members are kept.
     */
    @Test
    public void deleteTeamWithMembersButKeepPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);

        command.execute();

        // Check to see if team was deleted and all people remained

        Assert.assertThat(organisation.getTeams(), CoreMatchers.not(CoreMatchers.hasItem(teamWithMembers)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with members is successfully deleted from the project, and with the setDeleteMembers flag its
     * members are also deleted from the project.
     */
    @Test
    public void deleteTeamWithMembersAndPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);

        command.setDeleteTeamMembers(true);

        command.execute();

        // Check to see if team and all team members were deleted
        Assert.assertThat(organisation.getTeams(), CoreMatchers.not(CoreMatchers.hasItem(teamWithMembers)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person1)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person2)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3)); // person not in team remained
    }

    /**
     * Tests that a team with no members is re-added to the project, on undo.
     */
    @Test
    public void undoDeleteEmptyTeam() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, organisation);

        command.execute();
        command.undo();

        // check to see if team was deleted and restored
        Assert.assertThat(organisation.getTeams(), CoreMatchers.hasItem(teamNoMembers));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with members has its members re-added and itself re-added to the project, on undo.
     */
    @Test
    public void undoTeamWithMembersButKeepPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);

        command.execute();
        command.undo();

        // check to see if team was deleted and restored and all ppl remained in project
        Assert.assertThat(organisation.getTeams(), CoreMatchers.hasItem(teamWithMembers));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with members has its members re-added and itself re-added to the project, even when the
     * setDeleteMembers flag is set, on undo.
     */
    @Test
    public void undoTeamWithMembersAndPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);

        command.setDeleteTeamMembers(true);

        command.execute();
        command.undo();

        // check to see if team was deleted and restored and all ppl remained in project
        Assert.assertThat(organisation.getTeams(), CoreMatchers.hasItem(teamWithMembers));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with no members is re-deleted from the project, on redo.
     */
    @Test
    public void redoEmptyTeam_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, organisation);

        command.execute();
        command.undo();
        command.redo();

        // check to see if team was deleted and all ppl remained in project
        Assert.assertThat(organisation.getTeams(), CoreMatchers.not(CoreMatchers.hasItem(teamNoMembers)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with members is re-deleted from the project but its members are retained, on redo.
     */
    @Test
    public void redoTeamWithMembersButKeepPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);

        command.execute();
        command.undo();
        command.redo();

        // check to see if team was deleted and all ppl remained in project
        Assert.assertThat(organisation.getTeams(), CoreMatchers.not(CoreMatchers.hasItem(teamWithMembers)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person1));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person2));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }

    /**
     * Tests that a team with members is re-deleted from the project and its members are re-deleted from the project when
     * the setDeleteMembers flag is set, on redo.
     */
    @Test
    public void redoTeamWithMembersAndPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);

        command.setDeleteTeamMembers(true);

        command.execute();
        command.undo();
        command.redo();

        // check to see if team and team members were deleted but all non-team members remained in project
        Assert.assertThat(organisation.getTeams(), CoreMatchers.not(CoreMatchers.hasItem(teamWithMembers)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person1)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.not(CoreMatchers.hasItem(person2)));
        Assert.assertThat(organisation.getPeople(), CoreMatchers.hasItem(person3));
    }
}

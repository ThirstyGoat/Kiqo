package seng302.group4.undo;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;

/**
 * Created by James on 11/04/15.
 */
public class DeleteTeamCommandTest {

    Person person1;
    Person person2;
    Person person3;
    private Project project;
    private Team teamWithMembers;
    private Team teamNoMembers;

    /**
     * Initialises the test environment.
     */
    @Before
    public void setUp() {
        project = new Project();

        // Create people
        person1 = new Person("one", "", "", "", "", "", "", new ArrayList<>());
        person2 = new Person("two", "", "", "", "", "", "", new ArrayList<>());
        person3 = new Person("three", "", "", "", "", "", "", new ArrayList<>());

        // Add people to project
        project.getPeople().add(person1);
        project.getPeople().add(person2);
        project.getPeople().add(person3);

        // Create a team
        teamWithMembers = new Team("teamOne", "", new ArrayList<>());
        teamNoMembers = new Team("teamOne", "", new ArrayList<>());

        // Populate team members
        teamWithMembers.getTeamMembers().add(person1);
        teamWithMembers.getTeamMembers().add(person2);

        // Add teams to project
        project.getTeams().add(teamWithMembers);
        project.getTeams().add(teamNoMembers);
    }

    /**
     * Tests that a team with no members is successfully deleted from the project.
     */
    @Test
    public void deleteEmptyTeam_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, project);

        command.execute();

        // Check to see if team was deleted and all people remained
        assert !project.getTeams().contains(teamNoMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with members is deleted but its members are kept.
     */
    @Test
    public void deleteTeamWithMembersButKeepPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, project);

        command.execute();

        // Check to see if team was deleted and all people remained
        assert !project.getTeams().contains(teamWithMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with members is successfully deleted from the project, and with the setDeleteMembers flag its
     * members are also deleted from the project.
     */
    @Test
    public void deleteTeamWithMembersAndPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, project);

        command.setDeleteMembers();

        command.execute();

        // Check to see if team and all team members were deleted
        assert !project.getTeams().contains(teamWithMembers);
        assert !project.getPeople().contains(person1);
        assert !project.getPeople().contains(person2);
        assert project.getPeople().contains(person3); // person not in team remained
    }

    /**
     * Tests that a team with no members is re-added to the project, on undo.
     */
    @Test
    public void undoDeleteEmptyTeam() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, project);

        command.execute();
        command.undo();

        // check to see if team was deleted and restored
        assert project.getTeams().contains(teamNoMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with members has its members re-added and itself re-added to the project, on undo.
     */
    @Test
    public void undoTeamWithMembersButKeepPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, project);

        command.execute();
        command.undo();

        // check to see if team was deleted and restored and all ppl remained in project
        assert project.getTeams().contains(teamWithMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with members has its members re-added and itself re-added to the project, even when the
     * setDeleteMembers flag is set, on undo.
     */
    @Test
    public void undoTeamWithMembersAndPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, project);

        command.setDeleteMembers();

        command.execute();
        command.undo();

        // check to see if team was deleted and restored and all ppl remained in project
        assert project.getTeams().contains(teamWithMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with no members is re-deleted from the project, on redo.
     */
    @Test
    public void redoEmptyTeam_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, project);

        command.execute();
        command.undo();
        command.redo();

        // check to see if team was deleted and all ppl remained in project
        assert !project.getTeams().contains(teamNoMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with members is re-deleted from the project but its members are retained, on redo.
     */
    @Test
    public void redoTeamWithMembersButKeepPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, project);

        command.execute();
        command.undo();
        command.redo();

        // check to see if team was deleted and all ppl remained in project
        assert !project.getTeams().contains(teamWithMembers);
        assert project.getPeople().contains(person1);
        assert project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }

    /**
     * Tests that a team with members is re-deleted from the project and its members are re-deleted from the project
     * when the setDeleteMemebers flag is set, on redo.
     */
    @Test
    public void redoTeamWithMembersAndPeople_Success() {
        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, project);

        command.setDeleteMembers();

        command.execute();
        command.undo();
        command.redo();

        // check to see if team and team members were deleted but all non-team members remained in project
        assert !project.getTeams().contains(teamWithMembers);
        assert !project.getPeople().contains(person1);
        assert !project.getPeople().contains(person2);
        assert project.getPeople().contains(person3);
    }
}
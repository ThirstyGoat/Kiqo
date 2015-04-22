//package seng302.group4.undo;
//
//import java.util.ArrayList;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import seng302.group4.Person;
//import seng302.group4.Organisation;
//import seng302.group4.Team;
//
///**
// * Created by James on 11/04/15.
// */
//public class DeleteTeamCommandTest {
//
//    Person person1;
//    Person person2;
//    Person person3;
//    private Organisation organisation;
//    private Team teamWithMembers;
//    private Team teamNoMembers;
//
//    /**
//     * Initialises the test environment.
//     */
//    @Before
//    public void setUp() {
//        organisation = new Organisation();
//
//        // Create people
//        person1 = new Person("one", "", "", "", "", "", "", new ArrayList<>());
//        person2 = new Person("two", "", "", "", "", "", "", new ArrayList<>());
//        person3 = new Person("three", "", "", "", "", "", "", new ArrayList<>());
//
//        // Add people to project
//        organisation.getPeople().add(person1);
//        organisation.getPeople().add(person2);
//        organisation.getPeople().add(person3);
//
//        // Create a team
//        teamWithMembers = new Team("teamOne", "", new ArrayList<>());
//        teamNoMembers = new Team("teamOne", "", new ArrayList<>());
//
//        // Populate team members
//        teamWithMembers.getTeamMembers().add(person1);
//        teamWithMembers.getTeamMembers().add(person2);
//
//        // Add teams to project
//        organisation.getTeams().add(teamWithMembers);
//        organisation.getTeams().add(teamNoMembers);
//    }
//
//    /**
//     * Tests that a team with no members is successfully deleted from the project.
//     */
//    @Test
//    public void deleteEmptyTeam_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, organisation);
//
//        command.execute();
//
//        // Check to see if team was deleted and all people remained
//        assert !organisation.getTeams().contains(teamNoMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with members is deleted but its members are kept.
//     */
//    @Test
//    public void deleteTeamWithMembersButKeepPeople_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);
//
//        command.execute();
//
//        // Check to see if team was deleted and all people remained
//        assert !organisation.getTeams().contains(teamWithMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with members is successfully deleted from the project, and with the setDeleteMembers flag its
//     * members are also deleted from the project.
//     */
//    @Test
//    public void deleteTeamWithMembersAndPeople_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);
//
//        command.setDeleteMembers();
//
//        command.execute();
//
//        // Check to see if team and all team members were deleted
//        assert !organisation.getTeams().contains(teamWithMembers);
//        assert !organisation.getPeople().contains(person1);
//        assert !organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3); // person not in team remained
//    }
//
//    /**
//     * Tests that a team with no members is re-added to the project, on undo.
//     */
//    @Test
//    public void undoDeleteEmptyTeam() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, organisation);
//
//        command.execute();
//        command.undo();
//
//        // check to see if team was deleted and restored
//        assert organisation.getTeams().contains(teamNoMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with members has its members re-added and itself re-added to the project, on undo.
//     */
//    @Test
//    public void undoTeamWithMembersButKeepPeople_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);
//
//        command.execute();
//        command.undo();
//
//        // check to see if team was deleted and restored and all ppl remained in project
//        assert organisation.getTeams().contains(teamWithMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with members has its members re-added and itself re-added to the project, even when the
//     * setDeleteMembers flag is set, on undo.
//     */
//    @Test
//    public void undoTeamWithMembersAndPeople_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);
//
//        command.setDeleteMembers();
//
//        command.execute();
//        command.undo();
//
//        // check to see if team was deleted and restored and all ppl remained in project
//        assert organisation.getTeams().contains(teamWithMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with no members is re-deleted from the project, on redo.
//     */
//    @Test
//    public void redoEmptyTeam_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamNoMembers, organisation);
//
//        command.execute();
//        command.undo();
//        command.redo();
//
//        // check to see if team was deleted and all ppl remained in project
//        assert !organisation.getTeams().contains(teamNoMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with members is re-deleted from the project but its members are retained, on redo.
//     */
//    @Test
//    public void redoTeamWithMembersButKeepPeople_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);
//
//        command.execute();
//        command.undo();
//        command.redo();
//
//        // check to see if team was deleted and all ppl remained in project
//        assert !organisation.getTeams().contains(teamWithMembers);
//        assert organisation.getPeople().contains(person1);
//        assert organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//
//    /**
//     * Tests that a team with members is re-deleted from the project and its members are re-deleted from the project
//     * when the setDeleteMemebers flag is set, on redo.
//     */
//    @Test
//    public void redoTeamWithMembersAndPeople_Success() {
//        final DeleteTeamCommand command = new DeleteTeamCommand(teamWithMembers, organisation);
//
//        command.setDeleteMembers();
//
//        command.execute();
//        command.undo();
//        command.redo();
//
//        // check to see if team and team members were deleted but all non-team members remained in project
//        assert !organisation.getTeams().contains(teamWithMembers);
//        assert !organisation.getPeople().contains(person1);
//        assert !organisation.getPeople().contains(person2);
//        assert organisation.getPeople().contains(person3);
//    }
//}
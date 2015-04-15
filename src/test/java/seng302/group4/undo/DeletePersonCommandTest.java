package seng302.group4.undo;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeletePersonCommandTest {

    private Project project;
    private Team team;

    @Before
    public void setUp() {
        project = new Project();
        team = new Team("", "", new ArrayList<>());
    }

    @Test
    public void deletePerson_PersonRemovedFromProject() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
    }

    @Test
    public void deletePerson_PersonRemovedFromTeam() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
    }

    @Test
    public void deletePersonWithPORole_PersonRemovedFromTeamAndPORole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.setProductOwner(person);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
        assert team.getProductOwner() != person;
    }

    @Test
    public void deletePersonWithDevRole_PersonRemovedFromTeamAndDevRole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.getDevTeam().add(person);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
        assert !team.getDevTeam().contains(person);
    }

    @Test
    public void undoDeletePerson_PersonAddedBackToProject() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);

        command.undo();

        assert project.getPeople().contains(person);
    }

    @Test
    public void undoDeletePersonInTeam_PersonAddedBackToTeam() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);

        command.undo();

        assert project.getPeople().contains(person);
        assert team.getTeamMembers().contains(person);
    }

    @Test
    public void undoDeletePersonInTeamWithPORole_PersonAddedBackToTeamWithRole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.setProductOwner(person);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
        assert team.getProductOwner() != person;

        command.undo();

        assert project.getPeople().contains(person);
        assert team.getTeamMembers().contains(person);
        assert team.getProductOwner() == person;
    }

    @Test
    public void undoDeletePersonInTeamWithDevRole_PersonAddedBackToTeamWithDevRole() {
        // Create new person
        final Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.getDevTeam().add(person);

        // Create command
        final DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
        assert !team.getDevTeam().contains(person);

        command.undo();

        assert project.getPeople().contains(person);
        assert team.getTeamMembers().contains(person);
        assert team.getDevTeam().contains(person);
    }
}
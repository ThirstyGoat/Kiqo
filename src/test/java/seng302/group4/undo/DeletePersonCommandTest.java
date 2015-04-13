package seng302.group4.undo;

import org.junit.Test;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;

import java.util.ArrayList;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeletePersonCommandTest {

    private Project project;
    private Team team;

    public void setUp() {
        project = new Project();
        team = new Team("", "", new ArrayList<>());

    }

    @Test
    public void deletePerson_PersonRemovedFromProject() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
    }

    @Test
    public void deletePerson_PersonRemovedFromTeam() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
    }

    @Test
    public void deletePersonWithPORole_PersonRemovedFromTeamAndPORole() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.setProductOwner(person);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
        assert team.getProductOwner() != person;
    }

    @Test
    public void deletePersonWithDevRole_PersonRemovedFromTeamAndDevRole() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.getDevTeam().add(person);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);
        assert !team.getDevTeam().contains(person);
    }

    @Test
    public void undoDeletePerson_PersonAddedBackToProject() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);

        command.undo();

        assert project.getPeople().contains(person);
    }

    @Test
    public void undoDeletePersonInTeam_PersonAddedBackToTeam() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
        command.execute();

        assert !project.getPeople().contains(person);
        assert !team.getTeamMembers().contains(person);

        command.undo();

        assert project.getPeople().contains(person);
        assert team.getTeamMembers().contains(person);
    }

    @Test
    public void undoDeletePersonInTeamWithPORole_PersonAddedBackToTeamWithRole() {
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.setProductOwner(person);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
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
        setUp();
        // Create new person
        Person person = new Person("", "", "", "", "", "", "", new ArrayList<>());
        project.getPeople().add(person);

        team.getTeamMembers().add(person);
        person.setTeam(team);
        team.getDevTeam().add(person);

        // Create command
        DeletePersonCommand command = new DeletePersonCommand(person, project);
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
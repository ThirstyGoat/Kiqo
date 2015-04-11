package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;

import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Created by james on 11/04/15.
 */
public class DeleteTeamCommand extends Command<Team> {
    private Project project;
    private Team team;
    private ArrayList<Person> teamMembers = new ArrayList<>();


    /**
     *
     * @param team the team to be deleted
     * @param project the project to delete the team from
     */
    public DeleteTeamCommand(final Team team, final Project project) {
        this.project = project;
        this.team = team;
    }

    /**
     * Sets the members of the team to be deleted
     */
    public void setDeleteMembers() {
        teamMembers.addAll(team.getTeamMembers().stream().collect(Collectors.toList()));
    }

    @Override
    public Team execute() {
        // Set team members' team field to null
        for (Person person : team.getTeamMembers()) {
            person.setTeam(null);
        }

        // if setDeleteMembers was called, delete each team member
        for (Person person : teamMembers) {
            System.out.println("Deleting: " + person.getShortName());
            project.getPeople().remove(person);
        }

        // delete the team
        project.getTeams().remove(team);
        return team;
    }

    @Override
    public void undo() {
        // Set team members team field to this team
        for (Person person : team.getTeamMembers()) {
            person.setTeam(team);
        }

        for (Person person : teamMembers) {
            System.out.println("Undoing deletion of: " + person.getShortName());
            project.getPeople().add(person);
        }

        project.getTeams().add(team);

    }

    @Override
    public String toString() {
        return "<Delete Team: \"" + team.getShortName() + "\">";
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public String getType() {
        return "Delete Team";
    }
}

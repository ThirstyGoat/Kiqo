package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Organisation;
import seng302.group4.Team;

import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Created by james on 11/04/15.
 */
public class DeleteTeamCommand extends Command<Team> {
    private final Organisation organisation;
    private final Team team;
    private final ArrayList<Person> teamMembers = new ArrayList<>();


    /**
     *
     * @param team the team to be deleted
     * @param organisation the project to delete the team from
     */
    public DeleteTeamCommand(final Team team, final Organisation organisation) {
        this.organisation = organisation;
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
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(null);
        }

        // if setDeleteMembers was called, delete each team member
        for (final Person person : teamMembers) {
            System.out.println("Deleting: " + person.getShortName());
            organisation.getPeople().remove(person);
        }

        // delete the team
        organisation.getTeams().remove(team);
        return team;
    }

    
    @Override
    public void undo() {
        // Set team members team field to this team
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(team);
        }

        for (final Person person : teamMembers) {
            System.out.println("Undoing deletion of: " + person.getShortName());
            organisation.getPeople().add(person);
        }

        organisation.getTeams().add(team);

    }

    @Override
    public String toString() {
        return "<Delete Team: \"" + team.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Team";
    }
}

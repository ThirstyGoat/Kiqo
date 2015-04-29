package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Organisation;
import seng302.group4.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by james on 11/04/15.
 */
public class DeleteTeamCommand extends Command<Team> {
    private final Organisation organisation;
    private final Team team;
    // Hash map of people in the team to be deleted, and the index at which the person appears in people list of
    // the organisation
    private final Map<Person, Integer> teamMembers = new HashMap<>();

    private int organisationIndex;

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
        for (Person person : team.getTeamMembers()) {
            teamMembers.put(person, organisation.getPeople().indexOf(person));
        }
    }

    @Override
    public Team execute() {
        // Set team members' team field to null
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(null);
        }

        // if setDeleteMembers was called, delete each team member
        for (Person person : teamMembers.keySet()) {
            organisation.getPeople().remove(person);
        }

        // delete the team
        organisationIndex = organisation.getTeams().indexOf(team);
        organisation.getTeams().remove(team);
        return team;
    }

    
    @Override
    public void undo() {
        // Set team members team field to this team
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(team);
        }

        for (Map.Entry<Person, Integer> entry : teamMembers.entrySet()) {
            organisation.getPeople().add(entry.getValue(), entry.getKey());
        }

        organisation.getTeams().add(organisationIndex, team);

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

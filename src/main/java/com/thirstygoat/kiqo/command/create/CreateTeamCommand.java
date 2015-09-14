package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;

/**
 * Command to create a project
 *
 * @author amy
 */
public class CreateTeamCommand extends CreateCommand {
    private final Organisation organisation;
    private Team team;

    /**
     * Constructor
     *
     * @param team         team to be "created"
     * @param organisation organisation to which this team belongs
     */
    public CreateTeamCommand(Team team, Organisation organisation) {
        super(team);
        this.team = team;
        this.organisation = organisation;
    }

    @Override
    public void addToModel() {
        // Assign this team to all the people in the team
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(team);
        }

        // Add team to project
        organisation.getTeams().add(team);
    }

    @Override
    public void removeFromModel() {
        // Goodbye team
        organisation.getTeams().remove(team);

        // Remove this team from all the people within it
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(null);
        }
    }

    @Override
    public String toString() {
        return "<Create Team: \"" + team.toString() + "\">";
    }

    @Override
    public String getType() {
        return "Create Team";
    }

}

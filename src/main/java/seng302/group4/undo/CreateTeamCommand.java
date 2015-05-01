package seng302.group4.undo;

import java.util.ArrayList;
import java.util.List;

import seng302.group4.Organisation;
import seng302.group4.Person;
import seng302.group4.Team;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreateTeamCommand extends Command<Team> {
    private final String shortName;
    private final String description;
    private final List<Person> teamMembers = new ArrayList<>();
    private final Person productOwner;
    private final Person scrumMaster;
    private final List<Person> devTeam;

    private Team team;
    private final Organisation organisation;

    /**
     * Constructor for CreateTeamCommand
     *
     * @param shortName Short name of the team
     * @param description Description of the team
     * @param teamMembers List of people to be in the team
     * @param productOwner Person in the Product Owner role for the team
     * @param scrumMaster Person in the Scrum Master role for the team
     * @param devTeam List of people in development roles for the team
     * @param organisation organisation to which this team belongs
     */
    public CreateTeamCommand(final String shortName, final String description, final List<Person> teamMembers, final Person productOwner,
            final Person scrumMaster, final List<Person> devTeam,
                             final Organisation organisation) {
        this.shortName = shortName;
        this.description = description;
        this.teamMembers.addAll(teamMembers);
        this.productOwner = productOwner;
        this.scrumMaster = scrumMaster;
        this.devTeam = devTeam;
        this.organisation = organisation;
    }

    @Override
    public Team execute() {
        if (team == null) {
            team = new Team(shortName, description, teamMembers);
            team.setProductOwner(productOwner);
            team.setScrumMaster(scrumMaster);
            team.setDevTeam(devTeam);
        }

        // Assign this team to all the people in the team
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(team);
        }

        // Add team to project
        organisation.getTeams().add(team);
        return team;
    }

    @Override
    public void undo() {
        // Goodbye team
        organisation.getTeams().remove(team);

        // Remove this team from all the people within it
        for (final Person person : team.getTeamMembers()) {
            person.setTeam(null);
        }
    }

    @Override
    public String toString() {
        return "<Create Team: \"" + shortName + "\">";
    }

    @Override
    public String getType() {
        return "Create Team";
    }

}

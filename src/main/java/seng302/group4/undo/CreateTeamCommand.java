package seng302.group4.undo;

import java.util.ArrayList;

import seng302.group4.Person;
import seng302.group4.Project;
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
    private final ArrayList<Person> teamMembers = new ArrayList<>();
    private final Person productOwner;
    private final Person scrumMaster;
    private final ArrayList<Person> devTeam;

    private Team team;
    private final Project project;

    /**
     * Constructor for CreateTeamCommand
     * @param shortName Short name of the team
     * @param description Description of the team
     * @param teamMembers ArrayList of people to be in the team
     */
    public CreateTeamCommand(final String shortName, final String description, final ArrayList<Person> teamMembers,
                             final Person productOwner, final Person scrumMaster, final ArrayList<Person> devTeam,
                             final Project project) {
        this.shortName = shortName;
        this.description = description;
        this.teamMembers.addAll(teamMembers);
        this.productOwner = productOwner;
        this.scrumMaster = scrumMaster;
        this.devTeam = devTeam;
        this.project = project;
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
        project.getTeams().add(team);
        return team;
    }

    @Override
    public void undo() {
        // Goodbye team
        project.getTeams().remove(team);

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

package seng302.group4.undo;

import seng302.group4.Organisation;
import seng302.group4.Person;
import seng302.group4.Team;

/**
 * Command to delete a person from a project.
 *
 */
public class DeletePersonCommand extends Command<Person> {
    private final Organisation organisation;
    private final Person person;
    private final Team team;
    private boolean isProductOwner = false;
    private boolean isScrumMaster = false;
    private boolean inDevTeam = true;

    private int index;

    /**
     * @param person Person to be deleted
     * @param organisation organisation to which the person belongs
     */
    public DeletePersonCommand(final Person person, final Organisation organisation) {
        this.person = person;
        this.organisation = organisation;
        team = person.getTeam();
        if (team != null) {
            if (team.getProductOwner() == person) {
                isProductOwner = true;
            }
            if (team.getScrumMaster() == person) {
                isScrumMaster = true;
            }
            if (team.observableDevTeam().contains(person)) {
                inDevTeam = true;
            }
        }
    }

    @Override
    public Person execute() {
        // Remove the person first from their team, working the way up the hierarchy
        if (team != null) {
            // Remove person from PO/SM/Dev Role if they're there, and remember where they are
            if (isProductOwner) {
                team.setProductOwner(null);
            }
            if (isScrumMaster) {
                team.setScrumMaster(null);
            }
            if (inDevTeam) {
                team.observableDevTeam().remove(person);
            }

            // Remove person from Team Members
            team.observableTeamMembers().remove(person);
        }

        // We keep track of the position in the list that the person was in, so they can be added back to the same
        // position, not at the bottom
        index = organisation.getPeople().indexOf(person);

        // Remove the person from the project
        organisation.getPeople().remove(person);
        return person;
    }

    @Override
    public void undo() {
        // Repeat execute steps backwards
        // Add person back to project at appropriate index
        organisation.getPeople().add(index, person);

        if (team != null) {
            // Add person to Team Members
            team.observableTeamMembers().add(person);

            if (isProductOwner) {
                team.setProductOwner(person);
            }
            if (isScrumMaster) {
                team.setScrumMaster(person);
            }
            if (inDevTeam) {
                team.observableDevTeam().add(person);
            }
        }
    }

    @Override
    public String toString() {
        return "<Delete Person: \"" + person.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Person";
    }
}

package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.exceptions.InvalidPersonDeletionException;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.Utilities;

/**
 * Command to delete a person from a project.
 *
 */
public class DeletePersonCommand extends DeleteCommand {
    private final Organisation organisation;
    private final Person person;
    private final Team team;
    private boolean isProductOwner = false;
    private boolean isScrumMaster = false;
    private boolean inDevTeam = false;

    private int index;

    /**
     * @param person Person to be deleted
     * @param organisation organisation to which the person belongs
     * @throws InvalidPersonDeletionException person is PO of one or more backlogs
     */
    public DeletePersonCommand(final Person person, final Organisation organisation) throws InvalidPersonDeletionException {
        super(person);
        this.person = person;
        this.organisation = organisation;

        if (!canDeletePerson()) {
            throw new InvalidPersonDeletionException(person);
        }
        team = person.getTeam();
        if (team != null) {
            if (team.getProductOwner() == person) {
                isProductOwner = true;
            }
            if (team.getScrumMaster() == person) {
                isScrumMaster = true;
            }
            if (team.getDevTeam().contains(person)) {
                inDevTeam = true;
            }
        }
    }

    private boolean canDeletePerson() {
        // Return whether or not the person is PO of one or more backlogs
        return !Utilities.isPersonPoOfBacklog(person, organisation);
    }

    @Override
    public void removeFromModel() {
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
                team.getDevTeam().remove(person);
            }

            // Remove person from Team Members
            team.getTeamMembers().remove(person);
        }

        // We keep track of the position in the list that the person was in, so they can be added back to the same
        // position, not at the bottom
        index = organisation.getPeople().indexOf(person);

        // Remove the person from the project
        organisation.getPeople().remove(person);
    }

    @Override
    public void addToModel() {
        // Repeat execute steps backwards
        // Add person back to project at appropriate index
        organisation.getPeople().add(index, person);

        if (team != null) {
            // Add person to Team Members
            team.getTeamMembers().add(person);

            if (isProductOwner) {
                team.setProductOwner(person);
            }
            if (isScrumMaster) {
                team.setScrumMaster(person);
            }
            if (inDevTeam) {
                team.getDevTeam().add(person);
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

package com.thirstygoat.kiqo.command.delete;

import java.util.ArrayList;
import java.util.List;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.exceptions.InvalidPersonDeletionException;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;


/**
 * Created by james on 11/04/15.
 */
public class DeleteTeamCommand extends DeleteCommand {
    private final Organisation organisation;
    private final Team team;
    private final List<DeletePersonCommand> deletePersonCommands = new ArrayList<>();
    private int organisationIndex;
    private boolean deleteTeamMembers = false;

    /**
     * @param team the team to be deleted
     * @param organisation the project to delete the team from
     */
    public DeleteTeamCommand(final Team team, final Organisation organisation) {
        super(team);
        this.team = team;
        this.organisation = organisation;

        addDeletePersonCommands();
    }

    public boolean canDeleteTeamMembers() {
        return !deletePersonCommands.isEmpty();
    }

    public void setDeleteTeamMembers(boolean deleteTeamMembers) {
        this.deleteTeamMembers = deleteTeamMembers;
    }

    /**
     * Sets the members of the team to be deleted
     */
    public void addDeletePersonCommands() {
        // Add new delete people commands
        for (Person person : team.observableTeamMembers()) {
            try {
                deletePersonCommands.add(new DeletePersonCommand(person, organisation));
            } catch (InvalidPersonDeletionException e) {
                // Then one of the people can't be deleted
                deletePersonCommands.clear();
                break;
            }
        }
    }

    @Override
    public void removeFromModel() {
        // If we are deleting the team members as well, their team field can stay set to this team
        if (deleteTeamMembers) {
            deletePersonCommands.forEach(Command::execute);
        } else {
            // If we aren't deleting the team members, at the very least, we have to set their teams to null
            for (Person person : team.observableTeamMembers()) {
                person.setTeam(null);
            }
        }

        // delete the team
        organisationIndex = organisation.getTeams().indexOf(team);
        organisation.getTeams().remove(team);
    }


    @Override
    public void addToModel() {
        if (deleteTeamMembers) {
            deletePersonCommands.forEach(Command::undo);
        } else {
            // If the team members weren't deleted, we need to update their team to be this team now
            for (Person person : team.observableTeamMembers()) {
                person.setTeam(team);
            }
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

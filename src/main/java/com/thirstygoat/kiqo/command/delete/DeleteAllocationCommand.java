package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.search.SearchableItems;

/**
 * Command to delete a person from a project.
 *
 */
public class DeleteAllocationCommand extends Command {
    private final Allocation allocation;
    private final Project project;
    private final Team team;

    private int projectIndex;
    private int teamIndex;

    /**
     * @param allocation Allocation to be deleted
     */
    public DeleteAllocationCommand(final Allocation allocation) {
        this.allocation = allocation;
        project = allocation.getProject();
        team = allocation.getTeam();
    }

    @Override
    public void execute() {
        projectIndex = project.getAllocations().indexOf(allocation);
        project.observableAllocations().remove(allocation);

        teamIndex = team.getAllocations().indexOf(allocation);
        team.observableAllocations().remove(allocation);

        // Remove from SearchableItems
        SearchableItems.getInstance().removeSearchable(allocation);
    }

    @Override
    public void undo() {
        project.observableAllocations().add(projectIndex, allocation);
        team.observableAllocations().add(teamIndex, allocation);

        // Add back to SearchableItems
        SearchableItems.getInstance().addSearchable(allocation);
    }

    @Override
    public String toString() {
        return "<Delete Allocation of Team: \"" + team.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Allocation";
    }
}

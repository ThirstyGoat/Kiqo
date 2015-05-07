package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;

/**
 * Command to delete a person from a project.
 *
 */
public class DeleteAllocationCommand extends Command<Allocation> {
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
    public Allocation execute() {
        projectIndex = project.getAllocations().indexOf(allocation);
        project.observableAllocations().remove(allocation);

        teamIndex = team.getAllocations().indexOf(allocation);
        team.observableAllocations().remove(allocation);
        return allocation;
    }

    @Override
    public void undo() {
        project.observableAllocations().add(projectIndex, allocation);
        team.observableAllocations().add(teamIndex, allocation);
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

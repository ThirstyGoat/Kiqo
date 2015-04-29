package seng302.group4.undo;

import seng302.group4.*;

/**
 * Command to delete a person from a project.
 *
 */
public class DeleteAllocationCommand extends Command<Allocation> {
    private Allocation allocation;
    private Project project;
    private Team team;

    private int projectIndex;
    private int teamIndex;

    /**
     * @param allocation Allocation to be deleted
     */
    public DeleteAllocationCommand(final Allocation allocation) {
        this.allocation = allocation;
        this.project = allocation.getProject();
        this.team = allocation.getTeam();
    }

    @Override
    public Allocation execute() {
        projectIndex = project.getAllocations().indexOf(allocation);
        project.getAllocations().remove(allocation);

        teamIndex = team.getAllocations().indexOf(allocation);
        team.getAllocations().remove(allocation);
        return allocation;
    }

    @Override
    public void undo() {
        project.getAllocations().add(projectIndex, allocation);
        team.getAllocations().add(teamIndex, allocation);
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

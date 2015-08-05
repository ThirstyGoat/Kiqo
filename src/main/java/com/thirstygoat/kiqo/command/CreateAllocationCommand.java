package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;

/**
 * Created by bradley on 23/04/15.
 */
public class CreateAllocationCommand extends Command {
    private final Allocation allocation;
    private final Project project;
    private final Team team;

    public CreateAllocationCommand(Allocation allocation) {
        this.allocation = allocation;
        team = allocation.getTeam();
        project = allocation.getProject();
    }

    @Override
    public void execute() {
        project.observableAllocations().add(allocation);
        team.observableAllocations().add(allocation);
    }

    @Override
    public void undo() {
        team.observableAllocations().remove(allocation);
        project.observableAllocations().remove(allocation);
    }

    @Override
    public String toString() {
        return "<Create Allocation of Team: \"" + team.getShortName() + "\">";
    }


    @Override
    public String getType() {
        return "Allocate Team";
    }
}
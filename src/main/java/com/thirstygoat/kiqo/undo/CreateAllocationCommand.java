package com.thirstygoat.kiqo.undo;

import com.thirstygoat.kiqo.Allocation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.Team;

/**
 * Created by bradley on 23/04/15.
 */
public class CreateAllocationCommand extends Command<Allocation> {
    private Allocation allocation;
    private Project project;
    private Team team;

    public CreateAllocationCommand(Allocation allocation) {
        this.allocation = allocation;
        this.team = allocation.getTeam();
        this.project = allocation.getProject();
    }

    @Override
    public Allocation execute() {
        project.getAllocations().add(allocation);
        team.getAllocations().add(allocation);
        return null;
    }

    @Override
    public void undo() {
        team.getAllocations().remove(allocation);
        project.getAllocations().remove(allocation);
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
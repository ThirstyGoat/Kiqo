package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.create.CreateAllocationCommand;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by bradley on 23/04/15.
 */
public class CreateAllocationCommandTest {

    private Team team;
    private Project project;
    private Allocation allocation;
    private CreateAllocationCommand command;

    @Before
    public void setUp() {
        team = new Team("", "", new ArrayList<>());
        project = new Project("", "");
        allocation = new Allocation(team, LocalDate.of(2015, 1, 1), LocalDate.of(2016, 1, 1), project);
        command = new CreateAllocationCommand(allocation);
    }

    @Test
    public void createAllocation_AllocationAdded() {
        command.execute();

        Assert.assertTrue(team.getAllocations().contains(allocation));
        Assert.assertTrue(project.getAllocations().contains(allocation));
    }

    @Test
    public void undoCreateAllocation_AllocationRemoved() {
        command.execute();
        command.undo();

        Assert.assertFalse(team.getAllocations().contains(allocation));
        Assert.assertFalse(project.getAllocations().contains(allocation));
    }
}

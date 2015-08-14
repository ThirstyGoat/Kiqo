package com.thirstygoat.kiqo.command;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.command.create.CreateAllocationCommand;
import com.thirstygoat.kiqo.command.delete.DeleteAllocationCommand;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;

/**
 * Created by bradley on 23/04/15.
 */
public class DeleteAllocationCommandTest {

    private Allocation allocation;
    private Team team;
    private Project project;
    private DeleteAllocationCommand command;

    @Before
    public void setup() {
        team = new Team("", "", new ArrayList<>());
        project = new Project("", "");
        allocation = new Allocation(team, LocalDate.now(), LocalDate.of(2016, 1, 1), project);
        new CreateAllocationCommand(allocation).execute();
        command = new DeleteAllocationCommand(allocation);
    }

    @Test
    public void deleteAllocation_AllocationRemoved() {
        Assert.assertTrue(project.getAllocations().contains(allocation));
        Assert.assertTrue(team.getAllocations().contains(allocation));

        command.execute();

        Assert.assertFalse(project.getAllocations().contains(allocation));
        Assert.assertFalse(team.getAllocations().contains(allocation));
    }

    @Test
    public void undoDeleteAllocation_AllocationAddedBack() {
        command.execute();

        Assert.assertFalse(project.getAllocations().contains(allocation));
        Assert.assertFalse(team.getAllocations().contains(allocation));

        command.undo();

        Assert.assertTrue(project.getAllocations().contains(allocation));
        Assert.assertTrue(team.getAllocations().contains(allocation));
    }
}

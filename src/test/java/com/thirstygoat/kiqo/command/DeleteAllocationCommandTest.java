package com.thirstygoat.kiqo.command;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.command.CreateAllocationCommand;
import com.thirstygoat.kiqo.command.DeleteAllocationCommand;
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
        assert project.getAllocations().contains(allocation);
        assert team.getAllocations().contains(allocation);

        command.execute();

        assert !project.getAllocations().contains(allocation);
        assert !team.getAllocations().contains(allocation);
    }

    @Test
    public void undoDeleteAllocation_AllocationAddedBack() {
        command.execute();

        assert !project.getAllocations().contains(allocation);
        assert !team.getAllocations().contains(allocation);

        command.undo();

        assert project.getAllocations().contains(allocation);
        assert team.getAllocations().contains(allocation);
    }
}

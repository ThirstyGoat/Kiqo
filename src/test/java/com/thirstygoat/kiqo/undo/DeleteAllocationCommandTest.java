package com.thirstygoat.kiqo.undo;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.Allocation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.Team;
import com.thirstygoat.kiqo.undo.CreateAllocationCommand;
import com.thirstygoat.kiqo.undo.DeleteAllocationCommand;

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

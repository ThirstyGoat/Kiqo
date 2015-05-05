package com.thirstygoat.kiqo.undo;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.Allocation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.Team;
import com.thirstygoat.kiqo.undo.CreateAllocationCommand;

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

        assert team.getAllocations().contains(allocation);
        assert project.getAllocations().contains(allocation);
    }

    @Test
    public void undoCreateAllocation_AllocationRemoved() {
        command.execute();
        command.undo();

        assert !team.getAllocations().contains(allocation);
        assert !project.getAllocations().contains(allocation);
    }
}

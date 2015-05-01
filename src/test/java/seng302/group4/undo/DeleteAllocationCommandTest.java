package seng302.group4.undo;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import seng302.group4.Allocation;
import seng302.group4.Project;
import seng302.group4.Team;

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

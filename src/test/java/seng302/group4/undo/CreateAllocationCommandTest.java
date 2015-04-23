package seng302.group4.undo;

import org.junit.Test;
import seng302.group4.Allocation;
import seng302.group4.Project;
import seng302.group4.Team;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by bradley on 23/04/15.
 */
public class CreateAllocationCommandTest {

    @Test
    public void createAllocation_AllocationAdded() {
        Team team = new Team("", "", new ArrayList<>());
        Project project = new Project("", "");

        Allocation allocation = new Allocation(team, LocalDate.now(), LocalDate.of(2016, 1, 1), project);

        CreateAllocationCommand command = new CreateAllocationCommand(allocation);

        command.execute();

        assert team.getAllocations().contains(allocation);
        assert project.getAllocations().contains(allocation);
    }

    @Test
    public void undoCreateAllocation_AllocationRemoved() {
        Team team = new Team("", "", new ArrayList<>());
        Project project = new Project("", "");

        Allocation allocation = new Allocation(team, LocalDate.now(), LocalDate.of(2016, 1, 1), project);

        CreateAllocationCommand command = new CreateAllocationCommand(allocation);

        command.execute();

        assert team.getAllocations().contains(allocation);
        assert project.getAllocations().contains(allocation);

        command.undo();

        assert !team.getAllocations().contains(allocation);
        assert !project.getAllocations().contains(allocation);
    }
}

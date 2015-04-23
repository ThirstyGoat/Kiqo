package seng302.group4.undo;

import org.junit.Before;
import org.junit.Test;
import seng302.group4.Allocation;
import seng302.group4.Project;
import seng302.group4.Team;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by bradley on 23/04/15.
 */
public class DeleteAllocationCommandTest {

    private Allocation allocation;
    private Team team;
    private Project project;

    @Before
    public void setup() {
        team = new Team("", "", new ArrayList<>());
        project = new Project("", "");

        allocation = new Allocation(team, LocalDate.now(), LocalDate.of(2016, 1, 1), project);

        new CreateAllocationCommand(allocation).execute();
    }

    @Test
    public void deleteAllocation_AllocationRemoved() {
        DeleteAllocationCommand command = new DeleteAllocationCommand(allocation);

        assert project.getAllocations().contains(allocation);
        assert team.getAllocations().contains(allocation);

        command.execute();

        assert !project.getAllocations().contains(allocation);
        assert !team.getAllocations().contains(allocation);
    }

    @Test
    public void undoDeleteAllocation_AllocationAddedBack() {
        DeleteAllocationCommand command = new DeleteAllocationCommand(allocation);

        assert project.getAllocations().contains(allocation);
        assert team.getAllocations().contains(allocation);

        command.execute();

        assert !project.getAllocations().contains(allocation);
        assert !team.getAllocations().contains(allocation);

        command.undo();

        assert project.getAllocations().contains(allocation);
        assert team.getAllocations().contains(allocation);
    }
}

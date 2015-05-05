package com.thirstygoat.kiqo.undo;

import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.Organisation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.undo.DeleteProjectCommand;

/**
 * Created by Bradley on 23/04/15.
 */
public class DeleteProjectCommandTest {
    private Organisation organisation;
    private Project project;

    @Before
    public void setup() {
        organisation = new Organisation();
        project = new Project("", "");

        organisation.getProjects().add(project);
    }
    @Test
    public void deleteProject_ProjectRemoved() {
        DeleteProjectCommand command = new DeleteProjectCommand(project, organisation);

        assert organisation.getProjects().contains(project);

        command.execute();

        assert !organisation.getProjects().contains(project);
    }

    @Test
    public void undoDeleteAllocation_AllocationAddedBack() {
        DeleteProjectCommand command = new DeleteProjectCommand(project, organisation);

        assert organisation.getProjects().contains(project);

        command.execute();

        assert !organisation.getProjects().contains(project);

        command.undo();

        assert organisation.getProjects().contains(project);
    }
}

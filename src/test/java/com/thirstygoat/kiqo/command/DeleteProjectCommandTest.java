package com.thirstygoat.kiqo.command;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;

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
        final DeleteProjectCommand command = new DeleteProjectCommand(project, organisation);

        Assert.assertTrue(organisation.getProjects().contains(project));

        command.execute();

        Assert.assertFalse(organisation.getProjects().contains(project));
    }

    @Test
    public void undoDeleteAllocation_AllocationAddedBack() {
        final DeleteProjectCommand command = new DeleteProjectCommand(project, organisation);

        Assert.assertTrue(organisation.getProjects().contains(project));

        command.execute();

        Assert.assertFalse(organisation.getProjects().contains(project));

        command.undo();

        Assert.assertTrue(organisation.getProjects().contains(project));
    }
}

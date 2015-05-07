package com.thirstygoat.kiqo.command;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;

/**
 * Created by bradley on 14/04/15.
 */
public class CreateReleaseCommandTest {
    private Project project;
    private Release release;
    private CreateReleaseCommand command;

    @Before
    public void setup() {
        project = new Project("proj", "Project");
        release = new Release("", project, LocalDate.now(), "");
        command = new CreateReleaseCommand(release);
    }

    @Test
    public void createRelease_ReleaseAddedToProject() {
        Assert.assertFalse(project.getReleases().contains(release));

        command.execute();

        Assert.assertTrue(project.getReleases().contains(release));
    }

    @Test
    public void undoCreateRelease_ReleaseRemovedFromProject() {
        command.execute();
        command.undo();

        Assert.assertFalse(project.getReleases().contains(release));
    }
}

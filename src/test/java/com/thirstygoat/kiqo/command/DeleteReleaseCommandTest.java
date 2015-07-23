package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by bradley on 14/04/15.
 */
public class DeleteReleaseCommandTest {
    private Release release;
    private Project project;
    private DeleteReleaseCommand command;

    @Before
    public void setup() {
        project = new Project("", "");
        release = new Release("", project, LocalDate.now(), "");
        project.observableReleases().add(release);
        command = new DeleteReleaseCommand(release);
    }

    @Test
    public void deleteRelease_ReleaseRemovedFromProject() {
        Assert.assertTrue(project.getReleases().contains(release));

        command.execute();

        Assert.assertFalse(project.getReleases().contains(release));
    }

    @Test
    public void undoDeleteRelease_ReleaseAddedBackToProject() {
        Assert.assertTrue(project.getReleases().contains(release));

        command.execute();

        Assert.assertFalse(project.getReleases().contains(release));

        command.undo();

        Assert.assertTrue(project.getReleases().contains(release));
    }
}

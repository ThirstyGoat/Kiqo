package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.delete.DeleteSprintCommand;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Sprint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by bradley on 14/04/15.
 */
public class DeleteSprintCommandTest {
    private Release release;
    private Project project;
    private Sprint sprint;
    private DeleteSprintCommand command;

    @Before
    public void setup() {
        project = new Project("", "");
        release = new Release("", project, LocalDate.now(), "");
        project.observableReleases().add(release);
        sprint = new Sprint();
        release.getSprints().add(sprint);
        sprint.releaseProperty().set(release);
        command = new DeleteSprintCommand(sprint);
    }

    @Test
    public void deleteSprint_SprintRemovedFromRelease() {
        Assert.assertTrue(release.getSprints().contains(sprint));

        command.execute();

        Assert.assertFalse(release.getSprints().contains(sprint));
    }

    @Test
    public void undoDeleteSprint_SprintAddedBackToRelease() {
        Assert.assertTrue(release.getSprints().contains(sprint));

        command.execute();

        Assert.assertFalse(release.getSprints().contains(sprint));

        command.undo();

        Assert.assertTrue(release.getSprints().contains(sprint));
    }
}

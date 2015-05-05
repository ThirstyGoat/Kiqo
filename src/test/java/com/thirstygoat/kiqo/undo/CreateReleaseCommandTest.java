package com.thirstygoat.kiqo.undo;

import java.io.File;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.Organisation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.Release;
import com.thirstygoat.kiqo.undo.CreateReleaseCommand;

/**
 * Created by bradley on 14/04/15.
 */
public class CreateReleaseCommandTest {
    private Organisation organisation;
    private Project project;
    private Release release;
    private CreateReleaseCommand command;

    @Before
    public void setup() {
        organisation = new Organisation(new File(""));
        project = new Project("proj", "Project");
        release = new Release("", project, LocalDate.now(), "", organisation);
        command = new CreateReleaseCommand(release, organisation);
    }

    @Test
    public void createRelease_ReleaseAddedToProject() {
        assert !project.getReleases().contains(release);

        command.execute();

        assert project.getReleases().contains(release);
    }

    @Test
    public void undoCreateRelease_ReleaseRemovedFromProject() {
        command.execute();
        command.undo();

        assert !project.getReleases().contains(release);
    }
}

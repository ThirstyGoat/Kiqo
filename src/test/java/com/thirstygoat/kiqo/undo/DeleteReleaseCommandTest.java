package com.thirstygoat.kiqo.undo;

import java.io.File;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.Organisation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.Release;
import com.thirstygoat.kiqo.undo.DeleteReleaseCommand;

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
        release = new Release("", project, LocalDate.now(), "", new Organisation(new File("")));
        project.getReleases().add(release);
        command = new DeleteReleaseCommand(release);
    }

    @Test
    public void deleteRelease_ReleaseRemovedFromProject() {
        assert project.getReleases().contains(release);

        command.execute();

        assert !project.getReleases().contains(release);
    }

    @Test
    public void undoDeleteRelease_ReleaseAddedBackToProject() {
        assert project.getReleases().contains(release);

        command.execute();

        assert !project.getReleases().contains(release);

        command.undo();

        assert project.getReleases().contains(release);
    }
}

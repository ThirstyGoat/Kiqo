package seng302.group4.undo;

import org.junit.Test;
import seng302.group4.Project;
import seng302.group4.Release;

import java.time.LocalDate;

/**
 * Created by bradley on 14/04/15.
 */
public class CreateReleaseCommandTest {
    @Test
    public void createRelease_ReleaseAddedToProject() {
        Project project = new Project();
        Release release = new Release("", LocalDate.now(), "", project);

        CreateReleaseCommand command = new CreateReleaseCommand(release, project);

        assert !project.getReleases().contains(release);

        command.execute();

        assert project.getReleases().contains(release);
    }

    @Test
    public void undoCreateRelease_ReleaseRemovedFromProject() {
        Project project = new Project();
        Release release = new Release("", LocalDate.now(), "", project);

        CreateReleaseCommand command = new CreateReleaseCommand(release, project);

        assert !project.getReleases().contains(release);

        command.execute();

        assert project.getReleases().contains(release);

        command.undo();

        assert !project.getReleases().contains(release);
    }
}
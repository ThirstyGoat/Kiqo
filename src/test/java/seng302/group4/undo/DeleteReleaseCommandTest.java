package seng302.group4.undo;

import org.junit.Test;
import seng302.group4.Project;
import seng302.group4.Release;

import java.time.LocalDate;

/**
 * Created by bradley on 14/04/15.
 */
public class DeleteReleaseCommandTest {
    @Test
    public void deleteRelease_ReleaseRemovedFromProject() {
        Project project = new Project();
        Release release = new Release("", LocalDate.now(), "", project);
        project.getReleases().add(release);

        DeleteReleaseCommand command = new DeleteReleaseCommand(release, project);

        assert project.getReleases().contains(release);

        command.execute();

        assert !project.getReleases().contains(release);
    }

    @Test
    public void undoDeleteRelease_ReleaseAddedBackToProject() {
        Project project = new Project();
        Release release = new Release("", LocalDate.now(), "", project);
        project.getReleases().add(release);

        DeleteReleaseCommand command = new DeleteReleaseCommand(release, project);

        assert project.getReleases().contains(release);

        command.execute();

        assert !project.getReleases().contains(release);

        command.undo();

        assert project.getReleases().contains(release);
    }
}
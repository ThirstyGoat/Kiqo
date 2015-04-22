package seng302.group4.undo;

import org.junit.Test;
import seng302.group4.Organisation;
import seng302.group4.Release;

import java.time.LocalDate;

/**
 * Created by bradley on 14/04/15.
 */
public class DeleteReleaseCommandTest {
    @Test
    public void deleteRelease_ReleaseRemovedFromProject() {
        Organisation organisation = new Organisation();
        Release release = new Release("", LocalDate.now(), "", organisation);
        organisation.getReleases().add(release);

        DeleteReleaseCommand command = new DeleteReleaseCommand(release, organisation);

        assert organisation.getReleases().contains(release);

        command.execute();

        assert !organisation.getReleases().contains(release);
    }

    @Test
    public void undoDeleteRelease_ReleaseAddedBackToProject() {
        Organisation organisation = new Organisation();
        Release release = new Release("", LocalDate.now(), "", organisation);
        organisation.getReleases().add(release);

        DeleteReleaseCommand command = new DeleteReleaseCommand(release, organisation);

        assert organisation.getReleases().contains(release);

        command.execute();

        assert !organisation.getReleases().contains(release);

        command.undo();

        assert organisation.getReleases().contains(release);
    }
}
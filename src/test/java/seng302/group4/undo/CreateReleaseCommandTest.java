//package seng302.group4.undo;
//
//import org.junit.Test;
//import seng302.group4.Organisation;
//import seng302.group4.Release;
//
//import java.time.LocalDate;
//
///**
// * Created by bradley on 14/04/15.
// */
//public class CreateReleaseCommandTest {
//    @Test
//    public void createRelease_ReleaseAddedToProject() {
//        Organisation organisation = new Organisation();
//        Release release = new Release("", LocalDate.now(), "", organisation);
//
//        CreateReleaseCommand command = new CreateReleaseCommand(release, organisation);
//
//        assert !organisation.getReleases().contains(release);
//
//        command.execute();
//
//        assert organisation.getReleases().contains(release);
//    }
//
//    @Test
//    public void undoCreateRelease_ReleaseRemovedFromProject() {
//        Organisation organisation = new Organisation();
//        Release release = new Release("", LocalDate.now(), "", organisation);
//
//        CreateReleaseCommand command = new CreateReleaseCommand(release, organisation);
//
//        assert !organisation.getReleases().contains(release);
//
//        command.execute();
//
//        assert organisation.getReleases().contains(release);
//
//        command.undo();
//
//        assert !organisation.getReleases().contains(release);
//    }
//}
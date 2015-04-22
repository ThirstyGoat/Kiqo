package seng302.group4.undo;

import seng302.group4.Organisation;
import seng302.group4.Release;

/**
 * Created by james on 11/04/15.
 */
public class CreateReleaseCommand extends Command<Release> {
    private Organisation organisation;
    private Release release;


    public CreateReleaseCommand(final Release release, final Organisation organisation) {
        this.organisation = organisation;
        this.release = release;
    }

    @Override
    public Release execute() {
        organisation.getReleases().add(release);
        return release;
    }

    @Override
    public void undo() {
        organisation.getReleases().remove(release);
    }

    @Override
    public String toString() {
        return "<Create Release: \"" + release.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Create Release";
    }
}
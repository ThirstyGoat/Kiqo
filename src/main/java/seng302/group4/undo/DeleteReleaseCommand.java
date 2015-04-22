package seng302.group4.undo;

import seng302.group4.Organisation;
import seng302.group4.Release;


/**
 * Created by james on 14/04/15.
 */
public class DeleteReleaseCommand extends Command<Release> {

    private Organisation organisation;
    private Release release;


    public DeleteReleaseCommand(final Release release, final Organisation organisation) {
        this.organisation = organisation;
        this.release = release;
    }

    @Override
    public Release execute() {
        organisation.getReleases().remove(release);
        return release;
    }

    @Override
    public void undo() {
        organisation.getReleases().add(release);
    }


    @Override
    public String toString() {
        return "<Delete Release: \"" + release.getShortName() + "\">";
    }

    public Release getSkill() {
        return release;
    }

    public String getType() {
        return "Delete Release";
    }

}

package seng302.group4.undo;

import seng302.group4.Release;


/**
 * Created by james on 14/04/15.
 */
public class DeleteReleaseCommand extends Command<Release> {
    private final Release release;


    public DeleteReleaseCommand(final Release release) {
        this.release = release;
    }

    @Override
    public Release execute() {
        release.getProject().getReleases().remove(release);
        return release;
    }

    @Override
    public void undo() {
        release.getProject().getReleases().add(release);
    }


    @Override
    public String toString() {
        return "<Delete Release: \"" + release.getShortName() + "\">";
    }

    public Release getSkill() {
        return release;
    }

    @Override
    public String getType() {
        return "Delete Release";
    }

}

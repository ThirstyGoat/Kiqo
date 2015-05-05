package com.thirstygoat.kiqo.undo;

import com.thirstygoat.kiqo.Release;


/**
 * Created by james on 14/04/15.
 */
public class DeleteReleaseCommand extends Command<Release> {
    private final Release release;

    private int index;

    public DeleteReleaseCommand(final Release release) {
        this.release = release;
    }

    @Override
    public Release execute() {
        index = release.getProject().getReleases().indexOf(release);
        release.getProject().getReleases().remove(release);
        return release;
    }

    @Override
    public void undo() {
        release.getProject().getReleases().add(index, release);
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

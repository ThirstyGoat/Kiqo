package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.model.Release;


/**
 * Created by james on 14/04/15.
 */
public class DeleteReleaseCommand extends DeleteCommand {
    private final Release release;
    private int index;

    public DeleteReleaseCommand(final Release release) {
        super(release);
        this.release = release;
    }

    @Override
    public void removeFromModel() {
        index = release.getProject().getReleases().indexOf(release);
        release.getProject().observableReleases().remove(release);
    }

    @Override
    public void addToModel() {
        release.getProject().observableReleases().add(index, release);
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

package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Release;

/**
 * Created by james on 11/04/15.
 */
public class CreateReleaseCommand extends CreateCommand {
    private final Release release;


    public CreateReleaseCommand(final Release release) {
        super(release);
        this.release = release;
    }

    @Override
    public void addToModel() {
        release.getProject().observableReleases().add(release);
    }

    @Override
    public void removeFromModel() {
        release.getProject().observableReleases().remove(release);
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
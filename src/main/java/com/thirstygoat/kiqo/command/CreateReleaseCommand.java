package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Release;

/**
 * Created by james on 11/04/15.
 */
public class CreateReleaseCommand extends Command<Release> {
    private final Organisation organisation;
    private final Release release;


    public CreateReleaseCommand(final Release release, final Organisation organisation) {
        this.organisation = organisation;
        this.release = release;
    }

    @Override
    public Release execute() {
        release.getProject().getReleases().add(release);
        return release;
    }

    @Override
    public void undo() {
        release.getProject().getReleases().remove(release);
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
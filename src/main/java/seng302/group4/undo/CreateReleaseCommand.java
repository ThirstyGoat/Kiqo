package seng302.group4.undo;

import seng302.group4.Project;
import seng302.group4.Release;

/**
 * Created by james on 11/04/15.
 */
public class CreateReleaseCommand extends Command<Release> {
    private Project project;
    private Release release;


    public CreateReleaseCommand(final Release release, final Project project) {
        this.project = project;
        this.release = release;
    }

    @Override
    public Release execute() {
        project.getReleases().add(release);
        return release;
    }

    @Override
    public void undo() {
        project.getReleases().remove(release);
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
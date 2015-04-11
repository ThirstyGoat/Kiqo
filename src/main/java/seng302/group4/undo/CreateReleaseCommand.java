package seng302.group4.undo;

import seng302.group4.Project;
import seng302.group4.Release;


/**
 * Created by james on 11/04/15.
 */
public class CreateReleaseCommand extends Command<Release> {
    private final String id;
    private final String releaseDate;
    private final String description;
    private Project project;
    private Release release;


    public CreateReleaseCommand(final String id, final String releaseDate, final String description, final Project project) {
        this.project = project;
        this.description = description;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    @Override
    public Release execute() {
        if (release == null) {
            release = new Release(id, releaseDate, description, project);
        }
        project.getRelease().add(release);
        return release;
    }

    @Override
    public void undo() {
        project.getRelease().remove(release);
    }

    @Override
    public String toString() {
        return "<Create Release: \"" + id + "\">";
    }

    @Override
    public String getType() {
        return "Create Release";
    }
}

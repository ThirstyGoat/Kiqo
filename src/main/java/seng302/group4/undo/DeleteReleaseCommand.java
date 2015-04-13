package seng302.group4.undo;

import seng302.group4.Project;
import seng302.group4.Release;


/**
 * Created by james on 14/04/15.
 */
public class DeleteReleaseCommand extends Command<Release> {

    private Project project;
    private Release release;


    public DeleteReleaseCommand(final Release release, final Project project) {
        this.project = project;
        this.release = release;
    }

    @Override
    public Release execute() {
        project.getRelease().remove(release);
        return release;
    }

    @Override
    public void undo() {
        project.getRelease().add(release);
    }


    @Override
    public String toString() {
        return "<Delete Release: \"" + release.getId() + "\">";
    }

    public Release getSkill() {
        return release;
    }

    public String getType() {
        return "Delete Release";
    }

}

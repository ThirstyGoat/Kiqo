package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.search.SearchableItems;


/**
 * Created by james on 14/04/15.
 */
public class DeleteReleaseCommand extends Command {
    private final Release release;

    private int index;

    public DeleteReleaseCommand(final Release release) {
        this.release = release;
    }

    @Override
    public void execute() {
        index = release.getProject().getReleases().indexOf(release);
        release.getProject().observableReleases().remove(release);

        // Remove from SearchableItems
        SearchableItems.getInstance().removeSearchable(release);
    }

    @Override
    public void undo() {
        release.getProject().observableReleases().add(index, release);

        // Add back to SearchableItems
        SearchableItems.getInstance().addSearchable(release);
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

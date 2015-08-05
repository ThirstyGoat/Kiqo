package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by Carina on 20/05/2015.
 */
public class CreateBacklogCommand extends CreateCommand {
    private final Backlog backlog;

    public CreateBacklogCommand(final Backlog backlog) {
        super(backlog);
        this.backlog = backlog;
    }

    @Override
    public void addToModel() {
        backlog.getProject().observableBacklogs().add(backlog);

        // Assign this backlog as the owner of each of the stories
        for (Story story : backlog.getStories()) {
            story.setBacklog(backlog);
        }

        // Remove all stories from unallocated stories in Project
        backlog.getProject().observableUnallocatedStories().removeAll(backlog.getStories());
    }

    @Override
    public void removeFromModel() {
        backlog.getProject().observableBacklogs().remove(backlog);

        for (Story story : backlog.getStories()) {
            story.setBacklog(null);
        }

        backlog.getProject().observableUnallocatedStories().addAll(backlog.getStories());
    }

    @Override
    public String toString() {
        return "<Create Backlog: \"" + backlog.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Create Backlog";
    }
}


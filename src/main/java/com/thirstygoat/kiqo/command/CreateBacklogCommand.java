package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Backlog;

/**
 * Created by Carina on 20/05/2015.
 */
public class CreateBacklogCommand extends Command<Backlog> {
    private final Backlog backlog;

    public CreateBacklogCommand(final Backlog backlog) {
        this.backlog = backlog;
    }

    @Override
    public Backlog execute() {
        backlog.getProject().observableBacklogs().add(backlog);
        backlog.getProject().observableStories().removeAll(backlog.getStories());
        return backlog;
    }

    @Override
    public void undo() {
        backlog.getProject().observableBacklogs().remove(backlog);
        backlog.getProject().observableStories().addAll(backlog.getStories());
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


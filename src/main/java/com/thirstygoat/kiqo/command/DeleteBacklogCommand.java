package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Backlog;

/**
 * Created by Carina on 20/05/2015.
 */
public class DeleteBacklogCommand extends Command<Backlog>{
    private final Backlog backlog;

    private int index;

    public DeleteBacklogCommand(final Backlog backlog) {
        this.backlog = backlog;
    }

    @Override
    public Backlog execute() {
        index = backlog.getProject().getBacklogs().indexOf(backlog);
        backlog.getProject().observableBacklogs().remove(backlog);
        return backlog;
    }

    @Override
    public void undo() {
        backlog.getProject().observableBacklogs().add(index, backlog);
    }


    @Override
    public String toString() {
        return "<Delete Backlog: \"" + backlog.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Backlog";
    }



}

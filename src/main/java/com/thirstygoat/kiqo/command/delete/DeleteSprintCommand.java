package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.model.Sprint;


/**
 * Created by Bradley on 29/04/15.
 */
public class DeleteSprintCommand extends DeleteCommand {
    private final Sprint sprint;

    private int index;

    public DeleteSprintCommand(final Sprint sprint) {
        super(sprint);
        this.sprint = sprint;
    }

    @Override
    public void removeFromModel() {
        index = sprint.getRelease().getSprints().indexOf(sprint);
        sprint.getRelease().getSprints().remove(sprint);
    }

    
    @Override
    public void addToModel() {
        sprint.getRelease().getSprints().add(index, sprint);
    }

    @Override
    public String toString() {
        return "<Delete Sprint: \"" + sprint.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Sprint";
    }
}

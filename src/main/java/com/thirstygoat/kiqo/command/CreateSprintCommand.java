package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Sprint;

/**
 * Created by amy on 5/8/15.
 */
public class CreateSprintCommand extends Command<Void> {
    private final Sprint sprint;

    public CreateSprintCommand(final Sprint sprint) {
        this.sprint = sprint;
    }

    @Override
    public Void execute() {
        sprint.getRelease().getSprints().add(sprint);
        return null;
    }

    @Override
    public void undo() {
        sprint.getRelease().getSprints().remove(sprint);
    }

    @Override
    public String toString() {
        return "<Create Sprint: \"" + sprint.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Create Sprint";
    }
}
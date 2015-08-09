package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.Sprint;

/**
 * Created by amy on 5/8/15.
 */
public class CreateSprintCommand extends CreateCommand {
    private final Sprint sprint;

    public CreateSprintCommand(final Sprint sprint) {
        super(sprint);
        this.sprint = sprint;
    }

    @Override
    public void addToModel() {
        sprint.getRelease().getSprints().add(sprint);
    }

    @Override
    public void removeFromModel() {
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
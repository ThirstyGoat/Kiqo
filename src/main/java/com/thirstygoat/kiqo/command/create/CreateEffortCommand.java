package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by james on 17/09/15.
 */
public class CreateEffortCommand extends CreateCommand {
    final private Effort effort;
    final private Task task;

    public CreateEffortCommand(final Effort effort, final Task task) {
        super(effort);
        this.effort = effort;
        this.task = task;
    }

    @Override
    protected void addToModel() {
        task.getLoggedEffort().add(effort);
    }

    @Override
    protected void removeFromModel() {
        task.getLoggedEffort().remove(effort);
    }

    @Override
    public String getType() {
        return "<Create Effort: \"" + effort.getComment() + "\">";
    }

    @Override
    public String toString() {
        return "Create Effort";
    }
}

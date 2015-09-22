package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by james on 17/09/15.
 */
public class DeleteEffortCommand extends DeleteCommand {
    final private Effort effort;
    final private Task task;

    public DeleteEffortCommand(final Effort effort, final Task task) {
        super(effort);
        this.effort = effort;
        this.task = task;
    }

    @Override protected void removeFromModel() {
        task.getObservableLoggedEffort().remove(effort);
    }

    @Override protected void addToModel() {
        task.getObservableLoggedEffort().add(effort);
    }

    @Override public String getType() {
        return "<Create Effort: \"" + effort.getComment() + "\">";
    }

    @Override public String toString() {
        return "Create Effort";
    }
}

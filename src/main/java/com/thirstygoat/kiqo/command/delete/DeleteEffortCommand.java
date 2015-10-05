package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by james on 17/09/15.
 */
public class DeleteEffortCommand extends Command {
    final private Effort effort;
    final private Task task;

    public DeleteEffortCommand(final Effort effort, final Task task) {
        this.effort = effort;
        this.task = task;
    }

    @Override
    public void execute() {
        task.getObservableLoggedEffort().remove(effort);
    }

    @Override
    public void undo() {
        task.getObservableLoggedEffort().add(effort);
    }

    @Override public String getType() {
        return "<Create Effort: \"" + effort.getComment() + "\">";
    }

    @Override public String toString() {
        return "Create Effort";
    }
}

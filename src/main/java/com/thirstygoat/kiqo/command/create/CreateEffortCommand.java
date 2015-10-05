package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by james on 17/09/15.
 */
public class CreateEffortCommand extends Command {
    final private Effort effort;
    final private Task task;

    public CreateEffortCommand(final Effort effort, final Task task) {
        this.effort = effort;
        this.task = task;
    }

    @Override
    public void execute() {
        task.getObservableLoggedEffort().add(effort);
    }

    @Override
    public void undo() {
        task.getObservableLoggedEffort().remove(effort);
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

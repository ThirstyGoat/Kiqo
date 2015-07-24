package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by Carina Blair on 23/07/2015.
 */
public class CreateTaskCommand extends Command<Task> {

    private final Task task;
    private final Story story;

    public CreateTaskCommand(final Task task, final Story story) {
        this.task = task;
        this.story = story;
    }

    @Override
    public Task execute() {
        story.observableTasks().add(task);
        return task;
    }

    @Override
    public void undo() {
        story.observableTasks().remove(task);
    }

   @Override
   public String toString() { return "<Create Task: \"" + task.getShortName() + "\">"; }

    @Override
    public String getType() {return "Create Task"; }

}


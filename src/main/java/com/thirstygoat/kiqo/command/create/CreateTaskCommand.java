package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by Carina Blair on 23/07/2015.
 */
public class CreateTaskCommand extends CreateCommand {

    private final Task task;
    private final Story story;

    public CreateTaskCommand(final Task task, final Story story) {
        super(task);
        this.task = task;
        this.story = story;
    }

    @Override
    public void addToModel() {
        story.observableTasks().add(task);
    }

    @Override
    public void removeFromModel() {
        story.observableTasks().remove(task);
    }

   @Override
   public String toString() { return "<Create Task: \"" + task.getShortName() + "\">"; }

    @Override
    public String getType() {return "Create Task"; }

}


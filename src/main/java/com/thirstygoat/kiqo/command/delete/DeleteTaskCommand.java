package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;

/**
 * Created by Carina Blair on 23/07/2015.
 *
 * Command to delete a task from a project.
 */
public class DeleteTaskCommand extends DeleteCommand {

    private final Story story;
    private final Task task;

    private int storyIndex;

    public DeleteTaskCommand(final Task task, final Story story) {
        super(task);
        this.task = task;
        this.story = story;
    }

    @Override
    public void removeFromModel() {
        storyIndex = story.observableTasks().indexOf(task);
        story.observableTasks().remove(task);
    }

    @Override
    public void addToModel() {
        story.observableTasks().add(storyIndex, task);
    }
    @Override
    public String toString() {return "<Delete Task: \"" + task.getShortName() + "\">";}

    @Override
    public String getType() {return "Delete Task";}

}

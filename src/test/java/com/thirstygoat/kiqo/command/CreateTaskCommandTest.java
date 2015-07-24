package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Carina Blair on 24/07/2015.
 */
public class CreateTaskCommandTest {
    private Project project;
    private Backlog backlog;
    private Person person;
    private Story story;
    private CreateTaskCommand command;
    private Task task;

    @Before
    public void setup() {
        project = new Project("proj1", "Project");
        person = new Person("pers1", "Person","descr", "id", "email", "phone", "dept", new ArrayList<>());
        story = new Story("story1", "Story", "descr", person, project, backlog, 9, Scale.FIBONACCI, 0, false);
        task = new Task("task1", "descr", 0f );
        command = new CreateTaskCommand(task, story);
    }

    /**
     * Create a Task and check that it is added to the tasks of that story
     */
    @Test
    public void createTask() {
        Assert.assertFalse(story.observableTasks().contains(task));

        command.execute();

        Assert.assertTrue(story.observableTasks().contains(task));
    }

    @Test
    public void undoCreateTask() {
        command.execute();
        command.undo();

        Assert.assertFalse(story.observableTasks().contains(task));
    }


}

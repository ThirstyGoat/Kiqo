package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Carina Blair on 24/07/2015.
 */
public class DeleteTaskCommandTest {
    private Project project;
    private Backlog backlog;
    private Person person;
    private Story story;
    private DeleteTaskCommand command;
    private Task task;

    @Before
    public void setup() {
        project = new Project("proj", "Project");
        person = new Person("pers1", "Person","descr", "id", "email", "phone", "dept", new ArrayList<Skill>());
        story = new Story("story1", "Story", "descr", person, project, backlog, 9, Scale.FIBONACCI, 0, false);
        task = new Task("task1", "descr", 0f );
        story.getTask().add(task);
        command = new DeleteTaskCommand(task, story);
    }

    @Test
    public void deleteTask() {
        Assert.assertTrue(story.getTask().contains(task));

        command.execute();

        Assert.assertFalse(story.getTask().contains(task));
    }

    @Test
    public void undoDeleteTask() {
        Assert.assertTrue(story.getTask().contains(task));

        command.execute();

        Assert.assertFalse(story.getTask().contains(task));

        command.undo();

        Assert.assertTrue(story.getTask().contains(task));
    }
}



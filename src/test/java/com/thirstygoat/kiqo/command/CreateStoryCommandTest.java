package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.create.CreateStoryCommand;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by bradley on 14/04/15.
 */
public class CreateStoryCommandTest {
    private Project project;
    private Backlog backlog;
    private Person person;
    private Story story;
    private CreateStoryCommand command;

    @Before
    public void setup() {
        project = new Project("proj", "Project");
        person = new Person("pers1", "Person","descr", "id", "email", "phone", "dept", new ArrayList<>());
        story = new Story("story1", "Story One", "descr", person, project, backlog, 9, Scale.FIBONACCI, 0, false, false);
        command = new CreateStoryCommand(story);
    }

    @Test
    public void createStory_StoryAddedToProject() {
        Assert.assertFalse(project.getUnallocatedStories().contains(story));

        command.execute();

        Assert.assertTrue(project.getUnallocatedStories().contains(story));
    }

    @Test
    public void undoCreateStory_StoryRemovedFromProject() {
        command.execute();
        command.undo();

        Assert.assertFalse(project.getUnallocatedStories().contains(story));
    }
}

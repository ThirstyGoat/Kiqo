package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by leroy on 15/05/2015
 */
public class DeleteStoryCommandTest {
    private Story story;
    private Project project;
    private Backlog backlog;
    private Person person;
    private DeleteStoryCommand command;

    @Before
    public void setup() {
        project = new Project("", "");
        story = new Story("story1", "Story One", "descr", person, project, backlog, 9, 0, Scale.FIBONACCI);
        project.observableUnallocatedStories().add(story);
        command = new DeleteStoryCommand(story);
    }

    @Test
    public void deleteStory_StoryRemovedFromProject() {
        Assert.assertTrue(project.getUnallocatedStories().contains(story));

        command.execute();

        Assert.assertFalse(project.getUnallocatedStories().contains(story));
    }

    @Test
    public void undoDeleteStory_StoryAddedBackToProject() {
        Assert.assertTrue(project.getUnallocatedStories().contains(story));

        command.execute();

        Assert.assertFalse(project.getUnallocatedStories().contains(story));

        command.undo();

        Assert.assertTrue(project.getUnallocatedStories().contains(story));
    }
}

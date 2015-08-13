package com.thirstygoat.kiqo.command;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.command.delete.DeleteStoryCommand;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;

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
        story = new Story("story1", "Story One", "descr", person, project, backlog, 9, Scale.FIBONACCI, 0, false, false);
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

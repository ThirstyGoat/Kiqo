package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bradley on 23/07/15.
 */
public class RemoveStoryFromBacklogCommandTest {
    private Project project;
    private Backlog backlog;
    private Story story;

    @Before
    public void setup() {
        project = new Project();
        story = new Story();
        List<Story> stories = new ArrayList<>();
        stories.add(story);
        backlog = new Backlog("short", "long", "desc", new Person(), project, stories, Scale.FIBONACCI);
    }

    @Test
    public void checkStoryRemovedFromBacklog() {
        Command<?> command = new RemoveStoryFromBacklogCommand(story, backlog);
        Assert.assertTrue(backlog.getStories().contains(story));

        command.execute();
        Assert.assertFalse(backlog.getStories().contains(story));
    }

    @Test
    public void checkStoryAddedToProjectUnAllocatedStories() {
        Command<?> command = new RemoveStoryFromBacklogCommand(story, backlog);
        Assert.assertTrue(backlog.getStories().contains(story));

        command.execute();
        Assert.assertTrue(backlog.getProject().getUnallocatedStories().contains(story));
    }

    @Test
    public void checkReadinessReset() {
        story.setIsReady(true);
        Command<?> command = new RemoveStoryFromBacklogCommand(story, backlog);

        command.execute();
        Assert.assertFalse(story.getIsReady());
    }

    @Test
    public void checkReadinessReset_Undo() {
        story.setIsReady(true);
        Command<?> command = new RemoveStoryFromBacklogCommand(story, backlog);

        command.execute();
        Assert.assertFalse(story.getIsReady());

        command.undo();
        Assert.assertTrue(story.getIsReady());
    }
}
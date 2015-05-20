package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Story;

/**
 * Created by leroy on 15/05/15.
 */
public class DeleteStoryCommand extends Command<Story> {
    private final Story story;

    private int index;

    public DeleteStoryCommand(final Story story) {
        this.story = story;
    }

    @Override
    public Story execute() {
        index = story.getProject().getStories().indexOf(story);
        story.getProject().observableStories().remove(story);
        return story;
    }

    @Override
    public void undo() {
        // Add the story back to wherever it was
        story.getProject().observableStories().add(index, story);
    }

    @Override
    public String toString() {
        return "<Delete Story \"" + story.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Story";
    }
}

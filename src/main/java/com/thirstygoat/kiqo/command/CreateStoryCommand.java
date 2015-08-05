package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Story;

/**
 * Created by james on 11/04/15.
 */
public class CreateStoryCommand extends Command {
    private final Story story;


    public CreateStoryCommand(final Story story) {
        this.story = story;
    }

    @Override
    public void execute() {
        story.getProject().observableUnallocatedStories().add(story);
    }

    @Override
    public void undo() {
        story.getProject().observableUnallocatedStories().remove(story);
    }

    @Override
    public String toString() {
        return "<Create Story: \"" + story.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Create Story";
    }
}
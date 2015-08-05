package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Story;

/**
 * Created by james on 11/04/15.
 */
public class CreateStoryCommand extends CreateCommand {
    private final Story story;


    public CreateStoryCommand(final Story story) {
        super(story);
        this.story = story;
    }

    @Override
    public void addToModel() {
        story.getProject().observableUnallocatedStories().add(story);
    }

    @Override
    public void removeFromModel() {
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
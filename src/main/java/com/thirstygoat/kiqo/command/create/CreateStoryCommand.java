package com.thirstygoat.kiqo.command.create;

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
        // Check to see if story is supposed to be in backlog
        if (story.getBacklog() == null) {
            story.getProject().observableUnallocatedStories().add(story);
        } else {
            story.getBacklog().observableStories().add(story);
        }
    }

    @Override
    public void removeFromModel() {
        if (story.getBacklog() == null) {
            story.getProject().observableUnallocatedStories().remove(story);
        } else {
            story.getBacklog().observableStories().remove(story);
        }
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
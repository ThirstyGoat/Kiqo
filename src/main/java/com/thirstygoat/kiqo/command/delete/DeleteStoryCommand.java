package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.search.SearchableItems;

/**
 * Created by leroy on 15/05/15.
 */
public class DeleteStoryCommand extends DeleteCommand {
    private final Story story;

    private int index;

    public DeleteStoryCommand(final Story story) {
        super(story);
        this.story = story;
    }

    @Override
    public void removeFromModel() {
        if (story.getBacklog() != null) {
            index = story.getBacklog().getStories().indexOf(story);
            story.getBacklog().observableStories().remove(story);
        } else {
            index = story.getProject().getUnallocatedStories().indexOf(story);
            story.getProject().observableUnallocatedStories().remove(story);
        }
    }

    @Override
    public void addToModel() {
        // Add the story back to wherever it was
        if (story.getBacklog() != null) {
            story.getBacklog().observableStories().add(index, story);
        } else {
            story.getProject().observableUnallocatedStories().add(index, story);
        }
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

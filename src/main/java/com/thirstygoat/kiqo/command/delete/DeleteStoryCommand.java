package com.thirstygoat.kiqo.command.delete;

import java.util.HashMap;
import java.util.Map;

import com.thirstygoat.kiqo.model.Story;

/**
 * Created by leroy on 15/05/15.
 */
public class DeleteStoryCommand extends DeleteCommand {
    private final Story story;

    private int index;
    private Map<Story, Integer> dependencyIndices = new HashMap<>();

    public DeleteStoryCommand(final Story story) {
        super(story);
        this.story = story;

        // Add stories that depend on this story to the list
        if (story.getBacklog() != null) {
            for (Story story1 : story.getBacklog().observableStories()) {
                if (story1.getDependencies().contains(story)) {
                    dependencyIndices.put(story1, story1.observableDependencies().indexOf(story));
                }
            }
        }
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
        dependencyIndices.forEach((story1, integer) -> {
            story1.observableDependencies().remove(story);
        });
    }

    @Override
    public void addToModel() {
        // Add the story back to wherever it was
        if (story.getBacklog() != null) {
            story.getBacklog().observableStories().add(index, story);
        } else {
            story.getProject().observableUnallocatedStories().add(index, story);
        }
        dependencyIndices.forEach((story1, integer) -> story1.observableDependencies().add(integer, story));
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

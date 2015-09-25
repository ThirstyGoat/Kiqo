package com.thirstygoat.kiqo.command.delete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by leroy on 15/05/15.
 */
public class DeleteStoryCommand extends DeleteCommand {
    private final Story story;
    private Map<Sprint, Integer> sprintsWithin = new HashMap<>();

    private int index;
    private Map<Story, Integer> dependencyIndices = new HashMap<>();

    public DeleteStoryCommand(final Story story) {
        super(story);
        this.story = story;

        checkInSprint();

        // Add stories that depend on this story to the list
        if (story.getBacklog() != null) {
            for (Story story1 : story.getBacklog().observableStories()) {
                if (story1.getDependencies().contains(story)) {
                    dependencyIndices.put(story1, story1.observableDependencies().indexOf(story));
                }
            }
        }
    }

    private void checkInSprint() {
        for (Sprint sprint : story.getProject().getSprints()) {
            if (sprint.getStories().contains(story)) {
                sprintsWithin.put(sprint, sprint.getStories().indexOf(story));
            }
        }
    }

    public boolean inSprint() {
        return !sprintsWithin.isEmpty();
    }

    public List<Sprint> getSprintsWithin() {
        return sprintsWithin.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public void removeFromModel() {
        // Remove Story from Sprints within
        for (Sprint sprint : sprintsWithin.keySet()) {
            sprint.getStories().remove(story);
        }

        if (story.getBacklog() != null) {
            index = story.getBacklog().getStories().indexOf(story);
            story.getBacklog().observableStories().remove(story);
        } else {
            index = story.getProject().getUnallocatedStories().indexOf(story);
            story.getProject().getUnallocatedStories().remove(story);
        }
        dependencyIndices.forEach((story1, integer) -> {
            story1.observableDependencies().remove(story);
        });
    }

    @Override
    public void addToModel() {
        // Add Story back to Sprints within
        for (Map.Entry<Sprint, Integer> entry : sprintsWithin.entrySet()) {
            entry.getKey().getStories().add(entry.getValue(), story);
        }

        // Add the story back to wherever it was
        if (story.getBacklog() != null) {
            story.getBacklog().observableStories().add(index, story);
        } else {
            story.getProject().getUnallocatedStories().add(index, story);
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

package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Story;

/**
 * Created by leroy on 21/07/15.
 */
public class StoryTableEntryViewModel {

    private String shortName;
    private Number priority;

    public StoryTableEntryViewModel(Story story) {
        this.shortName = story.getShortName();
        this.priority = story.getPriority();
    }

    public Number getPriority() {
        return priority;
    }

    public String getShortName() {
        return shortName;
    }
}

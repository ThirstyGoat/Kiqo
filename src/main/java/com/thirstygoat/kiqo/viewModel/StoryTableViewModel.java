package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Story;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by leroy on 21/07/15.
 */
public class StoryTableViewModel {

    public final String PLACEHOLDER = "No stories in backlog";

    private ObservableList<StoryTableEntryViewModel> stories = FXCollections.observableArrayList();

    public void setStories(ObservableList<Story> stories) {
        this.stories.clear();
        stories.forEach(story -> this.stories.add(new StoryTableEntryViewModel(story)));
    }

    public ObservableList<StoryTableEntryViewModel> stories() {
        return stories;
    }
}

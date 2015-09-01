package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardViewModel implements Loadable<Sprint>, ViewModel {

    private VBox scrumBoardVBox;

    private Organisation organisation;
    private Sprint sprint;

    private ObservableList<Node> storyRows = FXCollections.observableArrayList();
    private Map<Node, Story> storyRowsMap = new HashMap<>();

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        debug(sprint.getStories());
        this.sprint = sprint;
        this.organisation = organisation;
    }

    /**
     * Debug method to populate the scrumboard vbox with some gridpanes
     */
    private void debug(Collection<Story> stories) {
        storyRows.clear();
        stories.forEach(story -> {
            // Add StoryRowViewModel
            ViewTuple<StoryRowView, StoryRowViewModel> viewTuple = FluentViewLoader.fxmlView(StoryRowView.class).load();
            viewTuple.getViewModel().load(story, organisation);
            storyRowsMap.put(viewTuple.getView(), story);
            storyRows.add(viewTuple.getView());
        });
    }

    /**c
     * Checks if the order of stories shown in the ScrumBoard has changed, if so, creates the
     * appropriate command to update the order in the model.
     */
    public void updateStoryOrder() {
//        Story reOrderedStory = sprint.getStories().get(ScrumBoardView.currentlyDraggingStoryInitialIndex);
//        int initialIndex = ScrumBoardView.currentlyDraggingStoryInitialIndex;
//        int finalIndex = ScrumBoardView.currentlyDraggingStoryFinalIndex;
//
//        if (initialIndex != finalIndex) {
//            System.out.println("Moved story from index: " + initialIndex + " to: " + finalIndex);
//
//            // Calculate priority changes
//            Story previousStory = sprint.getStories().get(Math.max(0, finalIndex - 1));
//            Story nextStory = sprint.getStories().get(Math.min(sprint.getStories().size() - 1, finalIndex));
//            System.out.println("Prev Story Priority: " + previousStory.getPriority() + ", Next Story Priority: " + nextStory.getPriority());
//        }
    }

    public ObservableList<Node> storyRowsProperty() {
        return storyRows;
    }
}

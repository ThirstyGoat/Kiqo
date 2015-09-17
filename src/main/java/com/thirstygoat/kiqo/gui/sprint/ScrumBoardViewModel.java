package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardViewModel implements Loadable<Sprint>, ViewModel {
    public Node currentlyDraggingStoryRow;
    public Integer currentlyDraggingStoryInitialIndex = null;
    public Integer currentlyDraggingStoryFinalIndex = null;
    private MainController mainController;
    private Organisation organisation;
    private Sprint sprint;
    private ObservableList<Node> storyRows = FXCollections.observableArrayList();

    private Map<Node, Story> storyRowsMap = new HashMap<>();
    private Map<Story, Node> storyMap = new HashMap<>();

    private ObservableList<Story> sortedStories;
    private ListChangeListener<Story> listener;

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        // Only reload if this is a new sprint
        if (sprint != this.sprint) {
            Platform.runLater(() -> populateStories(sprint.getStories()));

            this.sprint = sprint;
            this.organisation = organisation;
        }
    }

    private void mapStoryRow(Story story, Node node) {
        storyRowsMap.put(node, story);
        storyMap.put(story, node);
    }

    private Node getStoryRow(Story story) {
        if (storyMap.get(story) == null) {
            ViewTuple<StoryRowView, StoryRowViewModel> viewTuple = FluentViewLoader.fxmlView(StoryRowView.class).load();
            viewTuple.getViewModel().load(story, organisation);
            viewTuple.getViewModel().setMainController(mainController);
            viewTuple.getCodeBehind().setScrumBoardViewModel(this);
            mapStoryRow(story, viewTuple.getView());
            return viewTuple.getView();
        } else {
            return storyMap.get(story);
        }
    }

    /**
     * Populates the scrumboard vbox with the appropriate story rows
     */
    private void populateStories(ObservableList<Story> stories) {
        storyRows.clear();

        // Remove old listener
        if (sortedStories != null)
            sortedStories.removeListener(listener);

        // Create new SortedList for the new stories
        sortedStories = new SortedList<>(stories, (o1, o2) -> {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        });

        // Create new listener which listens for changes in new sorted stories list
        listener = c -> {
            // Update stories appropriately (re-ordering/adding/removing)
            c.next();

            if (c.getAddedSubList().isEmpty() && c.getRemoved().isEmpty()) {
                // Then there has been an order change, update appropriately

                storyRows.setAll(sortedStories.stream().map(this::getStoryRow).collect(Collectors.toList()));
            }

            // Remove all stories that have been deleted / removed from sprint
            storyRows.removeAll(c.getRemoved().stream().map(storyMap::get).collect(Collectors.toList()));

            // Add all stories that have been added to sprint
            storyRows.addAll(c.getAddedSubList().stream().map(this::getStoryRow).collect(Collectors.toList()));
        };

        // Add the listener to the new sorted stories
        sortedStories.addListener(listener);

        // Populate story rows with the new sorted stories
        storyRows.setAll(sortedStories.stream().map(this::getStoryRow).collect(Collectors.toList()));
    }

    /**c
     * Checks if the order of stories shown in the ScrumBoard has changed, if so, creates the
     * appropriate command to update the order in the model.
     */
    public void updateStoryOrder() {
        Node movedStoryRow = currentlyDraggingStoryRow;
        Story movedStory = storyRowsMap.get(movedStoryRow);

        int movedStoryIndex = storyRows.indexOf(movedStoryRow);
        Node prevStoryRow = storyRows.get(Math.max(movedStoryIndex-1, 0));
        Node nextStoryRow = storyRows.get(Math.min(movedStoryIndex + 1, storyRows.size() - 1));

        int prevPriority = Story.MAX_PRIORITY;
        int nextPriority = Story.MIN_PRIORITY;

        if (prevStoryRow != movedStoryRow) {
            // Story moved to top of list, therefore, must have highest priority
            prevPriority = storyRowsMap.get(prevStoryRow).getPriority();
        }
        if (nextStoryRow != movedStoryRow) {
            nextPriority = storyRowsMap.get(nextStoryRow).getPriority();
        }

        int newPriority = (prevPriority+nextPriority)/2;
        Command command = new EditCommand<>(movedStory, "priority", newPriority);
        UndoManager.getUndoManager().doCommand(command);
    }

    public ObservableList<Node> storyRowsProperty() {
        return storyRows;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

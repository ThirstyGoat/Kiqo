package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardViewModel implements Loadable<Sprint>, ViewModel {

    private VBox scrumBoardVBox;

    private Organisation organisation;

    @Override
    public void load(Sprint sprint, Organisation organisation) {
//        scrumBoardVBox.setStories(sprint.getStories());
        debug(sprint.getStories());
        this.organisation = organisation;
    }

    /**
     * Debug method to populate the scrumboard vbox with some gridpanes
     */
    private void debug(Collection<Story> stories) {
        List<Node> storyRows = new ArrayList<>();

        stories.forEach(story -> {
            // Add StoryRowViewModel
            ViewTuple<StoryRowView, StoryRowViewModel> viewTuple = FluentViewLoader.fxmlView(StoryRowView.class).load();
            viewTuple.getViewModel().load(story, organisation);
            storyRows.add(viewTuple.getView());
        });

        scrumBoardVBox.getChildren().setAll(storyRows);
    }

    public void setScrumBoardVBox(VBox scrumBoardVBox) {
        this.scrumBoardVBox = scrumBoardVBox;
    }
}

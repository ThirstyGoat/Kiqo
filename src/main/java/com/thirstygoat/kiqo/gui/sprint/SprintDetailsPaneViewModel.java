package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel extends SprintViewModel {
    public final String PLACEHOLDER = "No stories in sprint";

    public SprintDetailsPaneViewModel() {

    }

    /**
     * Bind to model so there is not copying data in
     */
    @Override
    public void load(Sprint sprint, Organisation organisation) {
        if (sprint != null) {
            super.goalProperty().bind(sprint.shortNameProperty());
            super.longNameProperty().bind(sprint.longNameProperty());
            super.descriptionProperty().bind(sprint.descriptionProperty());
            super.backlogProperty().bind(sprint.backlogProperty());
            super.startDateProperty().bind(sprint.startDateProperty());
            super.endDateProperty().bind(sprint.endDateProperty());
            super.teamProperty().bind(sprint.teamProperty());
            super.releaseProperty().bind(sprint.releaseProperty());
            super.getStories().clear();
            super.getStories().addAll(sprint.getStories());
        } else {
            super.goalProperty().unbind();
            super.longNameProperty().unbind();
            super.descriptionProperty().unbind();
            super.backlogProperty().unbind();
            super.startDateProperty().unbind();
            super.endDateProperty().unbind();
            super.teamProperty().unbind();
            super.releaseProperty().unbind();
            super.getStories().clear();
        }
    }
}



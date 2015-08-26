package com.thirstygoat.kiqo.gui.backlog;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class BacklogDetailsPaneViewModel extends BacklogViewModel {
    public final String PLACEHOLDER = "No stories in backlog";
    private final BooleanProperty highlightStoryState;

    public BacklogDetailsPaneViewModel() {
        super();
        highlightStoryState = new SimpleBooleanProperty(true); // highlights on by default
    }

    public BooleanProperty highlightStoryStateProperty() {
        return highlightStoryState;
    }
}

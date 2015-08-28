package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class BacklogDetailsPaneViewModel extends BacklogViewModel implements Editable {
    public final String PLACEHOLDER = "No stories in backlog";
    private final BooleanProperty highlightStoryState;

    public BacklogDetailsPaneViewModel() {
        super();
        highlightStoryState = new SimpleBooleanProperty(true); // highlights on by default
    }

    public BooleanProperty highlightStoryStateProperty() {
        return highlightStoryState;
    }

    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    public void cancelEdit() {
        reload();
    }
}

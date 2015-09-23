package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.nodes.GoatTree.GoatTree;
import com.thirstygoat.kiqo.gui.nodes.GoatTree.HierarchicalData;
import com.thirstygoat.kiqo.model.Story;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


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

    /**
     * Opens the dependency visualiser
     */
    public void visualiseDependencies() {
        GoatTree<Story> goatTree = new GoatTree<>();
        goatTree.run();
    }
}

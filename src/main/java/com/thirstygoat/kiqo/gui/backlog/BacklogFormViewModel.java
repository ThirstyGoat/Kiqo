package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Story;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

/**
* Created by Carina Blair on 19/07/2015.
*/
public class BacklogFormViewModel extends BacklogViewModel {
    private ListProperty<Story> sourceStoriesProperty = new SimpleListProperty<>();

    private Runnable exitStrategy;

    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    protected void okAction() {
        UndoManager.getUndoManager().doCommand(getCommand());
        exitStrategy.run();
    }

    protected void cancelAction() {
        exitStrategy.run();
    }
}
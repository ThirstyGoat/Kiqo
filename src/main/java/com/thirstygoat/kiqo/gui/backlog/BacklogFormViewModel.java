package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;

/**
* Created by Carina Blair on 19/07/2015.
*/
public class BacklogFormViewModel extends BacklogViewModel {
    private Runnable exitStrategy;

    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    protected void okAction() {
        Command command = getCommand();
        if (command != null ) {
            UndoManager.getUndoManager().doCommand(command);
        }
        exitStrategy.run();
    }

    protected void cancelAction() {
        exitStrategy.run();
    }
}

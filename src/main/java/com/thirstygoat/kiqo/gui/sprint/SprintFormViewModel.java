package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;


/**
 * Created by samschofield on 31/07/15.
 */
public class SprintFormViewModel extends SprintViewModel {
    
    private Runnable exitStrategy;

    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    protected void okAction() {
        final Command command = createCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
        exitStrategy.run();
    }

    protected void cancelAction() {
        exitStrategy.run();
    }
}

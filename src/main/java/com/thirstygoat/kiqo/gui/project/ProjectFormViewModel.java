package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.command.UndoManager;

/**
 * Created by leroy on 9/09/15.
 */
public class ProjectFormViewModel extends ProjectViewModel {
    private Runnable exitStrategy;

    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    public void okAction() {
        UndoManager.getUndoManager().doCommand(getCommand());
        exitStrategy.run();
    }

    protected void cancelAction() {
        exitStrategy.run();
    }
}

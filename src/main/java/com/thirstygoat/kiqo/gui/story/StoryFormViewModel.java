package com.thirstygoat.kiqo.gui.story;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.viewModel.StoryViewModel;

/**
 * Created by Carina Blair on 14/08/2015.
 */
public class StoryFormViewModel extends StoryViewModel {

    private Runnable exitStrategy;

    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    protected void okAction() {
        Command command = getCommand();
        if(command != null) {
            UndoManager.getUndoManager().doCommand(getCommand());
            exitStrategy.run();
        }
    }

    protected  void cancelAction() {
        exitStrategy.run();
    }
}

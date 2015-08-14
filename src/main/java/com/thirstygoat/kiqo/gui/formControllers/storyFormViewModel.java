package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.IFormViewModel;
import com.thirstygoat.kiqo.gui.viewModel.StoryViewModel;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by Carina Blair on 14/08/2015.
 */
public class StoryFormViewModel extends StoryViewModel implements IFormViewModel<Story> {

    private Runnable exitStrategy;

    @Override
    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    protected void okAction() {
        UndoManager.getUndoManager().doCommand(getCommand());
        exitStrategy.run();
    }

    protected  void cancelAction() {
        exitStrategy.run();
    }
}

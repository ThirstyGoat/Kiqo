package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import de.saxsys.mvvmfx.ViewModel;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel extends SprintViewModel implements Editable {

    private SprintDetailsPaneDetailsViewModel sprintDetailsPaneDetailsViewModel;
    private ScrumBoardViewModel scrumBoardViewModel;

    public void setDetailsViewModel(SprintDetailsPaneDetailsViewModel viewModel) {
        sprintDetailsPaneDetailsViewModel = viewModel;
    }

    public void setScrumboardViewModel(ScrumBoardViewModel viewModel) {
        scrumBoardViewModel = viewModel;
    }

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        super.load(sprint, organisation);
        sprintDetailsPaneDetailsViewModel.load(sprint, organisation);
        scrumBoardViewModel.load(sprint, organisation);
    }

    public ScrumBoardViewModel getScrumBoardViewModel() {
        return scrumBoardViewModel;
    }

    @Override
    public void commitEdit() {
        Command command = createCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {
        this.reload();
    }
}
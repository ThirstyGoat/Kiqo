package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import de.saxsys.mvvmfx.ViewModel;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel implements Loadable<Sprint>, ViewModel {

    private SprintDetailsPaneDetailsViewModel sprintDetailsPaneDetailsViewModel;
    private ScrumboardViewModel scrumboardViewModel;

    public void setDetailsViewModel(SprintDetailsPaneDetailsViewModel viewModel) {
        sprintDetailsPaneDetailsViewModel = viewModel;
    }

    public void setScrumboardViewModel(ScrumboardViewModel viewModel) {
        scrumboardViewModel = viewModel;
    }

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        sprintDetailsPaneDetailsViewModel.load(sprint, organisation);
        scrumboardViewModel.load(sprint, organisation);
    }
}
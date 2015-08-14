package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ListChangeListener;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel implements Loadable<Sprint>, ViewModel {

    private SprintDetailsPaneDetailsViewModel sprintDetailsPaneDetailsViewModel;

    public void setDetailsViewModel(SprintDetailsPaneDetailsViewModel viewModel) {
        sprintDetailsPaneDetailsViewModel = viewModel;
    }

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        sprintDetailsPaneDetailsViewModel.load(sprint, organisation);
    }
}
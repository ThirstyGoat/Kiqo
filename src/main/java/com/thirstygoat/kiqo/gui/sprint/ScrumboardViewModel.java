package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.nodes.scrumboard.Scrumboard;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.binding.Bindings;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumboardViewModel implements Loadable<Sprint>, ViewModel {

    private Scrumboard scrumboard;

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        scrumboard.setStories(sprint.getStories());
    }

    public void setScrumboard(Scrumboard scrumboard) {
        this.scrumboard = scrumboard;
    }
}

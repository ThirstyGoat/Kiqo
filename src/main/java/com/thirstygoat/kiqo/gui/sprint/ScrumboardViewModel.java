package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.nodes.scrumboard.ScrumBoard;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import de.saxsys.mvvmfx.ViewModel;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardViewModel implements Loadable<Sprint>, ViewModel {

    private ScrumBoard scrumBoard;

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        scrumBoard.setStories(sprint.getStories());
    }

    public void setScrumBoard(ScrumBoard scrumBoard) {
        this.scrumBoard = scrumBoard;
    }
}

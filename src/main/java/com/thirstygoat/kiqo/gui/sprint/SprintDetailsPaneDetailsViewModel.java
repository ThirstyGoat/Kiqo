package com.thirstygoat.kiqo.gui.sprint;

import javafx.collections.ListChangeListener;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneDetailsViewModel extends SprintViewModel implements Loadable<Sprint> {
    public static final String PLACEHOLDER = "No stories in sprint";

    private final ListChangeListener<Story> listChangeListener;

    public SprintDetailsPaneDetailsViewModel() {
        listChangeListener = change -> {
            change.next();
            stories().setAll(change.getList());
        };
    }
}



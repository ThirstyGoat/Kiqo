package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel extends SprintViewModel implements Loadable<Sprint> {
    public static final String PLACEHOLDER = "No stories in sprint";

    private final ListChangeListener<Story> listChangeListener;

    public SprintDetailsPaneViewModel() {
        listChangeListener = change -> {
            change.next();
            stories().setAll(change.getList());
        };
    }
}



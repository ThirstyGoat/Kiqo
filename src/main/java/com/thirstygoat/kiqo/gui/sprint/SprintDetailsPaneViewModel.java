package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.GoatViewModel;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel extends SprintViewModel implements Loadable<Sprint>, GoatViewModel {
    public static final String PLACEHOLDER = "No stories in sprint";

    private final ListChangeListener<Story> listChangeListener;

    public SprintDetailsPaneViewModel() {
        super();
        sprintProperty().addListener((observable -> {
            // Listen for changes on the model.
            // If the model changes reload the ViewModel so that it displays the updated info.
            super.reload();
        }));
        listChangeListener = change -> {
            change.next();
            stories().setAll(change.getList());
        };
    }
}



package com.thirstygoat.kiqo.gui.customCells;

import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneDetailsViewModel;
import com.thirstygoat.kiqo.model.Story;
import javafx.scene.control.TableCell;

/**
 * Created by Carina Blair on 19/09/2015.
 */
public class SprintListCell extends TableCell<Story, String> {
    private final SprintDetailsPaneDetailsViewModel vm;

    public SprintListCell(SprintDetailsPaneDetailsViewModel sprintDetailsPaneDetailsViewModel) {
        super();
        vm = sprintDetailsPaneDetailsViewModel;
    }

    @Override
    protected void updateItem(String storyShortName, boolean empty) {
        super.updateItem(storyShortName, empty);
        setText(storyShortName);
    }


}

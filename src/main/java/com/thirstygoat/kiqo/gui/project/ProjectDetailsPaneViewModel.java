package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;

/**
 * Created by leroy on 9/09/15.
 */
public class ProjectDetailsPaneViewModel extends ProjectViewModel implements Editable {
    public final String PLACEHOLDER = "No allocations";

    @Override
    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {
        reload();
    }
}

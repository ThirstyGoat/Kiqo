package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Editable;

public class TeamDetailsPaneViewModel extends TeamViewModel implements Editable {
    @Override
    public void commitEdit() {
        Command command = createCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {
        reload();
    }
}

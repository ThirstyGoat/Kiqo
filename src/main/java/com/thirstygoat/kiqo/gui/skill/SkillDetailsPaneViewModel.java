package com.thirstygoat.kiqo.gui.skill;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Editable;

/**
 * Created by leroy on 25/08/15.
 */
public class SkillDetailsPaneViewModel extends SkillViewModel implements Editable {

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

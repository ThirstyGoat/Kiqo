package com.thirstygoat.kiqo.gui.skill;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.model.Skill;

/**
 * Created by leroy on 25/08/15.
 */
public class SkillDetailsPaneViewModel extends SkillViewModel implements Editable {

    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    public void cancelEdit() {
        reload();
    }
}

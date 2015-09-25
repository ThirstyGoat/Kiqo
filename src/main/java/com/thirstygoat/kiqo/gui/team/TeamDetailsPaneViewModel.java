package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.*;

import javafx.beans.property.*;

public class TeamDetailsPaneViewModel extends TeamViewModel implements Editable {

    private ObjectProperty<MainController> mainControllerProperty = new SimpleObjectProperty<>();

    public ObjectProperty<MainController> mainControllerProperty() {
        return mainControllerProperty;
    }

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

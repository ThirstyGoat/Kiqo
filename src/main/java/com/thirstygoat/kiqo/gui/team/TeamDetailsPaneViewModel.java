package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.MainController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TeamDetailsPaneViewModel extends TeamViewModel implements Editable {

    private ObjectProperty<MainController> mainControllerProperty = new SimpleObjectProperty<>();

    public ObjectProperty<MainController> mainControllerProperty() {
        return mainControllerProperty;
    }

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

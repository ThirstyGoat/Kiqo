package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.MainController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by leroy on 9/09/15.
 */
public class ProjectDetailsPaneViewModel extends ProjectViewModel implements Editable {
    public final String PLACEHOLDER = "No allocations";

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

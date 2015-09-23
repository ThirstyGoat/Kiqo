package com.thirstygoat.kiqo.gui.release;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReleaseDetailsPaneViewModel extends ReleaseViewModel implements Editable {

    private StringProperty dateStringProperty;
    
    public ReleaseDetailsPaneViewModel() {
        super();
        dateStringProperty = new SimpleStringProperty("");
        dateStringProperty.bind(Bindings.createStringBinding(() -> {
            return dateProperty().get() != null 
                    ? dateProperty().get().format(Utilities.DATE_FORMATTER)
                    : "";
        }, dateProperty()));
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
    
    protected StringProperty dateStringProperty() {
        return dateStringProperty;
    }
}

package com.thirstygoat.kiqo.gui.release;

import java.util.List;
import java.util.function.Supplier;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.util.Utilities;

public class ReleaseDetailsPaneViewModel extends ReleaseViewModel implements Editable {

    private StringProperty dateStringProperty;
    
    public ReleaseDetailsPaneViewModel() {
        super();
        dateStringProperty = new SimpleStringProperty("");
        dateStringProperty.bind(Bindings.createStringBinding(() -> {
            return dateProperty().get() != null 
                    ? dateProperty().get().format(Utilities.DATE_TIME_FORMATTER)
                    : "";
        }, dateProperty()));
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
    
    protected StringProperty dateStringProperty() {
        return dateStringProperty;
    }

    public Supplier<List<Project>> projectSupplier() {
        return () -> {
            return organisationProperty().get().getProjects();
        };
    }
}

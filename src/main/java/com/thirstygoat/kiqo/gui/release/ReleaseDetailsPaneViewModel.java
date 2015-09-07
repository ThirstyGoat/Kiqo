package com.thirstygoat.kiqo.gui.release;

import java.time.format.DateTimeFormatter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

public class ReleaseDetailsPaneViewModel extends ReleaseViewModel implements Editable {

    private StringProperty projectNameProperty;
    private StringProperty dateStringProperty;
    
    public ReleaseDetailsPaneViewModel() {
        super();
        projectNameProperty = new SimpleStringProperty("");
        dateStringProperty = new SimpleStringProperty("");
        dateStringProperty.bind(Bindings.createStringBinding(() -> {
            return dateProperty().get() != null 
                    ? dateProperty().get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "";
        }, dateProperty()));
    }
    
    @Override
    public void load(Release release, Organisation organisation) {
        super.load(release, organisation);
        projectNameProperty.bindBidirectional(projectProperty(), StringConverters.projectStringConverter(organisation));
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
    
    protected StringProperty projectNameProperty() {
        return projectNameProperty;
    } 
    
    protected StringProperty dateStringProperty() {
        return dateStringProperty;
    }
}

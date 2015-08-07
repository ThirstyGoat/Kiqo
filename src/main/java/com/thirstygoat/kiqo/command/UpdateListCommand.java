package com.thirstygoat.kiqo.command;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public final class UpdateListCommand<T> extends Command {

    private final List<T> oldContents;
    private final List<T> newContents;
    private final ObservableList<T> observableList;
    private final String type;

    public UpdateListCommand(String type, List<T> newContents, ObservableList<T> observableList) {
        super();
        this.oldContents = new ArrayList<>(observableList);
        this.newContents = newContents;
        this.observableList = observableList;
        this.type = type;
    }
    
    @Override
    public void execute() {
        observableList.setAll(newContents);
    }

    @Override
    public void undo() {
        observableList.setAll(oldContents);
    }

    @Override
    public String toString() {
        // TODO implement properly
        return "<Edit ";
    }

    @Override
    public String getType() {
        return type;
    }
}
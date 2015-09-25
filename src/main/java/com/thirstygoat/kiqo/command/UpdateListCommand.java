package com.thirstygoat.kiqo.command;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public final class UpdateListCommand<T> extends Command {

    private final List<T> oldContents;
    private final List<T> newContents = new ArrayList<>();
    private final ObservableList<T> observableList;
    private final String type;

    public UpdateListCommand(String type, List<T> newContents, ObservableList<T> observableList) {
        super();
        this.oldContents = new ArrayList<>(observableList);
        this.newContents.addAll(newContents);
        this.observableList = observableList;
        this.type = type;
    }
    
    @Override
    public void execute() {
    	observableList.clear();
        observableList.addAll(newContents);
    }

    @Override
    public void undo() {
    	observableList.clear();
        observableList.addAll(oldContents);
    }

    @Override
    public String toString() {
        // TODO implement properly
        return "<Edit " + type + ">";
    }

    @Override
    public String getType() {
        return type;
    }
}
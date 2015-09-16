package com.thirstygoat.kiqo.gui.nodes.BiControl;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.List;

/**
 * Created by leroy on 16/09/15.
 */
public class ListBiControl<D extends ListView, E extends Control, S> extends BiControl<D, E, ListProperty<S>> {

    private ListProperty<S> data;

    public void setDisplayCellFactory(Callback<ListView<S>, ListCell<S>> cellFactory) {
        this.getSkin().setDisplayCellFactory(cellFactory);
    }

    public void setEditCellFactory(Callback<ListView<S>, ListCell<S>> cellFactory) {
        this.getSkin().setEditCellFactory(cellFactory);
    }

    @Override public ListProperty<S> dataProperty() {
        return data;
    }
}


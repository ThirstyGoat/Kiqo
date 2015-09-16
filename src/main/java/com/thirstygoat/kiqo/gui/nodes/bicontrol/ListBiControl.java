package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.ListView;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class ListBiControl<S> extends BiControl<ListView<S>, ListView<S>, ObservableList<S>> {

    private ListProperty<S> data;
    
    public ListBiControl() {
        super();
        data = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @Override
    public ObservableList<S> getData() {
        return data;
    }
    
    @Override
    public void setData(ObservableList<S> data) {
        this.data.set(data);
    }

    @Override
    public ListBiControlSkin<S> createDefaultSkin() {
        return new ListBiControlSkin<S>(this);
    }
}


package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import org.controlsfx.control.CheckListView;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class ListBiControlSkin<S> extends BiControlSkin<ListView<S>, ListView<S>, ObservableList<S>> {

    protected ListBiControlSkin(ListBiControl<S> listBiControl) {
        super(listBiControl);
        editView.itemsProperty().set(listBiControl.getData()); // TODO setItems?
        displayView.itemsProperty().set(listBiControl.getData());
    }
    
    @Override
    protected ListView<S> makeEditView() {
        ListView<S> listView = new ListView<S>();
        listView.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        return listView;
    }

    @Override
    protected ListView<S> makeDisplayView() {
        ListView<S> listView = new ListView<S>();
        listView.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        return listView;
    }
    
    protected void setEditCellFactory(Callback<ListView<S>, ListCell<S>> cellFactory) {
        editView.setCellFactory(cellFactory);
    }
    
    protected void setDisplayCellFactory(Callback<ListView<S>, ListCell<S>> cellFactory) {
        displayView.setCellFactory(cellFactory);
    }
}

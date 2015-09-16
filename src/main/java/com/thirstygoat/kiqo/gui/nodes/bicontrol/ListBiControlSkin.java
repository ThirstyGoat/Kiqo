package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;

/**
 * Created by leroy on 16/09/15.
 * @param <E> type of edit listview
 * @param <S> type of list elements
 */
public abstract class ListBiControlSkin<S extends Item> extends BiControlSkin<ListView<S>, GoatFilteredListSelectionView<S>, ObservableList<S>> {
    protected ListBiControlSkin(FilteredListBiControl<S> listBiControl) {
        super(listBiControl);
        editView.itemsProperty().set(listBiControl.getData()); // TODO setItems?
        displayView.itemsProperty().set(listBiControl.getData());
    }

    @Override
    protected ListView<S> makeDisplayView() {
        ListView<S> listView = new ListView<S>();
        return listView;
    }
    
    protected void setEditCellFactory(Callback<ListView<S>, ListCell<S>> cellFactory) {
        editView.setCellFactory(cellFactory);
    }
    
    protected void setDisplayCellFactory(Callback<ListView<S>, ListCell<S>> cellFactory) {
        displayView.setCellFactory(cellFactory);
    }
}

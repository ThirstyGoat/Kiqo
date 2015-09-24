package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControl<D extends Control, S extends Item> extends BiControl<D, GoatFilteredListSelectionView<S>, ListProperty<S>> {

    private final ListProperty<S> selectedItems;
    private final ListProperty<S> allItems;
    
    public FilteredListBiControl() {
        super();
        selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        allItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public ListProperty<S> allItems() {
        return allItems;
    }
    
    public ListProperty<S> selectedItems() {
        return selectedItems;
    }
}


package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControl<D extends Control, S> extends BiControl<D, GoatFilteredListSelectionView<S>, ListProperty<S>> {

    private final ListProperty<S> selectedItems;
    private final ListProperty<S> unselectedItems;
    
    public FilteredListBiControl() {
        super();
        selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        unselectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public ListProperty<S> unselectedItems() {
        return unselectedItems;
    }
    
    public ListProperty<S> selectedItems() {
        return selectedItems;
    }

}


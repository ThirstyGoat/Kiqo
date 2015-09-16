package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.ListView;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControl<S> extends BiControl<ListView<S>, GoatFilteredListSelectionView<S>, ListProperty<S>> {

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


package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.ListView;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControl<S extends Item> extends BiControl<ListView<S>, GoatFilteredListSelectionView<S>, ObservableList<S>> {

    private ListProperty<S> allItems;
    
    public FilteredListBiControl() {
        super();
        allItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @Override
    public ObservableList<S> getData() {
        return allItems;
    }
    
    @Override
    public void setData(ObservableList<S> data) {
        this.allItems.set(data);
    }
}


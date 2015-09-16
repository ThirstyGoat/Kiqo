package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.util.Callback;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControl<S> extends BiControl<ListView<S>, GoatFilteredListSelectionView<S>, ListProperty<S>> {

    private final ListProperty<S> selectedItems;
    private final ListProperty<S> allItems;
    private final ObjectProperty<Callback<ListView<S>, ListCell<S>>> displayCellFactory;
    private final ObjectProperty<Callback<ListView<S>, ListCell<S>>> editCellFactory;
    
    public FilteredListBiControl() {
        super();
        selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        allItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        displayCellFactory = new SimpleObjectProperty<>();
        editCellFactory = new SimpleObjectProperty<>();
    }

    @Override
    public ListProperty<S> getData() {
        return selectedItems;
    }
    
    @Override
    public void setData(ListProperty<S> data) {
        this.selectedItems.set(data);
    }
    
    public ObservableList<S> getAllItems() {
        return allItems;
    }

    public void setAllItems(ObservableList<S> allItems) {
        this.allItems.set(allItems);
    }

    public void setDisplayCellFactory(Callback<ListView<S>, ListCell<S>> displayCellFactory) {
        this.displayCellFactory.set(displayCellFactory);
    }
    
    public void setEditCellFactory(Callback<ListView<S>, ListCell<S>> editCellFactory) {
        this.editCellFactory.set(editCellFactory);
    }

    public ObjectProperty<Callback<ListView<S>, ListCell<S>>> displayCellFactory() {
        return displayCellFactory;
    }

    public ObjectProperty<Callback<ListView<S>, ListCell<S>>> getEditCellFactory() {
        return editCellFactory;
    }
}


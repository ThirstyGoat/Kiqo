package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;

import javafx.beans.property.ListProperty;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.util.Callback;

public class FilteredListBiControlSkin<S extends Item> extends ListBiControlSkin<S> {
    private Runnable onCommit;
    private Runnable onCancel; // TODO

    public FilteredListBiControlSkin(FilteredListBiControl<S> listBiControl, 
            Runnable onCommit, Runnable onCancel, 
            ListProperty<S> selectedList, ListProperty<S> eligibleList,
            Callback<ListView<S>, ListCell<S>> displayCellFactory) {
        super(listBiControl);
        this.onCommit = onCommit;
        this.onCancel = onCancel;
        editView.setTargetItems(selectedList);
        editView.setSourceItems(eligibleList);
        displayView.setItems(selectedList);
        displayView.setCellFactory(displayCellFactory);
    }
    
    @Override
    protected void onCancelAction(ActionEvent event) {
        super.onCancelAction(event);
        onCancel.run();
    }
    
    @Override
    protected void onDoneAction(ActionEvent event) {
        super.onDoneAction(event);
        onCommit.run();
    }

    @Override
    protected GoatFilteredListSelectionView<S> makeEditView() {
        return new GoatFilteredListSelectionView<S>();
    }
}

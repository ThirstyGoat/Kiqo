package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControlSkin<S> extends BiControlSkin<ListView<S>, GoatFilteredListSelectionView<S>, ListProperty<S>> {
    private static final double PREF_HEIGHT = 200;
    protected Runnable onCommit;
    protected Runnable onCancel;

    public FilteredListBiControlSkin(FilteredListBiControl<S> listBiControl, 
            Runnable onCommit, Runnable onCancel, 
            Callback<ListView<S>, ListCell<S>> displayCellFactory, 
            Callback<S, StringProperty> stringPropertyCallback) {
        super(listBiControl);
        this.onCommit = onCommit;
        this.onCancel = onCancel;
        
        displayView.setItems(listBiControl.selectedItems());
        displayView.setCellFactory(displayCellFactory != null ? displayCellFactory : createDefaultCellFactory(stringPropertyCallback));
        
        editView.setStringPropertyCallback(stringPropertyCallback);
        editView.setTargetItems(listBiControl.selectedItems());
        editView.setSourceItems(listBiControl.allItems());
    }

    private Callback<ListView<S>, ListCell<S>> createDefaultCellFactory(Callback<S, StringProperty> stringPropertyCallback) {
        return listView -> {
            return new ListCell<S>() {
                @Override
                protected void updateItem(S s, boolean empty) {
                    super.updateItem(s, empty);
                    textProperty().unbind();
                    if (s != null && !empty) {
                        textProperty().bind(stringPropertyCallback.call(s));
                    } else {
                        textProperty().set("");
                    }
                }
            };
        };
    }

    @Override
    protected ListView<S> makeDisplayView() {
        ListView<S> view = new ListView<S>();
        view.setPrefHeight(PREF_HEIGHT);
        return view;
    }

    @Override
    protected GoatFilteredListSelectionView<S> makeEditView() {
        GoatFilteredListSelectionView<S> view = new GoatFilteredListSelectionView<S>();
        view.setPrefHeight(PREF_HEIGHT);
        return view;
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
        editView.resetFilter();
    }
}

package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.model.Item;

import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.util.Callback;

public class ListViewFilteredListBiControlSkin<S extends Item> extends FilteredListBiControlSkin<ListView<S>, S> {

	public ListViewFilteredListBiControlSkin(FilteredListBiControl<ListView<S>, S> listBiControl, 
			Runnable onCommit, Runnable onCancel,
            Callback<S, StringProperty> stringPropertyCallback) {
		super(listBiControl, onCommit, onCancel, stringPropertyCallback);
		
		displayView.itemsProperty().bind(listBiControl.selectedItems());
        displayView.setCellFactory(createDefaultCellFactory(stringPropertyCallback));
	}
	
	/**
	 * 
	 * @param stringPropertyCallback
	 * @return
	 */
	private Callback<ListView<S>, ListCell<S>> createDefaultCellFactory(
            Callback<S, StringProperty> stringPropertyCallback) {
        return listView -> {
            return new ListCell<S>() {
                @Override
                protected void updateItem(S item, boolean empty) {
                    if (!empty) {
                        textProperty().bind(stringPropertyCallback.call(item));
                    } else {
                        textProperty().unbind();
                        setText("");
                    }
                    super.updateItem(item, empty);
                }
            };
        };
    }

    @Override
    protected ListView<S> makeDisplayView() {
        ListView<S> view = new ListView<S>();
        view.setPrefHeight(VIEW_HEIGHT);
        return view;
    }

}

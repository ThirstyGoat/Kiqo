package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public class FilteredListBiControlSkin<S extends Item> 
        extends BiControlSkin<ListView<S>, GoatFilteredListSelectionView<S>, ListProperty<S>> {
    protected static final double VIEW_HEIGHT = 200;
    
    /**
     * Constructor
     * @param listBiControl control to be skinned
     * @param onCommit eg. commit viewModel state to model
     * @param onCancel eg. rollback viewModel state to last model state
     * @param displayCellFactory cell factory for listcells in display list
     * @param stringPropertyCallback callback to extract a StringProperty from an S, for display
     */
    public FilteredListBiControlSkin(FilteredListBiControl<ListView<S>, S> listBiControl, 
            Runnable onCommit, Runnable onCancel, 
            Callback<S, StringProperty> stringPropertyCallback) {
        super(listBiControl, onCommit, onCancel, false);
        
        editView.bindSelectedItems(listBiControl.selectedItems());
     	editView.bindAllItems(listBiControl.allItems());
        editView.setStringPropertyCallback(stringPropertyCallback);
        
        displayView.itemsProperty().bind(listBiControl.selectedItems());
        displayView.setCellFactory(createDefaultCellFactory(stringPropertyCallback));
	}
    
    @Override
    protected GoatFilteredListSelectionView<S> makeEditView() {
        GoatFilteredListSelectionView<S> view = new GoatFilteredListSelectionView<S>();
        view.setPrefHeight(VIEW_HEIGHT);
        return view;
    }
    
    @Override
    protected void enterEditMode() {
        super.enterEditMode();
        editView.resetFilter();
    }
    
    @Override
    protected void attachListeners() {
    	super.attachListeners();
    	editView._focusedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> { // hacky stuff (fixes issue with focus being lost when clicking on segmented button
                if (!editView._focusedProperty().get() && !newValue && !doneButtonIsFocused()) {
                    onCancelAction(new ActionEvent());
                }
            });
        });
    }



	/**
	 * 
	 * @param stringPropertyCallback
	 * @return
	 */
	protected Callback<ListView<S>, ListCell<S>> createDefaultCellFactory(Callback<S, StringProperty> stringPropertyCallback) {
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

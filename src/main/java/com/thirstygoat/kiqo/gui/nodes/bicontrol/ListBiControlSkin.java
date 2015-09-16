package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.beans.property.ListProperty;
import javafx.scene.control.ListView;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;

/**
 * Created by leroy on 16/09/15.
 * @param <S> type of list elements
 */
public abstract class ListBiControlSkin<S> extends BiControlSkin<ListView<S>, GoatFilteredListSelectionView<S>, ListProperty<S>> {
    protected ListBiControlSkin(FilteredListBiControl<S> listBiControl) {
        super(listBiControl);
        displayView.cellFactoryProperty().bindBidirectional(listBiControl.displayCellFactory());
        editView.setItems(listBiControl.getData());
        displayView.setItems(listBiControl.getData());
    }

    @Override
    protected ListView<S> makeDisplayView() {
        ListView<S> listView = new ListView<S>();
        return listView;
    }
}

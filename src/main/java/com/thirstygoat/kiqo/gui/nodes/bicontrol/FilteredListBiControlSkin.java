package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;

import javafx.scene.control.*;

public class FilteredListBiControlSkin<S extends Item> extends ListBiControlSkin<S> {
    public FilteredListBiControlSkin(ListBiControl<S> listBiControl) {
        super(listBiControl);
    }

    @Override
    protected ListView<S> makeEditView() {
        ListView<S> listView = new GoatFilteredListSelectionView<S>();
        listView.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        return listView;
    }
}

package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.model.Item;

import javafx.scene.control.*;

public class ListViewFilteredListBiControl<S extends Item> extends FilteredListBiControl<ListView<S>, S> {

//	@Override
//	public FilteredListBiControlSkin<ListView<S>, S> createDefaultSkin() {
//		Runnable noop = () -> {};
//		return new ListViewFilteredListBiControlSkin<>(this, noop, noop, Item::shortNameProperty);
//	}

}

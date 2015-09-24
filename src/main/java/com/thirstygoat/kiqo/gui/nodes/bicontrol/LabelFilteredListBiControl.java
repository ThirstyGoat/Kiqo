package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.model.Item;

import javafx.scene.control.Label;

public class LabelFilteredListBiControl<S extends Item> extends FilteredListBiControl<Label, S> {

	@Override
	public FilteredListBiControlSkin<Label, S> createDefaultSkin() {
		Runnable noop = () -> {};
		return new LabelFilteredListBiControlSkin<>(this, noop, noop, Item::shortNameProperty);
	}

}

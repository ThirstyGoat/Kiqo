package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.util.Utilities;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.util.Callback;

public class LabelFilteredListBiControlSkin<S extends Item> extends FilteredListBiControlSkin<Label, S> {

	public LabelFilteredListBiControlSkin(FilteredListBiControl<Label, S> listBiControl, Runnable onCommit,
			Runnable onCancel, Callback<S, StringProperty> stringPropertyCallback) {
		super(listBiControl, onCommit, onCancel, stringPropertyCallback);
		
		displayView.textProperty().bind(Utilities.commaSeparatedValuesBinding(listBiControl.selectedItems()));
	}

	@Override
	protected Label makeDisplayView() {
		return new Label();
	}

}

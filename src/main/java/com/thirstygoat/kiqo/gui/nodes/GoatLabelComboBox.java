package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.ComboBox;

import com.thirstygoat.kiqo.model.Item;

public class GoatLabelComboBox<T> extends GoatLabel<T, ComboBox<T>, GoatLabelComboBoxSkin<T>> {

    @Override
    protected GoatLabelComboBoxSkin<T> initSkin() {
        return new GoatLabelComboBoxSkin<T>(this);
    }

    @Override
    public ComboBox<T> getEditField() {
        return editField;
    }

    @Override
    protected void populateEditField() {
        editField.setValue(editField.getConverter().fromString(displayLabel.getText()));
    }

}

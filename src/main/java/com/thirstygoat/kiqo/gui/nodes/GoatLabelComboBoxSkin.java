package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;

public class GoatLabelComboBoxSkin<T> extends GoatLabelSkin<ComboBox<T>> {

    protected GoatLabelComboBoxSkin(@SuppressWarnings("rawtypes") GoatLabelComboBox control) {
        super(control);
    }

    @Override
    protected void setSizing() {
    }

    @Override
    protected ComboBox<T> createEditField() {
        return new ComboBox<T>();
    }

    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_PREF_SIZE);
        editField.setMaxHeight(Control.USE_PREF_SIZE);
        editView.setMinHeight(Control.USE_PREF_SIZE);
        editView.setMaxHeight(Control.USE_PREF_SIZE);
    }

}

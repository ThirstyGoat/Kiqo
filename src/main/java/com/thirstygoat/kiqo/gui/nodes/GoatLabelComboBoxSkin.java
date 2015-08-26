package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.ComboBox;

public class GoatLabelComboBoxSkin<T> extends GoatLabelSkin<ComboBox<T>> {

    protected GoatLabelComboBoxSkin(@SuppressWarnings("rawtypes") GoatLabelComboBox control) {
        super(control);
    }

    @Override
    protected void setSizing() {
        // do nothing
    }

    @Override
    protected ComboBox<T> createEditField() {
        return new ComboBox<T>();
    }

    @Override
    protected void showEditField() {
        // do nothing
    }

}

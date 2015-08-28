package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.TextField;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextField extends GoatLabel<TextField> {

    @Override
    protected void populateEditField() {
        editField.setText(displayLabel.getText());
    }

    @Override
    protected GoatLabelSkin<TextField> initSkin() {
        return new GoatLabelTextFieldSkin(this);
    }

    @Override
    public TextField getEditField() {
        return editField;
    }
}
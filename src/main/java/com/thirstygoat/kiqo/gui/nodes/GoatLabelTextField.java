package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.TextField;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextField extends GoatLabel {

    public GoatLabelTextField() {
        super();
    }

    @Override
    protected void populateEditField() {
        ((TextField) editField).setText(displayLabel.getText());
    }

    @Override
    protected GoatLabelSkin initSkin() {
        return new GoatLabelTextFieldSkin(this);
    }

    @Override
    public TextField getEditField() {
        return (TextField) editField;
    }
}

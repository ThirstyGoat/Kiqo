package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.TextArea;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextArea extends GoatLabel {

    @Override
    protected void populateEditField() {
        ((TextArea) editField).setText(displayLabel.getText());
    }

    @Override
    protected GoatLabelSkin initSkin() {
        return new GoatLabelTextAreaSkin(this);
    }

    @Override
    public TextArea getEditField() {
        return (TextArea) editField;
    }
}
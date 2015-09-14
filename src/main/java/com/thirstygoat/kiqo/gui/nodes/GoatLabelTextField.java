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

    /**
     * Restricts the input to the text field to be only [0-9.] if restrict is true, allows all input otherwise.
     *
     * @param restrict
     */
    public void restrictToNumericInput(boolean restrict) {
        skin.restrictToNumericInput = restrict;
    }
}

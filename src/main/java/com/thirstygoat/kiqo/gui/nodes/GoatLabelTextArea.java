package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextArea extends GoatLabel<TextArea> {

    @Override
    protected void populateEditField() {
        editField.setText(displayLabel.getText());
    }

    @Override
    protected GoatLabelSkin<TextArea> initSkin() {
        return new GoatLabelTextAreaSkin(this);
    }

    @Override
    public TextArea getEditField() {
        return editField;
    }

    @Override
    protected void setEnterAction() {
        skin.getEditField().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    int caretPos = getEditField().getCaretPosition();
                    getEditField().setText(getEditField().getText().substring(0, caretPos)
                            + '\n'
                            + getEditField().getText().substring(caretPos
                    ));
                    getEditField().positionCaret(caretPos + 1);
                } else {
                    event.consume();
                    doneAction();
                }
            }
        });
    }
}
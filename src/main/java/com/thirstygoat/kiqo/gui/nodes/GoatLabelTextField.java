package com.thirstygoat.kiqo.gui.nodes;


import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextField extends GoatLabel {
    private TextField editField;

    public GoatLabelTextField() {
        super();
    }

    @Override
    protected void setButtonBindings() {
        editButton.setOnAction(event -> {
            skin.showEdit();
            editField.setText(displayLabel.getText());
        });

        doneButton.setOnAction(event -> {
            skin.showDisplay();
        });

        editField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                skin.showDisplay();
            }
        });
    }

    @Override
    protected void setSkin() {
        skin = new GoatLabelTextFieldSkin(this);
        displayLabel = skin.getDisplayLabel();
        editField = (TextField) skin.getEditField();
        editButton = skin.getEditButton();
        doneButton = skin.getDoneButton();
    }

    @Override
    public TextField getEditField() {
        return editField;
    }
}

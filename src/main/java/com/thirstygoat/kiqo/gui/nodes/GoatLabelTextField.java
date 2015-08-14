package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Skill;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.function.Function;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextField extends GoatLabel {
    private TextField editField;

    public GoatLabelTextField() {
        super();
    }

    @Override
    protected void populateEditField() {
        editField.setText(displayLabel.getText());
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

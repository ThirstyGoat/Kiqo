package com.thirstygoat.kiqo.gui.nodes;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelTextArea<T extends Item> extends Control {
    public final GoatLabelTextAreaSkin skin;
    private Label displayLabel;
    private TextArea editField;
    private Button editButton;
    private Button doneButton;
    private T item;
    private String fieldName;
    private StringProperty currentVal;
    private EditCommand command;

    public GoatLabelTextArea() {
        super();
        skin = new GoatLabelTextAreaSkin(this) {
            {
                displayLabel = getDisplayLabel();
                editField = getEditField();
                editButton = getEditButton();
                doneButton = getDoneButton();
            }
        };

        setSkin(skin);

        editButton.setOnAction(event -> {
            editField.setText(displayLabel.getText());
            skin.showEdit();
        });

        doneButton.setOnAction(event -> {
            if (!editField.getText().equals(currentVal.get())) {
                command = new EditCommand<>(item, fieldName, editField.getText());
                UndoManager.getUndoManager().doCommand(command);
            }
            skin.showDisplay();
        });
    }

    public Button doneButton() {
        return doneButton;
    }

    public StringProperty textProperty() {
        return displayLabel.textProperty();
    }

    public TextArea getEditField() {
        return editField;
    }

    public void setItem(T item, String fieldName, StringProperty currentVal) {
        this.item = item;
        this.fieldName = fieldName;
        this.currentVal = currentVal;
    }

    public void setText(String text) {
        displayLabel.textProperty().setValue(text);
    }
}
package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatEditableTextArea<T extends Item> extends Control {
    public final GoatEditableTextAreaSkin skin;
    private Label displayLabel;
    private TextArea editField;
    private Button editButton;
    private Button doneButton;
    private T item;
    private String fieldName;
    private StringProperty currentVal;
    private EditCommand command;

    public GoatEditableTextArea() {
        super();
        skin = new GoatEditableTextAreaSkin(this) {
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

            displayLabel.textProperty().unbind();
            displayLabel.setText(editField.getText());
            displayLabel.textProperty().bind(currentVal);

            if (!editField.getText().equals(currentVal.get())) {
                command = new EditCommand<>(item, fieldName, editField.getText());
                UndoManager.getUndoManager().doCommand(command);
            }
            skin.showDisplay();
        });

        editField.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                event.consume();
//                skin.showDisplay();
//
//                displayLabel.textProperty().unbind();
//                displayLabel.setText(editField.getText());
//                displayLabel.textProperty().bind(currentVal);
//
//                if (!editField.getText().equals(currentVal.get())) {
//                    command = new EditCommand<>(item, fieldName, editField.getText());
//                    UndoManager.getUndoManager().doCommand(command);
//                }
//            }
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

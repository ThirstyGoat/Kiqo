package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.Item;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabel<T extends Item> extends Control {
    public final GoatLabelSkin skin;
    private Label displayLabel;
    private TextField editField;
    private Button editButton;
    private Button doneButton;
    private T item;
    private String fieldName;
    private StringProperty currentVal;
    private EditCommand command;

    public GoatLabel() {
        super();
        skin = new GoatLabelSkin(this) {
            {
                displayLabel = getDisplayLabel();
                editField = getEditField();
                editButton = getEditButton();
                doneButton = getDoneButton();
            }
        };

        setSkin(skin);

        editButton.setOnAction(event -> {
            skin.showEdit();
            editField.setText(displayLabel.getText());
        });

        doneButton.setOnAction(event -> {
            skin.showDisplay();

            displayLabel.textProperty().unbind();
            displayLabel.setText(editField.getText());
            displayLabel.textProperty().bind(currentVal);

            if (!editField.getText().equals(currentVal.get())) {
                command = new EditCommand<>(item, fieldName, editField.getText());
                UndoManager.getUndoManager().doCommand(command);
            }
        });

        editField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                skin.showDisplay();

                displayLabel.textProperty().unbind();
                displayLabel.setText(editField.getText());
                displayLabel.textProperty().bind(currentVal);

                if (!editField.getText().equals(currentVal.get())) {
                    command = new EditCommand<>(item, fieldName, editField.getText());
                    UndoManager.getUndoManager().doCommand(command);
                }
            }
        });

    }

    public Button doneButton() {
        return doneButton;
    }

    public StringProperty textProperty() {
        return displayLabel.textProperty();
    }

    public TextField getEditField() {
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

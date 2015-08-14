package com.thirstygoat.kiqo.gui.nodes;

import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatComboBoxLabel<T extends Item> extends Control {
    public final GoatComboBoxLabelSkin skin;
    private Label displayLabel;
    private ComboBox comboBox;
    private Button editButton;
    private Button doneButton;
    private T item;
    private String fieldName;
    private ObjectProperty currentVal;
    private EditCommand command;
    private ArrayList options;

    public GoatComboBoxLabel() {
        super();
        skin = new GoatComboBoxLabelSkin(this) {
            {
                displayLabel = getDisplayLabel();
                comboBox = getComboBox();
                editButton = getEditButton();
                doneButton = getDoneButton();
            }
        };

        setSkin(skin);


        editButton.setOnAction(event -> {
            skin.showEdit();
            comboBox.setValue(displayLabel.getText());
        });

        doneButton.setOnAction(event -> {
            skin.showDisplay();

            displayLabel.textProperty().unbind();
            displayLabel.setText(comboBox.getValue().toString());
            displayLabel.textProperty().bind(currentVal.asString());

            if (!comboBox.getValue().equals(currentVal.get())) {
                command = new EditCommand<>(item, fieldName, comboBox.getValue());
                UndoManager.getUndoManager().doCommand(command);
            }
        });

        comboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                skin.showDisplay();

                displayLabel.textProperty().unbind();
                displayLabel.setText(comboBox.getValue().toString());
                displayLabel.textProperty().bind(currentVal);

                if (!comboBox.getValue().equals(currentVal.get())) {
                    command = new EditCommand<>(item, fieldName, comboBox.getValue());
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

    public ComboBox getComboBox() {
        return comboBox;
    }

    public void setItem(T item, String fieldName, ObjectProperty currentVal, ArrayList options) {
        this.item = item;
        this.fieldName = fieldName;
        this.currentVal = currentVal;
        this.options = options;
        comboBox.setItems(FXCollections.observableArrayList(options));
    }

    public void setText(String text) {
        displayLabel.textProperty().setValue(text);
    }
}

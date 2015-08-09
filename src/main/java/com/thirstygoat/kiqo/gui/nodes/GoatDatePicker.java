package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.util.converter.DateStringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatDatePicker<T extends Item> extends Control {
    public final GoatDatePickerSkin skin;
    private Label displayLabel;
    private DatePicker datePicker;
    private Button editButton;
    private Button doneButton;
    private T item;
    private String fieldName;
    private ObjectProperty<LocalDate> currentVal;
    private EditCommand command;

    public GoatDatePicker() {
        super();
        skin = new GoatDatePickerSkin(this) {
            {
                displayLabel = getDisplayLabel();
                datePicker = getDatePicker();
                editButton = getEditButton();
                doneButton = getDoneButton();
            }
        };

        setSkin(skin);

        editButton.setOnAction(event -> {
            skin.showEdit();
            datePicker.setValue(LocalDate.parse(displayLabel.getText()));

        });

        doneButton.setOnAction(event -> {
            skin.showDisplay();

            displayLabel.textProperty().unbind();
            displayLabel.setText(datePicker.getValue().toString());
            displayLabel.textProperty().bind(currentVal.asString());

//            displayLabel.textProperty().bind(Bindings.createStringBinding(() -> dtf.format(datePicker.getValue())));


            if (!datePicker.getValue().equals(currentVal.get())) {
                command = new EditCommand<>(item, fieldName, datePicker.getValue());
                UndoManager.getUndoManager().doCommand(command);
            }
        });

//        datePicker.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                event.consume();
//                skin.showDisplay();
//
//                displayLabel.textProperty().unbind();
//                displayLabel.setText(datePicker.getText());
//                displayLabel.textProperty().bind(currentVal);
//
//                if (!datePicker.getText().equals(currentVal.get())) {
//                    command = new EditCommand<>(item, fieldName, datePicker.getText());
//                    UndoManager.getUndoManager().doCommand(command);
//                }
//            }
//        });

    }

    public Button doneButton() {
        return doneButton;
    }

    public StringProperty textProperty() {
        return displayLabel.textProperty();
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void setItem(T item, String fieldName, ObjectProperty<LocalDate> currentVal) {
        this.item = item;
        this.fieldName = fieldName;
        this.currentVal = currentVal;
    }

    public void setText(String text) {
        displayLabel.textProperty().setValue(text);
    }
}

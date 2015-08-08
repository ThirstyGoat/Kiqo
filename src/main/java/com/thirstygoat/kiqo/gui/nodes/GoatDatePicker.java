package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
* Created by Carina Blair on 8/08/2015.
*/
public class GoatDatePicker <T extends Item> extends Control {
        public final GoatDatePickerSkin skin;
        private Label dateLabel;
        private DatePicker datePicker;
        private Button editButton;
        private Button doneButton;
        private T item;
        private String fieldName;
        private ObjectProperty<LocalDate> currentVal;
        private EditCommand command;
        public static final String DISABLED_CELL_STYLE = "-fx-background-color: #ffc0cb";

        public GoatDatePicker() {
            super();
            skin = new GoatDatePickerSkin(this) {
                {
                    dateLabel = getDateLabel();
                    datePicker = getDatePicker();
                    editButton = getEditButton();
                    doneButton = getDoneButton();
                }
            };

            setSkin(skin);

            editButton.setOnAction(event -> {
                skin.showEdit();
                DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateLabel.textProperty().get(), datetimeFormat);
                datePicker.setValue(date);

            });

            doneButton.setOnAction(event -> {
                skin.showDisplay();

                dateLabel.textProperty().unbind();
                dateLabel.setText(datePicker.getValue().toString());
                dateLabel.textProperty().bind(currentVal.asString());

//                DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                textProperty().bind(currentVal.asString());

                if (!datePicker.getValue().toString().equals(currentVal.get())) {
                    command = new EditCommand<>(item, fieldName, datePicker.getValue());
                    UndoManager.getUndoManager().doCommand(command);
                }
            });

            datePicker.setOnKeyPressed(event -> {

                if (event.getCode() == KeyCode.ENTER) {
                    event.consume();
                    skin.showDisplay();

                    dateLabel.textProperty().unbind();
                    dateLabel.setText(datePicker.getValue().toString());
                    dateLabel.textProperty().bind(currentVal.asString());


                    if (!datePicker.getValue().toString().equals(currentVal.get())) {
                        command = new EditCommand<>(item, fieldName, datePicker.getValue().toString());
                        UndoManager.getUndoManager().doCommand(command);
                    }
                }
            });

        }

        public Button doneButton() {
            return doneButton;
        }

        public StringProperty textProperty() {
            return dateLabel.textProperty();
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
            dateLabel.textProperty().setValue(text);
        }
}

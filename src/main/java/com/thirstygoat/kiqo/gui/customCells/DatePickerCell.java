package com.thirstygoat.kiqo.gui.customCells;

import java.time.LocalDate;

import javafx.scene.control.*;

import com.thirstygoat.kiqo.util.Utilities;

public class DatePickerCell<S> extends TableCell<S, LocalDate> {

    private final DatePicker datePicker;

    public DatePickerCell() {
        this.datePicker = new DatePicker();
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (item != null) {
                if (item.equals(LocalDate.MAX)) {
                    setText("");
                } else {
                    setText(item.format(Utilities.DATE_TIME_FORMATTER));
                }
                setGraphic(null);
            }
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            setGraphic(datePicker);
            setText("");
            datePicker.requestFocus();
        }
    }


    @Override
    public void cancelEdit() {
        updateItem(getItem(), false);
        super.cancelEdit();
    }
}
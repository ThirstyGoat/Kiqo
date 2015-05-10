package com.thirstygoat.kiqo.viewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

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
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    setText(item.format(df));
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
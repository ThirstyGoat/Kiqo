package com.thirstygoat.kiqo.viewModel;

import java.time.LocalDate;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class DatePickerCell<S> extends TableCell<S, LocalDate> {

    private final DatePicker datePicker;

    public DatePickerCell() {
        // Initialize the DatePicker for birthday
        this.datePicker = new DatePicker();


        datePicker.setDayCellFactory(picker -> {
            final DateCell cell = new DateCell();
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                datePicker.setValue(cell.getItem());
                datePicker.hide();
                event.consume();
            });
            return cell ;
        });
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
                    setText(item.toString());
                }
            }
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            setText(null);
            if(getItem().equals(LocalDate.MAX)) {
                datePicker.setValue(LocalDate.now().plusDays(1));
            } else {
                datePicker.setValue(getItem());
            }

            setGraphic(datePicker);
            datePicker.show();
        }
    }


    @Override
    public void cancelEdit() {
        super.cancelEdit();
        updateItem(getItem(), false);
        if (!isEmpty()) {
            datePicker.hide();
        }
    }
}
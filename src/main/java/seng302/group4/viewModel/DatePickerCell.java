package seng302.group4.viewModel;

import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import seng302.group4.Allocation;

import java.time.LocalDate;

public class DatePickerCell<S> extends TableCell<S, LocalDate> {

    private final DatePicker datePicker;

    public DatePickerCell() {
        // Initialize the DatePicker for birthday
        this.datePicker = new DatePicker();

        datePicker.setDayCellFactory(picker -> {
            DateCell cell = new DateCell();
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
            setText((item != null) ? item.toString() : null);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            setText(null);
            datePicker.setValue(getItem());
            setGraphic(datePicker);
            datePicker.show();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        if (!isEmpty()) {
            datePicker.hide();
        }
    }
}
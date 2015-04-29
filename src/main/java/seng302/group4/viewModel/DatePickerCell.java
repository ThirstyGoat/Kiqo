package seng302.group4.viewModel;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;

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

        datePicker.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // If datepicker is no longer showing
                // Check if date has been changed, if so commitEdit
                boolean dateChanged = !(getItem().isEqual(datePicker.getValue()));
                if (dateChanged) {
                    // If the date has been changed, commit the edit
                    commitEdit(datePicker.getValue());
                }
                datePicker.hide();
                setGraphic(null);
                setText(datePicker.getValue().toString());
                cancelEdit();
            }
        });
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
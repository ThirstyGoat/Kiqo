package com.thirstygoat.kiqo.viewModel;

import java.time.LocalDate;

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.nodes.GoatDialog;

/**
 * Created by bradley on 30/04/15.
 */
public class AllocationDatePickerCell extends DatePickerCell<Allocation> {

    private final ChangeListener<LocalDate> datePickerListener;
    private ValidationType type;

    public AllocationDatePickerCell() {
        super();

        datePickerListener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                getDatePicker().setValue(LocalDate.MAX);
            }
            performValidation();
        };

        getDatePicker().valueProperty().addListener(datePickerListener);

        getDatePicker().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // If no longer in focus we need to perform validation
                performValidation();
                cancelEdit();
            }
        });
    }

    private void performValidation() {
        if (type == ValidationType.START_DATE) {
            // Perform START_DATE validation
            startDateValidation(getDatePicker().getValue());
        } else if (type == ValidationType.END_DATE) {
            // Perform END_DATE validation
            endDateValidation((getDatePicker().getValue() != null) ? getDatePicker().getValue() : LocalDate.MAX);
        }
    }

    public void setValidationType(ValidationType type) {
        this.type = type;
    }

    private void startDateValidation(LocalDate date) {
        final com.thirstygoat.kiqo.model.Allocation allocation =
                (com.thirstygoat.kiqo.model.Allocation)getTableRow().getItem();
        // Check to make sure that start date comes before end date if end date is set
        if (date == null || date == LocalDate.MAX) {
            // Alert the user that this is not allowed
            revertDate();
            GoatDialog.showAlertDialog((Stage) getTableView().getScene().getWindow(), "Error", "Error",
                    "Start date must not be empty!");
            return;
        }
        if (allocation.getEndDate() != null && !date.isBefore(allocation.getEndDate())) {
            // Then the start date was set before the end date [the end date is set] - prohibit and alert
            revertDate();
            GoatDialog.showAlertDialog((Stage) getTableView().getScene().getWindow(), "Error", "Error",
                    "Start date must be before end date!");
            return;
        }

        // Check date to make sure it doesn't cause overlap with any of the team's other allocations
        boolean dateRangesOverlap = false;
        for (final com.thirstygoat.kiqo.model.Allocation a : allocation.getTeam().getAllocations()) {
            if (a == allocation) {
                continue;
            }
            // If the end dates are null, then the allocation has no specified period
            // to make things easier, we pretend that they're infinite, ie. LocalDate.MAX
            final LocalDate aEnd = (a.getEndDate() == null) ? LocalDate.MAX : a.getEndDate();
            final LocalDate bEnd = (allocation.getEndDate() == null) ? LocalDate.MAX : allocation.getEndDate();
            if ((a.getStartDate().isBefore(bEnd)) && (aEnd.isAfter(date))) {
                dateRangesOverlap = true;
                break;
            }
        }

        if (dateRangesOverlap) {
            // Then this change would make the allocation overlap with another allocation - prohibit and alert
            revertDate();
            GoatDialog.showAlertDialog((Stage) getTableView().getScene().getWindow(), "Error", "Error",
                    "Allocation can not overlap with another allocation!");
            return;
        }

        acceptChange();
    }

    private void endDateValidation(LocalDate date) {
        final com.thirstygoat.kiqo.model.Allocation allocation =
                (com.thirstygoat.kiqo.model.Allocation)getTableRow().getItem();
        // Check to make sure that end date comes after start date
        LocalDate newDate = date == null ? LocalDate.MAX : date;
        if (!newDate.isAfter(allocation.getStartDate())) {
            // Then the start date was set before the end date [the end date is set] - prohibit and alert
            revertDate();
            GoatDialog.showAlertDialog((Stage) getTableView().getScene().getWindow(), "Error", "Error",
                    "End date must be after start date!");
            return;
        }

        // Check date to make sure it doesn't cause overlap with any of the team's other allocations
        boolean dateRangesOverlap = false;
        for (final com.thirstygoat.kiqo.model.Allocation a : allocation.getTeam().getAllocations()) {
            if (a == allocation) {
                continue;
            }
            // If the end dates are null, then the allocation has no specified period
            // to make things easier, we pretend that they're infinite, ie. LocalDate.MAX
            final LocalDate aEnd = (a.getEndDate() == null) ? LocalDate.MAX : a.getEndDate();
            if ((a.getStartDate().isBefore(newDate)) && (aEnd.isAfter(allocation.getStartDate()))) {
                dateRangesOverlap = true;
                break;
            }
        }

        if (dateRangesOverlap) {
            // Then this change would make the allocation overlap with another allocation - prohibit and alert
            revertDate();
            GoatDialog.showAlertDialog((Stage) getTableView().getScene().getWindow(), "Error", "Error",
                    "Allocation can not overlap with another allocation!");
            return;
        }

        acceptChange();
    }

    private void acceptChange() {
        LocalDate tmpDate = (getDatePicker().getValue() == null) ? LocalDate.MAX : getDatePicker().getValue();
        final boolean dateChanged = !(getItem().isEqual(tmpDate));
        if (dateChanged) {
            // If the date has been changed, commit the edit
            updateItem(tmpDate, false);
            commitEdit(tmpDate);
            cancelEdit();
        }
    }

    private void revertDate() {
        final com.thirstygoat.kiqo.model.Allocation allocation =
                (com.thirstygoat.kiqo.model.Allocation)getTableRow().getItem();
        getDatePicker().valueProperty().removeListener(datePickerListener);
        if (type == ValidationType.START_DATE) {
            updateItem(allocation.getStartDate(), false);
            getDatePicker().setValue(allocation.getStartDate());
        } else if (type == ValidationType.END_DATE) {
            updateItem(allocation.getEndDate(), false);
            getDatePicker().setValue(allocation.getEndDate());
        }
        getDatePicker().valueProperty().addListener(datePickerListener);
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            getDatePicker().valueProperty().removeListener(datePickerListener);
            if (getItem().equals(LocalDate.MAX)) {
                getDatePicker().setValue(null);
            } else {
                getDatePicker().setValue(getItem());
            }
            getDatePicker().valueProperty().addListener(datePickerListener);
        }
        super.startEdit();
    }

    @Override
    public void cancelEdit() {
        // Don't panic. We do nothing.
        super.cancelEdit();
    }

    public enum ValidationType {
        START_DATE,
        END_DATE
    }
}

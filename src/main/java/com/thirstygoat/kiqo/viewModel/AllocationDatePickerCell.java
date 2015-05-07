package com.thirstygoat.kiqo.viewModel;

import java.time.LocalDate;

import javafx.stage.Stage;

import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.nodes.GoatDialog;

/**
 * Created by bradley on 30/04/15.
 */
public class AllocationDatePickerCell extends DatePickerCell<Allocation> {

    private ValidationType type;

    public AllocationDatePickerCell() {
        super();

        getDatePicker().showingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // If datepicker is no longer showing
                // Check if date has been changed, if so commitEdit
                if (type == ValidationType.START_DATE) {
                    // Perform START_DATE Validation
                    startDateValidation(getDatePicker().getValue());
                } else if (type == ValidationType.END_DATE) {
                    // Perform END_DATE Validation
                    endDateValidation(getDatePicker().getValue());
                }

                getDatePicker().hide();
                setGraphic(null);
                if (getText() == null) {
                    setText(getDatePicker().getValue().toString());
                }
                cancelEdit();
            }
        });
    }

    public void setValidationType(ValidationType type) {
        this.type = type;
    }

    private void startDateValidation(LocalDate date) {
        final com.thirstygoat.kiqo.model.Allocation allocation = (com.thirstygoat.kiqo.model.Allocation)getTableRow().getItem();
        // Check to make sure that start date comes before end date if end date is set
        if (allocation.getEndDate() != null && !date.isBefore(allocation.getEndDate())) {
            // Then the start date was set before the end date [the end date is set] - prohibit and alert
            GoatDialog.showAlertDialog((Stage)getTableView().getScene().getWindow(), "Error", "Error",
                    "Start date must be before end date!");
            revertDate();
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
            GoatDialog.showAlertDialog((Stage)getTableView().getScene().getWindow(), "Error", "Error",
                    "Allocation can not overlap with another allocation!");
            revertDate();
            return;
        }

        acceptChange();
    }

    private void endDateValidation(LocalDate date) {
        final com.thirstygoat.kiqo.model.Allocation allocation = (com.thirstygoat.kiqo.model.Allocation)getTableRow().getItem();
        // Check to make sure that end date comes after start date
        if (date != null && !date.isAfter(allocation.getStartDate())) {
            // Then the start date was set before the end date [the end date is set] - prohibit and alert
            GoatDialog.showAlertDialog((Stage)getTableView().getScene().getWindow(), "Error", "Error",
                    "End date must be after start date!");
            revertDate();
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
            final LocalDate bEnd = (date == null) ? LocalDate.MAX : date;
            if ((a.getStartDate().isBefore(bEnd)) && (aEnd.isAfter(allocation.getStartDate()))) {
                dateRangesOverlap = true;
                break;
            }
        }

        if (dateRangesOverlap) {
            // Then this change would make the allocation overlap with another allocation - prohibit and alert
            GoatDialog.showAlertDialog((Stage)getTableView().getScene().getWindow(), "Error", "Error",
                    "Allocation can not overlap with another allocation!");
            revertDate();
            return;
        }

        acceptChange();
    }

    private void acceptChange() {
        final boolean dateChanged = !(getItem().isEqual(getDatePicker().getValue()));
        if (dateChanged) {
            // If the date has been changed, commit the edit
            updateItem(getDatePicker().getValue(), false);
            commitEdit(getDatePicker().getValue());
        }
    }

    private void revertDate() {
        final com.thirstygoat.kiqo.model.Allocation allocation = (com.thirstygoat.kiqo.model.Allocation)getTableRow().getItem();
        if (type == ValidationType.START_DATE) {
            updateItem(allocation.getStartDate(), false);
        } else if (type == ValidationType.END_DATE) {
            updateItem(allocation.getEndDate(), false);
        }
    }

    public enum ValidationType {
        START_DATE,
        END_DATE
    }
}

package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.DeleteAllocationCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.nodes.GoatDialog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


/**
 * TableView for allocations of teams to projects
 */
public class AllocationsTableViewController implements Initializable {
    public enum  FirstColumnType {
        PROJECT, TEAM
    }

    private MainController mainController;

    @FXML
    private TableView<Allocation> allocationsTableView;
    @FXML
    private TableColumn<Allocation, String> teamTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> startDateTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> endDateTableColumn;

    public void init(FirstColumnType type) {
        if (type.equals(FirstColumnType.PROJECT)) {
            teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProject().shortNameProperty());
            teamTableColumn.setText("Project");
        } else if(type.equals(FirstColumnType.TEAM)){
            teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getTeam().shortNameProperty());
        }
        initializeTable();
    }

    private void initializeTable() {

        startDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        endDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());

        startDateTableColumn.setCellFactory(param -> {
            final AllocationDatePickerCell startDateCellFactory = new AllocationDatePickerCell();
            startDateCellFactory.setValidationType(AllocationDatePickerCell.ValidationType.START_DATE);
            return startDateCellFactory;
        });

        endDateTableColumn.setCellFactory(param -> {
            final AllocationDatePickerCell endDateCellFactory = new AllocationDatePickerCell();
            endDateCellFactory.setValidationType(AllocationDatePickerCell.ValidationType.END_DATE);
            return endDateCellFactory;
        });

        allocationsTableView.setEditable(true);

        startDateTableColumn.setOnEditCommit(event -> {
            final EditCommand<Allocation, LocalDate> command = new EditCommand<>(event.getRowValue(), "startDate", event.getNewValue());
            mainController.doCommand(command);
        });

        endDateTableColumn.setOnEditCommit(event -> {
            final EditCommand<Allocation, LocalDate> command = new EditCommand<>(event.getRowValue(), "endDate", event.getNewValue());
            mainController.doCommand(command);
        });

        // fixes ghost column issue and resizing
        allocationsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem clearEndDateMenuItem = new MenuItem("Clear End Date");
        final MenuItem deleteMenuItem = new MenuItem("Delete Allocation");
        contextMenu.getItems().addAll(clearEndDateMenuItem, deleteMenuItem);

        allocationsTableView.setRowFactory(param -> {

            final TableRow<Allocation> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    row.setContextMenu(null);
                    row.getStyleClass().removeAll("allocation-current", "allocation-past", "allocation-future");
                } else {
                    if (newValue.getEndDate() == LocalDate.MAX) {
                        clearEndDateMenuItem.setDisable(true);
                    }
                    newValue.getEndDateProperty().addListener((observable1, oldValue1, newValue1) -> {
                        if (newValue1 == LocalDate.MAX) {
                            clearEndDateMenuItem.setDisable(true);
                        } else {
                            clearEndDateMenuItem.setDisable(false);
                        }
                    });
                    row.setContextMenu(contextMenu);

                    // set background color
                    final LocalDate now = LocalDate.now();
                    row.getStyleClass().removeAll("allocation-current", "allocation-future", "allocation-past");
                    if (newValue.getStartDate().isBefore(now) && newValue.getEndDate().isAfter(now)) {
                        row.getStyleClass().add("allocation-current");
                    } else if (newValue.getStartDate().isAfter(now)) {
                        row.getStyleClass().add("allocation-future");
                    } else {
                        row.getStyleClass().add("allocation-past");
                    }
                }
            });
            return row;
        });

        clearEndDateMenuItem.setOnAction(event -> {
            final Allocation selectedAllocation = allocationsTableView.getSelectionModel().getSelectedItem();
            final LocalDate endDate = selectedAllocation.getEndDate();
            final EditCommand<Allocation, LocalDate> command = new EditCommand<>(selectedAllocation, "endDate", LocalDate.MAX);
            mainController.doCommand(command);

            boolean canChange = true;

            for (final Allocation allocation : selectedAllocation.getTeam().getAllocations()) {
                if (allocation == selectedAllocation) {
                    continue;
                }

                if (allocation.getStartDate().isAfter(selectedAllocation.getStartDate())) {
                    canChange = false;
                    break;
                }
            }

            if (!canChange) {
                // Then this change would make the allocation overlap with another allocation - prohibit and alert
                GoatDialog.showAlertDialog((Stage) allocationsTableView.getScene().getWindow(), "Error", "Error",
                        "Allocation can not overlap with another allocation!");
                selectedAllocation.setEndDate(endDate);
                return;
            }
        });

        deleteMenuItem.setOnAction(event -> {
            final Allocation selectedAllocation = allocationsTableView.getSelectionModel().getSelectedItem();

            final DeleteAllocationCommand command = new DeleteAllocationCommand(selectedAllocation);

            final String[] buttons = {"Delete Allocation", "Cancel"};
            final String result = GoatDialog.createBasicButtonDialog(mainController.getPrimaryStage(),
                    "Delete Project", "Are you sure?",
                    "Are you sure you want to delete the allocation on this project for team " +
                            selectedAllocation.getTeam().getShortName() + "?", buttons);

            if (result.equals("Delete Allocation")) {
                mainController.doCommand(command);
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setItems(ObservableList<Allocation> items) {
        allocationsTableView.setItems(items);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}

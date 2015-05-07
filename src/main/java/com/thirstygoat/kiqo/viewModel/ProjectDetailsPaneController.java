package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.command.DeleteAllocationCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.nodes.GoatDialog;

/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class ProjectDetailsPaneController implements Initializable {
    private MainController mainController;

    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TableView<Allocation> allocationsTableView;
    @FXML
    private TableColumn<Allocation, String> teamTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> startDateTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> endDateTableColumn;
    @FXML
    private Button allocateTeamButton;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeTable();
        allocateTeamButton.setOnAction(event -> mainController.allocateTeams());
    }

    private void initializeTable() {
        teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getTeam().shortNameProperty());
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

    public void showDetails(final Project project) {
        if (project != null) {
            shortNameLabel.textProperty().bind(project.shortNameProperty());
            longNameLabel.textProperty().bind(project.longNameProperty());
            descriptionLabel.textProperty().bind(project.descriptionProperty());
            allocationsTableView.setItems(project.getAllocations());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            descriptionLabel.setText(null);
            allocationsTableView.setItems(null);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

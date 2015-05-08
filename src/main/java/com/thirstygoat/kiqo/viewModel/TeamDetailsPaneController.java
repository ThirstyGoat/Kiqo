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
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.nodes.GoatDialog;
import com.thirstygoat.kiqo.util.Utilities;

public class TeamDetailsPaneController implements Initializable {

    private MainController mainController;

    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label teamMembersLabel;
    @FXML
    private Label poLabel;
    @FXML
    private Label smLabel;
    @FXML
    private Label devTeamLabel;
    @FXML
    private TableView<Allocation> allocationsTableView;
    @FXML
    private TableColumn<Allocation, String> projectTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> startDateTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> endDateTableColumn;
    @FXML
    private Button allocateTeamButton;


    public void showDetails(final Team team) {
        if (team != null) {
            shortNameLabel.textProperty().bind(team.shortNameProperty());
            descriptionLabel.textProperty().bind(team.descriptionProperty());
            teamMembersLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(team.observableTeamMembers()));

            if (team.getProductOwner() != null) {
                poLabel.textProperty().bind(team.getProductOwner().shortNameProperty());
            }
            team.productOwnerProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Then the product owner is not null, proceed
                    poLabel.textProperty().unbind();
                    poLabel.textProperty().bind(newValue.shortNameProperty());
                } else {
                    poLabel.textProperty().unbind();
                    poLabel.setText(null);
                }
            });

            if (team.getScrumMaster() != null) {
                smLabel.textProperty().bind(team.getScrumMaster().shortNameProperty());
            }
            team.scrumMasterProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Then the scrum master is not null, proceed
                    smLabel.textProperty().unbind();
                    smLabel.textProperty().bind(newValue.shortNameProperty());
                } else {
                    smLabel.textProperty().unbind();
                    smLabel.setText(null);
                }
            });

            devTeamLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(team.observableDevTeam()));

            if (team.getAllocations() != null) {
                allocationsTableView.setItems(team.observableAllocations());
            }
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            teamMembersLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        allocateTeamButton.setOnAction(event -> mainController.allocateTeams());
    }

    private void initializeTable() {
        projectTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProject().shortNameProperty());
        startDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        endDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());

        allocationsTableView.setEditable(true);

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
        final MenuItem deleteMenuItem = new MenuItem("Delete Allocation");
        final MenuItem clearEndDateMenuItem = new MenuItem("Clear End Date");
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
}

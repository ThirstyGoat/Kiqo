package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import com.thirstygoat.kiqo.command.DeleteAllocationCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.nodes.GoatDialog;


/**
 * TableView for allocations of teams to projects
 */
public class AllocationsTableViewController implements Initializable {
    private MainController mainController;
    private FirstColumnType type;
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

    public void init(FirstColumnType type) {
        this.type = type;
        allocateTeamButton.setOnAction(event -> mainController.allocateTeams());
        initializeTable();
    }

    private void initializeTable() {
        // center the dates
        startDateTableColumn.setStyle("-fx-alignment: CENTER;");
        endDateTableColumn.setStyle("-fx-alignment: CENTER;");

        // set the Title of the first column and the cell factory for the first column
        if (type.equals(FirstColumnType.PROJECT)) {
            teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProject().shortNameProperty());
            teamTableColumn.setText("Project");
            allocationsTableView.setPlaceholder(new Label("Team not allocated to any projects"));
        } else if (type.equals(FirstColumnType.TEAM)) {
            teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getTeam().shortNameProperty());
            allocationsTableView.setPlaceholder(new Label("No teams allocated to project"));
        }

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
        final MenuItem deleteMenuItem = new MenuItem("Delete Allocation");
        contextMenu.getItems().addAll(deleteMenuItem);

        allocationsTableView.setRowFactory(param -> {

            final TableRow<Allocation> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    row.setContextMenu(null);
                    row.getStyleClass().removeAll("allocation-non-existent-team", "allocation-non-existent-project", "allocation-current", "allocation-past", "allocation-future");
                    row.setTooltip(null);
                } else {
                    row.setContextMenu(contextMenu);

                    // set background color
                    row.getStyleClass().removeAll("allocation-non-existent-team", "allocation-non-existent-project", "allocation-current", "allocation-future", "allocation-past");
                    if (!mainController.selectedOrganisationProperty().get().getTeams().contains(newValue.getTeam())) {
                        row.getStyleClass().add("allocation-non-existent-team");
                        row.setTooltip(new Tooltip("This allocation belongs to a non-existent team."));
                    } else if (!mainController.selectedOrganisationProperty().get().getProjects().contains(newValue.getProject())) {
                        row.getStyleClass().add("allocation-non-existent-project");
                        row.setTooltip(new Tooltip("This allocation belongs to a non-existent project."));
                    } else if (newValue.isCurrent()) {
                        row.getStyleClass().add("allocation-current");
                        row.setTooltip(new Tooltip("This allocation is currently in progress."));
                    } else if (newValue.isFuture()) {
                        row.getStyleClass().add("allocation-future");
                        row.setTooltip(new Tooltip("This allocation is scheduled for the future."));
                    } else {
                        row.getStyleClass().add("allocation-past");
                        row.setTooltip(new Tooltip("This allocation has been and gone."));
                    }
                }
            });
            return row;
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

    private void setAllocationButtonListeners() {

        setButtonDisabled();

        mainController.selectedOrganisationProperty().getValue().getProjects().addListener((ListChangeListener<Project>) c -> {
            setButtonDisabled();
        });

        mainController.selectedOrganisationProperty().getValue().getTeams().addListener((ListChangeListener<Team>) c -> {
            setButtonDisabled();
        });
    }

    /**
     * Enables the add allocations button if there are projects and teams in the org
     * otherwise disable it
     */
    private void setButtonDisabled() {
        final boolean areProjects = mainController.selectedOrganisationProperty().getValue().getProjects().size() > 0;
        final boolean areTeams = mainController.selectedOrganisationProperty().getValue().getTeams().size() > 0;
        allocateTeamButton.setDisable(!(areProjects && areTeams));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        // need to reset the listeners if the organisation changed i.e. load from json file
        mainController.selectedOrganisationProperty().addListener((observable, oldValue, newValue) -> {
            setAllocationButtonListeners();
        });
        setAllocationButtonListeners();
    }

    public void setItems(ObservableList<Allocation> items) {
        allocationsTableView.setItems(items);
        final ChangeListener<LocalDate> listener = (observable, oldValue, newValue) -> {
            // Refresh table view since dates have changed and background colours/tooltips need to update accordingly

//            ObservableList<Allocation> tmpItems = allocationsTableView.getItems();
//            allocationsTableView.setItems(null);
//            allocationsTableView.setItems(tmpItems);
            initializeTable();
        };

        for (final Allocation allocation : items) {
            allocation.getStartDateProperty().addListener(listener);
            allocation.getEndDateProperty().addListener(listener);
        }

        mainController.selectedOrganisationProperty().get().getTeams().addListener((ListChangeListener<Team>) c -> {
            c.next();
            if (!c.getAddedSubList().isEmpty() || !c.getRemoved().isEmpty()) {
                initializeTable();
            }
        });

        mainController.selectedOrganisationProperty().get().getProjects().addListener((ListChangeListener<Project>) c -> {
            c.next();
            if (!c.getAddedSubList().isEmpty() || !c.getRemoved().isEmpty()) {
                initializeTable();
            }
        });

        items.addListener((ListChangeListener<Allocation>) c -> {
            c.next();
            for (final Allocation a : c.getAddedSubList()) {
                a.getStartDateProperty().addListener(listener);
                a.getEndDateProperty().addListener(listener);
            }
            for (final Allocation a : c.getRemoved()) {
                a.getStartDateProperty().removeListener(listener);
                a.getEndDateProperty().removeListener(listener);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public enum  FirstColumnType {
        PROJECT, TEAM
    }
}
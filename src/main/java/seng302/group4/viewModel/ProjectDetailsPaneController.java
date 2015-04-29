package seng302.group4.viewModel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import seng302.group4.Allocation;
import seng302.group4.GoatDialog;
import seng302.group4.Project;
import seng302.group4.undo.DeleteAllocationCommand;
import seng302.group4.undo.EditCommand;

/**
 * Created by Bradley on 25/03/2015.
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


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeTable();
    }

    private void initializeTable() {
        teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getTeamStringProperty());
        startDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        endDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());

        startDateTableColumn.setCellFactory(param -> new DatePickerCell<>());
        endDateTableColumn.setCellFactory(param -> new DatePickerCell<>());
        allocationsTableView.setEditable(true);

        startDateTableColumn.setOnEditCommit(event -> {
            EditCommand<Allocation, LocalDate> command = new EditCommand<>(event.getRowValue(), "startDate", event.getNewValue());
            mainController.doCommand(command);
        });

        endDateTableColumn.setOnEditCommit(event -> {
            EditCommand<Allocation, LocalDate> command = new EditCommand<>(event.getRowValue(), "endDate", event.getNewValue());
            mainController.doCommand(command);
        });

        // fixes ghost column issue and resizing
        allocationsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete Allocation");
        contextMenu.getItems().add(deleteMenuItem);
        allocationsTableView.setContextMenu(contextMenu);

        deleteMenuItem.setOnAction(event -> {
            Allocation selectedAllocation = allocationsTableView.getSelectionModel().getSelectedItem();

            DeleteAllocationCommand command = new DeleteAllocationCommand(selectedAllocation);

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
            shortNameLabel.setText(project.getShortName());
            longNameLabel.setText(project.getLongName());
            descriptionLabel.setText(project.getDescription());
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

package seng302.group4.viewModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import seng302.group4.Allocation;
import seng302.group4.GoatDialog;
import seng302.group4.Person;
import seng302.group4.Team;
import seng302.group4.undo.DeleteAllocationCommand;
import seng302.group4.undo.EditCommand;
import seng302.group4.utils.Utilities;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

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
                allocationsTableView.setItems(team.getAllocations());
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
    }

    private void initializeTable() {
        projectTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProject().shortNameProperty());
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

        allocationsTableView.setRowFactory(param -> {
            final TableRow<Allocation> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });
            return row;
        });

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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

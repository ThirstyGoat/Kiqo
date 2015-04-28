package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.group4.Allocation;
import seng302.group4.Team;
import seng302.group4.utils.Utilities;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TeamDetailsPaneController implements Initializable {
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
    private TableView<Allocation> allocationTableView = new TableView<Allocation>();
    @FXML
    private TableColumn<Allocation, String> projectTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> startDateTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> endDateTableColumn;


    public void showDetails(final Team team) {
        if (team != null) {
            shortNameLabel.setText(team.getShortName());
            descriptionLabel.setText(team.getDescription());
            teamMembersLabel.setText(Utilities.commaSeparatedValues(team.getTeamMembers()));
            if (team.getProductOwner() != null) {
                poLabel.setText(team.getProductOwner().getShortName());
            } else {
                poLabel.setText("");
            }
            if (team.getScrumMaster() != null) {
                smLabel.setText(team.getScrumMaster().getShortName());
            } else {
                smLabel.setText("");
            }
            if (team.getDevTeam() != null) {
                devTeamLabel.setText(Utilities.commaSeparatedValues(team.getDevTeam()));
            } else {
                devTeamLabel.setText("");
            }
            if (team.getAllocations() != null) {
                allocationTableView.setItems(team.getAllocations());
            }
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            teamMembersLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        projectTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProjectStringProperty());
//        startDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
//        endDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());
        projectTableColumn.setCellValueFactory(new PropertyValueFactory<Allocation, String>("projectShortName"));
        startDateTableColumn.setCellValueFactory(new PropertyValueFactory<Allocation, LocalDate>("startDate"));
        endDateTableColumn.setCellValueFactory(new PropertyValueFactory<Allocation, LocalDate>("endDate"));

        // fixes ghost column issue and resizing
        allocationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
}

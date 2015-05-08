package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
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
    private AllocationsTableViewController allocationsTableViewController;
    @FXML
    private Button allocateTeamButton;
    private boolean hasTeams = false;
    private boolean hasProject = false;


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
                allocationsTableViewController.setItems(team.observableAllocations());
            }
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            teamMembersLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.PROJECT);
        allocateTeamButton.setOnAction(event -> mainController.allocateTeams());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        allocationsTableViewController.setMainController(mainController);
        setAllocationButtonListeners();
    }

    private void setAllocationButtonListeners() {
        mainController.getSelectedOrganisation().getProjects().addListener((ListChangeListener<Project>) c -> {
            if (mainController.getSelectedOrganisation().getProjects().isEmpty()) {
                // project list is empty so disable button
                hasProject = false;
                allocateTeamButton.setDisable(true);
            } else {
                // project is not empty so check if team is not empty too
                hasProject = true;
                if (hasTeams) {
                    allocateTeamButton.setDisable(false);
                }
            }
        });

        mainController.getSelectedOrganisation().getTeams().addListener((ListChangeListener<Team>) c -> {
            if (mainController.getSelectedOrganisation().getTeams().isEmpty()) {
                // team list is empty
                allocateTeamButton.setDisable(true);
                hasTeams = false;
            } else {
                hasTeams = true;
                if (hasProject) {
                    allocateTeamButton.setDisable(false);
                }
            }
        });
    }
}

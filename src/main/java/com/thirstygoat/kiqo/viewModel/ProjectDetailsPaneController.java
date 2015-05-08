package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

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
    private Button allocateTeamButton;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;
    private boolean hasProject = false;
    private boolean hasTeams = false;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.TEAM);
        allocateTeamButton.setOnAction(event -> mainController.allocateTeams());
    }

    public void showDetails(final Project project) {
        if (project != null) {
            shortNameLabel.textProperty().bind(project.shortNameProperty());
            longNameLabel.textProperty().bind(project.longNameProperty());
            descriptionLabel.textProperty().bind(project.descriptionProperty());
            allocationsTableViewController.setItems(project.observableAllocations());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            descriptionLabel.setText(null);
            allocationsTableViewController.setItems(null);
        }
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

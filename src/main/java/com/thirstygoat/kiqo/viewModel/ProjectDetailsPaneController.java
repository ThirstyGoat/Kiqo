package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.model.Project;

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


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.PROJECT);
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
    }
}

package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.viewModel.MainController;
import com.thirstygoat.kiqo.viewModel.StoryTableViewController;
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
public class BacklogDetailsPaneController implements Initializable, IDetailsPaneController<Backlog> {
    private MainController mainController;

    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button allocateStoryButton;
    @FXML
    private StoryTableViewController storyTableViewController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    @Override
    public void showDetails(final Backlog backlog) {
        if (backlog != null) {
            shortNameLabel.textProperty().bind(backlog.shortNameProperty());
            longNameLabel.textProperty().bind(backlog.longNameProperty());
            descriptionLabel.textProperty().bind(backlog.descriptionProperty());
            storyTableViewController.setItems(backlog.observableStories());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            descriptionLabel.setText(null);
            storyTableViewController.setItems(null);
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        storyTableViewController.setMainController(mainController);
    }
}

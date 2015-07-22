package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.viewModel.MainController;
import com.thirstygoat.kiqo.viewModel.StoryTableViewController;
import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.viewModel.StoryTableViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneController implements Initializable, IDetailsPaneController<Backlog> {
    private MainController mainController;
    private StoryTableViewModel storyTableViewModel = new StoryTableViewModel();

    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label productOwnerLabel;
    @FXML
    private Label scaleLabel;
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
            productOwnerLabel.textProperty().bind(Bindings.select(backlog.productOwnerProperty(), "shortName"));
            storyTableViewModel.setStories(backlog.observableStories());
            storyTableViewController.setViewModel(storyTableViewModel);
            scaleLabel.textProperty().bind(backlog.scaleProperty().asString());

        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            descriptionLabel.setText(null);
            productOwnerLabel.setText(null);
            storyTableViewModel.setStories(null);
            scaleLabel.textProperty().unbind();
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

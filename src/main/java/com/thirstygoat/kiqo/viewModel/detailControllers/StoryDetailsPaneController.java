package com.thirstygoat.kiqo.viewModel.detailControllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.model.Story;

public class StoryDetailsPaneController implements Initializable, IDetailsPaneController<Story> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label creatorLabel;
    @FXML
    private Label priorityLabel;


    public void showDetails(final Story story) {
        if (story != null) {
            longNameLabel.textProperty().bind(story.longNameProperty());
            shortNameLabel.textProperty().bind(story.shortNameProperty());
            descriptionLabel.textProperty().bind(story.descriptionProperty());
            creatorLabel.textProperty().bind(Bindings.select(story.creatorProperty(), "shortName"));
            priorityLabel.textProperty().bind(Bindings.convert(story.priorityProperty()));
        } else {
            longNameLabel.textProperty().unbind();
            shortNameLabel.textProperty().unbind();
            descriptionLabel.textProperty().unbind();
            creatorLabel.textProperty().unbind();
            priorityLabel.textProperty().unbind();

            longNameLabel.setText("");
            shortNameLabel.setText("");
            descriptionLabel.setText("");
            creatorLabel.setText("");
            priorityLabel.setText("");
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        // we don't need the main controller for now
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

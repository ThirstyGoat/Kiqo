package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.model.Story;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StoryDetailsPaneController implements Initializable {
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
            // TODO creatorLabel.textProperty().bind(story.creatorProperty().get());
            priorityLabel.textProperty().bind(Bindings.convert(story.priorityProperty()));
        } else {
            longNameLabel.textProperty().unbind();
            shortNameLabel.textProperty().unbind();
            descriptionLabel.textProperty().unbind();
            creatorLabel.textProperty().unbind();
            priorityLabel.textProperty().unbind();

            longNameLabel.setText(null);
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            creatorLabel.setText(null);
            priorityLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

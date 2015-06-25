package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Story;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

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
    @FXML
    private ListView<String> acListView;
    @FXML
    private Button addACButton;
    @FXML
    private Button removeACButton;


    public void showDetails(final Story story) {
        if (story != null) {
            longNameLabel.textProperty().bind(story.longNameProperty());
            shortNameLabel.textProperty().bind(story.shortNameProperty());
            descriptionLabel.textProperty().bind(story.descriptionProperty());
            // This is some seriously cool binding
            // Binding to a property of a property
            creatorLabel.textProperty().bind(Bindings.select(story.creatorProperty(), "shortName"));
            priorityLabel.textProperty().bind(Bindings.convert(story.priorityProperty()));

            AcceptanceCriteria ac = new AcceptanceCriteria();
            acListView.setItems(ac.criteria);
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
    public void setMainController(MainController mainController) {
        // we don't need the main controller for now
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

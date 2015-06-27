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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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
    private ListView<TextArea> acListView;
    @FXML
    private Button addACButton;
    @FXML
    private Button removeACButton;
    @FXML
    private Button editACButton;


    public void showDetails(final Story story) {
        if (story != null) {
            longNameLabel.textProperty().bind(story.longNameProperty());
            shortNameLabel.textProperty().bind(story.shortNameProperty());
            descriptionLabel.textProperty().bind(story.descriptionProperty());
            // This is some seriously cool binding
            // Binding to a property of a property
            creatorLabel.textProperty().bind(Bindings.select(story.creatorProperty(), "shortName"));
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

        addACButton.setOnAction(event -> addAC());
        removeACButton.setOnAction(event -> deleteAC());
        editACButton.setOnAction(event -> editAC());
    }

    private void addAC() {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Acceptance Criteria");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(1);
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                textArea.setPrefRowCount(textArea.getText().split("\n").length);
            }
        });

        textArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    textArea.setEditable(false);
                } else {
                    acListView.getSelectionModel().select(textArea);
                }
            }
        });
        acListView.getItems().add(textArea);
        //todo: work out why it only works for the 2nd item onwards
        textArea.requestFocus();
    }

    private void deleteAC() {
        if (acListView.getSelectionModel().getSelectedItem() != null) {
            acListView.getItems().remove(acListView.getSelectionModel().getSelectedItem());
        }
    }

    private void editAC() {
        if (acListView.getSelectionModel().getSelectedItem() != null) {
            acListView.getSelectionModel().getSelectedItem().setEditable(true);
            acListView.getSelectionModel().getSelectedItem().requestFocus();
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

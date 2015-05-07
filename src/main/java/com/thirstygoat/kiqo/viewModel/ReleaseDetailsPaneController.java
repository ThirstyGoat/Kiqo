package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.model.Release;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ReleaseDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label projectLabel;
    @FXML
    private Label releaseDateLabel;
    @FXML
    private Label descriptionLabel;


    public void showDetails(final Release release) {
        if (release != null) {
            shortNameLabel.textProperty().bind(release.shortNameProperty());

            projectLabel.textProperty().bind(release.getProject().shortNameProperty());
            // Add listener on project
            release.projectProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Then the product owner is not null, proceed
                    projectLabel.textProperty().unbind();
                    projectLabel.textProperty().bind(newValue.shortNameProperty());
                } else {
                    projectLabel.textProperty().unbind();
                    projectLabel.setText(null);
                }
            });

            releaseDateLabel.setText(release.getDate().toString());
            // Add listener on date
            release.dateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Then the product owner is not null, proceed
                    releaseDateLabel.textProperty().unbind();
                    releaseDateLabel.setText(newValue.toString());
                } else {
                    releaseDateLabel.textProperty().unbind();
                    releaseDateLabel.setText(null);
                }
            });

            descriptionLabel.textProperty().bind(release.descriptionProperty());
        } else {
            shortNameLabel.setText(null);
            projectLabel.setText(null);
            releaseDateLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
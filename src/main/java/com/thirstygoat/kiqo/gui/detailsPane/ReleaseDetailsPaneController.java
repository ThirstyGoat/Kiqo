package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatDatePicker;
import com.thirstygoat.kiqo.model.Release;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class ReleaseDetailsPaneController implements Initializable, IDetailsPaneController<Release> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label projectLabel;
    @FXML
    private GoatDatePicker releaseDateLabel;
    @FXML
    private Label descriptionLabel;


    @Override
    public void showDetails(final Release release) {
        DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

            releaseDateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
                return release.dateProperty().get().format(datetimeFormat);
            }, release.dateProperty()));
            releaseDateLabel.setItem(release, "date", release.dateProperty());

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

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }
}
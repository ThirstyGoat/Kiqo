package com.thirstygoat.kiqo.viewModel.detailsPane;

import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class SkillDetailsPaneController implements Initializable, IDetailsPaneController<Skill> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;


    @Override
    public void showDetails(final Skill skill) {
        if (skill != null) {
            shortNameLabel.textProperty().bind(skill.shortNameProperty());
            descriptionLabel.textProperty().bind(skill.descriptionProperty());
        } else {
            shortNameLabel.setText(null);
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

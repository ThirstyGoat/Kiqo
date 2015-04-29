package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Skill;

public class SkillDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;


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
}

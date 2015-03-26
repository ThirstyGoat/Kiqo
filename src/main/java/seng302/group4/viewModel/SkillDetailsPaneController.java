package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import seng302.group4.Project;
import seng302.group4.Skill;

import java.net.URL;
import java.util.ResourceBundle;

public class SkillDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;


    public void showDetails(final Skill skill) {
        if (skill != null) {
            shortNameLabel.setText(skill.getShortName());
            descriptionLabel.setText(skill.getDescription());
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

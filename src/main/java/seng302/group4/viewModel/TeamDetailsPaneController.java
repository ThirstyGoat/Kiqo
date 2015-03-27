package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Person;
import seng302.group4.Skill;
import seng302.group4.Team;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label teamMembersLabel;


    public void showDetails(final Team team) {
        if (team != null) {
            shortNameLabel.setText(team.getShortName());
            descriptionLabel.setText(team.getDescription());
            String teamMembersString = "";
            for(Person p : team.getTeamMembers()) {
                teamMembersString += p.getShortName();
                teamMembersString += "\n";
            }
            teamMembersLabel.setText(teamMembersString);
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            teamMembersLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

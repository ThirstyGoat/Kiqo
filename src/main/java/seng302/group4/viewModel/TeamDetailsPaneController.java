package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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

            for( int i = 0; i < team.getTeamMembers().size()-1; i++) {
                teamMembersString += team.getTeamMembers().get(i).getShortName();
                teamMembersString += ", ";
            }
            teamMembersString += team.getTeamMembers().get(team.getTeamMembers().size()-1).getShortName();
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

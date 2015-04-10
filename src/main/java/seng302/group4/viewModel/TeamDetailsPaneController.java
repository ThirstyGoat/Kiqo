package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Person;
import seng302.group4.Team;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TeamDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label teamMembersLabel;
    @FXML
    private Label poLabel;
    @FXML
    private Label smLabel;
    @FXML
    private Label devTeamLabel;


    public void showDetails(final Team team) {
        if (team != null) {
            shortNameLabel.setText(team.getShortName());
            descriptionLabel.setText(team.getDescription());
            teamMembersLabel.setText(commaSeparatedValues(team.getTeamMembers()));
            if (team.getProductOwner() != null) {
                poLabel.setText(team.getProductOwner().getShortName());
            } else {
                poLabel.setText("");
            }
            if (team.getScrumMaster() != null) {
                smLabel.setText(team.getScrumMaster().getShortName());
            } else {
                smLabel.setText("");
            }
            if (team.getDevTeam() != null) {
                devTeamLabel.setText(commaSeparatedValues(team.getDevTeam()));
            } else {
                devTeamLabel.setText("");
            }
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            teamMembersLabel.setText(null);
        }
    }

    private String commaSeparatedValues(ArrayList<Person> list) {
        String concatenatedString = "";

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                concatenatedString += list.get(i).getShortName();
                if (i != list.size() - 1) {
                    concatenatedString += ", ";
                }
            }
        }

        return concatenatedString;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
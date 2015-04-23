package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Allocation;
import seng302.group4.Team;
import seng302.group4.utils.Utilities;

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
    @FXML
    private Label allocationLabel;

    public static String newlineSeparatedValues(ArrayList<Allocation> list) {
        StringBuffer sb = new StringBuffer();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).getProject().getShortName());
                sb.append(", " + list.get(i).getStartDate());
                if (list.get(i).getEndDate() != null) {
                    sb.append("-" + list.get(i).getEndDate() + "\n");
                } else {
                    sb.append("-\n");
                }
            }
        }
        return sb.toString();
    }

    public void showDetails(final Team team) {
        if (team != null) {
            shortNameLabel.setText(team.getShortName());
            descriptionLabel.setText(team.getDescription());
            teamMembersLabel.setText(Utilities.commaSeparatedValues(team.getTeamMembers()));
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
                devTeamLabel.setText(Utilities.commaSeparatedValues(team.getDevTeam()));
            } else {
                devTeamLabel.setText("");
            }
            if (team.getAllocations() != null) {
                allocationLabel.setText(newlineSeparatedValues(team.getAllocations()));
            } else {
                allocationLabel.setText("");
            }
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

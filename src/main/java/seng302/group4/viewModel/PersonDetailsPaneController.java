package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Person;
import seng302.group4.Skill;

/**
 * Created by Carina on 25/03/2015.
 */
public class PersonDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label userIDLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label skillsLabel;
    @FXML
    private Label descriptionLabel;
    private Object skillsString;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public void showDetails(final Person person) {
        if (person != null) {
            shortNameLabel.setText(person.getShortName());
            longNameLabel.setText(person.getLongName());
            userIDLabel.setText(person.getUserID());
            emailLabel.setText(person.getEmailAddress());
            phoneLabel.setText(person.getPhoneNumber());
            departmentLabel.setText(person.getDepartment());
            skillsLabel.setText(formatSkillsString(person.getSkills()));
            descriptionLabel.setText(person.getDescription());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            userIDLabel.setText(null);
            emailLabel.setText(null);
            phoneLabel.setText(null);
            departmentLabel.setText(null);
            skillsLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    public String formatSkillsString(ArrayList<Skill> skills) {
        String toReturn = "";

        for (int i = 0; i < skills.size(); i++) {
            toReturn += skills.get(i).getShortName();
            if (i != skills.size() - 1) {
                toReturn += ", ";
            }
        }
        return toReturn;
    }
}

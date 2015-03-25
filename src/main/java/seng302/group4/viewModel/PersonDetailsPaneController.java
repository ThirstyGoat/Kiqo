package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import seng302.group4.Person;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Carina on 25/03/2015.
 */
public class PersonDetailsPaneController implements DetailsPaneController<Person> {
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
    private Label descriptionLabel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    @Override
    public void showDetails(final Person person) {
        if (person != null) {
            shortNameLabel.setText(person.getShortName());
            longNameLabel.setText(person.getLongName());
            userIDLabel.setText(person.getUserID());
            emailLabel.setText(person.getEmailAddress());
            phoneLabel.setText(person.getPhoneNumber());
            departmentLabel.setText(person.getDepartment());
            descriptionLabel.setText(person.getDescription());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            userIDLabel.setText(null);
            emailLabel.setText(null);
            phoneLabel.setText(null);
            departmentLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }
}

package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.Utilities;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public void showDetails(final Person person) {
        if (person != null) {
            shortNameLabel.textProperty().bind(person.shortNameProperty());
            longNameLabel.textProperty().bind(person.longNameProperty());
            userIDLabel.textProperty().bind(person.userIDProperty());
            emailLabel.textProperty().bind(person.emailAddressProperty());
            phoneLabel.textProperty().bind(person.phoneNumberProperty());
            departmentLabel.textProperty().bind(person.departmentProperty());
            descriptionLabel.textProperty().bind(person.descriptionProperty());
            skillsLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(person.observableSkills()));
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
}

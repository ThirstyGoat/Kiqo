package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Created by Carina on 25/03/2015.
 */
public class PersonDetailsPaneController implements Initializable, IDetailsPaneController<Person> {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private GoatLabelTextField userIdLabel;
    @FXML
    private GoatLabelTextField emailLabel;
    @FXML
    private GoatLabelTextField phoneLabel;
    @FXML
    private GoatLabelTextField departmentLabel;
    @FXML
    private Label skillsLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        emailLabel.managedProperty().bind(emailLabel.textProperty().isNotEmpty());
    }

    @Override
    public void showDetails(final Person person) {
        if (person != null) {
            shortNameLabel.textProperty().bind(person.shortNameProperty());
//            shortNameLabel.setItem(person, "shortName", person.shortNameProperty());

            longNameLabel.textProperty().bind(person.longNameProperty());
//            longNameLabel.setItem(person, "longName", person.longNameProperty());

            userIdLabel.textProperty().bind(person.userIdProperty());
//            userIdLabel.setItem(person, "userId", person.userIdProperty());

            emailLabel.textProperty().bind(person.emailAddressProperty());
//            emailLabel.setItem(person, "emailAddress", person.emailAddressProperty());

            phoneLabel.textProperty().bind(person.phoneNumberProperty());
//            phoneLabel.setItem(person, "phoneNumber", person.phoneNumberProperty());

            departmentLabel.textProperty().bind(person.departmentProperty());
//            departmentLabel.setItem(person, "department", person.departmentProperty());

            descriptionLabel.textProperty().bind(person.descriptionProperty());
//            descriptionLabel.setItem(person, "description", person.descriptionProperty());

            skillsLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(person.observableSkills()));

            final ValidationSupport validationSupport = new ValidationSupport();
//            final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
//                    Utilities.shortnameIsUnique(shortNameLabel.getEditField().getText(), person, organisation.getPeople());
            final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                    Utilities.shortnameIsUnique(shortNameLabel.getEditField().getText(), person, new ArrayList<>());
            //Todo prevent the user from clicking the done button
            validationSupport.registerValidator(shortNameLabel.getEditField(), Validator.createPredicateValidator(shortNameValidation,
                    "Short name must be unique and not empty."));

            validationSupport.registerValidator(longNameLabel.getEditField(),
                    Validator.createEmptyValidator("Name must not be empty", Severity.ERROR));
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            userIdLabel.setText(null);
            emailLabel.setText(null);
            phoneLabel.setText(null);
            departmentLabel.setText(null);
            skillsLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }
}

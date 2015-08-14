package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        emailLabel.managedProperty().bind(emailLabel.displayTextProperty().isNotEmpty());
    }

    @Override
    public void showDetails(final Person person) {
        if (person != null) {
            shortNameLabel.displayTextProperty().bind(person.shortNameProperty());
            shortNameLabel.getEditField().textProperty().addListener((observable, oldValue, newValue) -> {
                shortNameLabel.commandProperty().setValue(new EditCommand(person, "shortName", newValue));
            });
//            shortNameLabel.setItem(person, "shortName", person.shortNameProperty());

            longNameLabel.displayTextProperty().bind(person.longNameProperty());
//            longNameLabel.setItem(person, "longName", person.longNameProperty());

            userIdLabel.displayTextProperty().bind(person.userIdProperty());
//            userIdLabel.setItem(person, "userId", person.userIdProperty());

            emailLabel.displayTextProperty().bind(person.emailAddressProperty());
//            emailLabel.setItem(person, "emailAddress", person.emailAddressProperty());

            phoneLabel.displayTextProperty().bind(person.phoneNumberProperty());
//            phoneLabel.setItem(person, "phoneNumber", person.phoneNumberProperty());

            departmentLabel.displayTextProperty().bind(person.departmentProperty());
//            departmentLabel.displayTextProperty(person, "department", person.departmentProperty());

            descriptionLabel.displayTextProperty().bind(person.descriptionProperty());
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
            shortNameLabel.displayTextProperty().setValue("");
            longNameLabel.displayTextProperty().setValue("");
            userIdLabel.displayTextProperty().setValue("");
            emailLabel.displayTextProperty().setValue("");
            phoneLabel.displayTextProperty().setValue("");
            departmentLabel.displayTextProperty().setValue("");
            skillsLabel.setText(null);
            descriptionLabel.displayTextProperty().setValue("");
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }
}

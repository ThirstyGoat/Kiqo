package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabel;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SkillDetailsPaneController implements Initializable, IDetailsPaneController<Skill> {
    @FXML
    private GoatLabel shortNameLabel;
    @FXML
    private GoatLabel descriptionLabel;

    private final ValidationSupport validationSupport = new ValidationSupport();
    private Organisation organisation;


    @Override
    public void showDetails(final Skill skill) {

        if (skill != null) {

            shortNameLabel.textProperty().bind(skill.shortNameProperty());
            shortNameLabel.setItem(skill, "shortName", skill.shortNameProperty());

            final Predicate<String> shortNameValidation = s -> s.length() != 0 &&
                    Utilities.shortnameIsUnique(shortNameLabel.getEditField().getText(), skill, organisation.getSkills());

            validationSupport.registerValidator(shortNameLabel.getEditField(), Validator.createPredicateValidator(shortNameValidation,
                    "Short name must be unique and not empty."));

            validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // Then invalid, disable ok button
                    shortNameLabel.doneButton().setDisable(true);
                } else {
                    shortNameLabel.doneButton().setDisable(false);
                }
            });

            descriptionLabel.setItem(skill, "description", skill.descriptionProperty());
            descriptionLabel.textProperty().bind(skill.descriptionProperty());


        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    public void setOrganisation(Organisation organisaion) {
        this.organisation = organisaion;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }

}

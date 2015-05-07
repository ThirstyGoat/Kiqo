package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.command.CreateSkillCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by james on 18/03/15.
 */
public class NewSkillController implements Initializable {
    private Stage stage;
    private boolean valid = false;
    private Organisation organisation;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    @FXML
    private SkillFormController formController;

    private CreateSkillCommand command;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAddButton();
        setCancelButton();

        Platform.runLater(() -> setProjectForFormController());
    }

    /**
     * Sets the cancel button for new Person dialog
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            formController.errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    /**
     * Sets the New Person button for new Person dialog
     */
    private void setAddButton() {
        addButton.setOnAction(event -> {
            // check to see that shortname
            formController.validate();
            if (formController.isValid()) {
                for (Skill s : organisation.getSkills()) {
                    if (formController.getShortName().equals(s.getShortName())) {
                        formController.warnShortnameNotUnique();
                        return;
                    }
                }
                Skill skill = new Skill(formController.getShortName(), formController.getDescription());
                command = new CreateSkillCommand(skill, organisation);

                valid = true;
                stage.close();
            }
        });
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public CreateSkillCommand getCommand() {
        return command;
    }

    private void setProjectForFormController() {
        formController.setOrganisation(organisation);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @return validity of all fields
     */
    public boolean isValid() {
        return valid;
    }
}
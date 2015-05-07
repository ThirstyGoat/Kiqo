package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.command.CreateProjectCommand;
import com.thirstygoat.kiqo.model.Organisation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class NewProjectController implements Initializable {
    private Stage stage;
    private boolean valid = false;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button newProjectButton;
    @FXML
    private ProjectFormController formController;

    private CreateProjectCommand command;
    private Organisation organisation;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setCancelButton();
        setNewButton();
        formController.setShortNameSuggester();
    }

    /**
     * Sets the event handler for the New Project Button, performs validation
     * checks and instantiates the new project if applicable
     */
    private void setNewButton() {
        newProjectButton.setOnAction(event -> {
            formController.validate();
            if (formController.isValid()) {
                command = new CreateProjectCommand(formController.shortName, formController.longName, formController.description, organisation);
                valid = true;
                // Close the new project dialog (this window)
                stage.close();
            }
        });
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
        formController.setStage(stage);
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    /**
     * Sets the cancel button functionality
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            formController.errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    /**
     * @return the formController
     */
    public ProjectFormController getFormController() {
        return formController;
    }

    /**
     * @return the command
     */
    public CreateProjectCommand getCommand() {
        return command;
    }

    /**
     * @return validity of all fields
     */
    public boolean isValid() {
        return valid;
    }
}

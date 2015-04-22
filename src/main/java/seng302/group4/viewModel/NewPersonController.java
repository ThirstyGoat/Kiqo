package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng302.group4.Person;
import seng302.group4.Organisation;
import seng302.group4.undo.CreatePersonCommand;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by james on 18/03/15.
 */
public class NewPersonController implements Initializable {
    private Stage stage;
    private boolean valid = false;
    private Organisation organisation;
    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button newPersonButton;
    @FXML
    private PersonFormController formController;

    private CreatePersonCommand command;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNewPersonButton();
        setCancelButton();
        formController.setShortNameSuggester();

        Platform.runLater(this::setProjectForFormController);
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
    private void setNewPersonButton() {
        newPersonButton.setOnAction(event -> {
            // check to see that shortname and longname are populated and shortname is unique within the project
            formController.validate();
            if (formController.isValid()) {
                for (Person p : organisation.getPeople()) {
                    if (formController.getShortName().equals(p.getShortName())) {
                        formController.warnShortnameNotUnique();
                        return;
                    }
                }
                Person person = new Person(formController.getShortName(), formController.getLongName(),
                        formController.getDescription(), formController.getUserID(), formController.getEmailAddress(),
                        formController.getPhoneNumber(), formController.getDepartment(), formController.getSkills());
                command = new CreatePersonCommand(person, organisation);
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

    public CreatePersonCommand getCommand() {
        return command;
    }

    public void setProjectForFormController() {
        formController.setOrganisation(organisation);
        formController.setUpSkillsListSelectionView();
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
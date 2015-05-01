package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng302.group4.Organisation;
import seng302.group4.Person;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.EditCommand;

/**
 * Created by James on 18/03/15.
 */
public class EditPersonController implements Initializable {
    private Stage stage;
    private Organisation organisation;
    private Person person;
    private boolean valid = false;
    private CompoundCommand command;
    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button editPersonButton;
    @FXML
    private PersonFormController formController;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setCancelButton();
        setSaveButton();
    }


    /**
     * Populates the fields with project data to enable editing
     *
     * @param person source of existing field data
     */
    public void loadPerson(final Person person) {
        this.person = person;
        formController.loadPerson(person);
    }

    /**
     * Sets the event handler for the Save button, performs validation checks
     * and instantiates the new project if applicable
     */
    private void setSaveButton() {
        editPersonButton.setOnAction(event -> {
            // check to see that shortname and longname fields are populated and shortname is unique within the project
            formController.validate();
            if (formController.isValid()) {

                final ArrayList<Command<?>> changes = new ArrayList<>();

                if (!formController.getShortName().equals(person.getShortName())) {
                    for (final Person p : organisation.getPeople()) {
                        if (formController.getShortName().equals(p.getShortName())) {
                            formController.warnShortnameNotUnique();
                            return;
                        }
                    }
                    changes.add(new EditCommand<>(person, "shortName", formController.getShortName()));
                }
                if (!formController.getLongName().equals(person.getLongName())) {
                    changes.add(new EditCommand<>(person, "longName", formController.getLongName()));
                }
                if (!formController.getDescription().equals(person.getDescription())) {
                    changes.add(new EditCommand<>(person, "description", formController.getDescription()));
                }
                if (!formController.getUserID().equals(person.getUserID())) {
                    changes.add(new EditCommand<>(person, "userID", formController.getUserID()));
                }
                if (!formController.getEmailAddress().equals(person.getEmailAddress())) {
                    changes.add(new EditCommand<>(person, "emailAddress", formController.getEmailAddress()));
                }
                if (!formController.getPhoneNumber().equals(person.getPhoneNumber())) {
                    changes.add(new EditCommand<>(person, "phoneNumber", formController.getPhoneNumber()));
                }
                if (!formController.getDepartment().equals(person.getDepartment())) {
                    changes.add(new EditCommand<>(person, "department", formController.getDepartment()));
                }
                if (!formController.getSkills().equals(person.getSkills())) {
                    changes.add(new EditCommand<>(person, "skills", formController.getSkills()));
                }

                valid = !changes.isEmpty();

                    command = new CompoundCommand("Edit Person", changes);
                // Close the new project dialog (this window)
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

    public CompoundCommand getCommand() {
        return command;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            formController.errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    public void setProjectForFormController() {
        formController.setOrganisation(organisation);
    }
}

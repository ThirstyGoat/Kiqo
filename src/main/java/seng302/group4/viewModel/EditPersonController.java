package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.EditCommand;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by James on 18/03/15.
 */
public class EditPersonController implements Initializable {
    private Stage stage;
    private Project project;
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
        this.setCancelButton();
        this.setSaveButton();

        Platform.runLater(() -> setProjectForFormController());
    }


    /**
     * Populates the fields with project data to enable editing
     *
     * @param person
     */
    public void loadPerson(final Person person) {
        this.person = person;
        this.formController.loadPerson(person);
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
                    for (Person p : project.getPeople()) {
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

                valid = !changes.isEmpty();

                command = new CompoundCommand(changes);
                // Close the new project dialog (this window)
                this.stage.close();
            }
        });
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public CompoundCommand getCommand() {
        return command;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return this.valid;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    private void setCancelButton() {
        this.cancelButton.setOnAction(event -> this.stage.close());
    }

    private void setProjectForFormController() {
        formController.setProject(project);
    }
}

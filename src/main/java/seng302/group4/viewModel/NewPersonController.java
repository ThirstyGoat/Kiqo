package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.undo.CreatePersonCommand;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by james on 18/03/15.
 */
public class NewPersonController implements Initializable {
    private Stage stage;
    private Person person;
    private boolean valid = false;
    private Project project;
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

        Platform.runLater(() -> setProjectForFormController());
    }

    /**
     * Sets the cancel button for new Person dialog
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
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
                for (Person p : project.getPeople()) {
                    if (formController.getShortName().equals(p.getShortName())) {
                        formController.warnShortnameNotUnique();
                        return;
                    }
                }
                command = new CreatePersonCommand(formController.getShortName(), formController.getLongName(),
                        formController.getDescription(), formController.getUserID(), formController.getEmailAddress(),
                        formController.getPhoneNumber(), formController.getDepartment(), formController.getSkills());
                valid = true;
                stage.close();
            }
        });
    }

    /**
     * Returns the Person object created by the dialog box
     * @return the Person created by the dialog box
     */
    public Person getPerson() {
        return person;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public CreatePersonCommand getCommand() {
        return command;
    }

    private void setProjectForFormController() {
        formController.setProject(project);
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

package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import seng302.group4.Person;
import seng302.group4.Project;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by james on 18/03/15.
 */
public class NewPersonController implements Initializable {
    private Stage stage;
    private Person person;

    private Project project;
    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button newPersonButton;
    @FXML
    private PersonFormController formController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNewPersonButton();
        setCancelButton();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setProject();
            }
        });
    }

    private void setProject() {
        formController.setProject(project);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the cancel button for new Person dialog
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            stage.close();
        });
    }

    //String userID, String emailAddress,
//    String phoneNumber, String department
    /**
     * Sets the New Person button for new Person dialog
     */
    private void setNewPersonButton() {
        newPersonButton.setOnAction(event -> {
//            setProject();
            formController.validate();
            if (formController.isValid()) {
                person = new Person(formController.shortName, formController.longName, formController.description,
                        formController.userID, formController.emailAddress, formController.phoneNumber,
                        formController.department);
                stage.close();
            }
        });
    }
    
    /**
     * Returns the Person object created by the dialog box
     * @return the Person created by the dialog box
     */
    Person getPerson() {
        return person;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}

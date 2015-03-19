package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.group4.Person;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by james on 18/03/15.
 */
public class NewPersonController implements Initializable {
    private Stage stage;

    // FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField userIDTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField departmentTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button newPersonButton;

    private Person person;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNewPersonButton();
        setCancelButton();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            stage.close();
        });
    }

    private void setNewPersonButton() {
        newPersonButton.setOnAction(event -> {
            //TODO add person to the model
            person = createPerson();

            System.out.println("From inside NPC " + person);
            stage.close();
        });
    }

    private Person createPerson() {
        String description = null;
        String userID = null;
        String emailAddress = null;
        Integer phoneNumber = null;
        String department = null;
        if (!descriptionTextField.getText().equals("")) {
            description = descriptionTextField.getText();
        }
        if (!userIDTextField.getText().equals("")) {
            userID = userIDTextField.getText();
        }
        if (!emailTextField.getText().equals("")) {
            emailAddress = emailTextField.getText();
        }
        if (!phoneTextField.getText().equals("")) {
            phoneNumber = Integer.parseInt(phoneTextField.getText());
        }
        if (!departmentTextField.getText().equals("")) {
            department = departmentTextField.getText();
        }
        return new Person(shortNameTextField.getText(), longNameTextField.getText(), description, userID, emailAddress,
                phoneNumber, department);
    }

    Person getPerson() {
        return person;
    }

}

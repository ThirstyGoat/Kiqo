package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import seng302.group4.Person;
import seng302.group4.Project;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailsPaneController implements Initializable {
    @FXML
    private AnchorPane detailsPane;
    @FXML
    private GridPane projectDetailsPane;
    @FXML
    private GridPane personDetailsPane;
    @FXML
    private Button editButton;
    @FXML
    private ProjectDetailsPaneController projectDetailsPaneController;
    @FXML
    private PersonDetailsPaneController personDetailsPaneController;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clear();
    }


    public void showDetailsPane(Object objectForDisplay) {
        if (objectForDisplay == null) {
            clear();
        } else {
            System.out.println(objectForDisplay.getClass());
            if (objectForDisplay instanceof Project) {
                showProjectDetailsPane((Project)objectForDisplay);
            } else if (objectForDisplay instanceof Person) {
                showPersonDetailsPane((Person)objectForDisplay);
            }
        }
    }

    private void clear() {
        projectDetailsPane.setVisible(false);
//        projectDetailsPane.setManaged(false);
        personDetailsPane.setVisible(false);
//        personDetailsPane.setManaged(false);
    }

    private void showProjectDetailsPane(Project project) {
        System.out.println("ShowProjectDetails");
        projectDetailsPaneController.showDetails(project);
        personDetailsPane.setVisible(false);
//        personDetailsPane.setManaged(false);
        projectDetailsPane.setVisible(true);
//        projectDetailsPane.setManaged(true);

        editButton.setOnAction(event -> mainController.editProject());
    }

    private void showPersonDetailsPane(Person person) {
        System.out.println("showPersonDetails");
        personDetailsPaneController.showDetails(person);
        projectDetailsPane.setVisible(false);
//        projectDetailsPane.setManaged(false);
        personDetailsPane.setVisible(true);
//        personDetailsPane.setManaged(true);

        editButton.setOnAction(event -> mainController.editPerson());
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

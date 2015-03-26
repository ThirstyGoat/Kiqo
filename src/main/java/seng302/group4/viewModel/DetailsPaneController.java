package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import seng302.group4.Person;
import seng302.group4.Project;

/**
 * Switches between detail panes depending on type of content shown.
 *
 * @see http://stackoverflow.com/a/16179194
 * @author amy
 *
 */
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
            if (objectForDisplay instanceof Project) {
                showProjectDetailsPane((Project)objectForDisplay);
            } else if (objectForDisplay instanceof Person) {
                showPersonDetailsPane((Person)objectForDisplay);
            }
        }
    }

    private void clear() {
        detailsPane.getChildren().clear();
    }

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneController.showDetails(project);
        detailsPane.getChildren().setAll(projectDetailsPane);
        editButton.setOnAction(event -> mainController.editProject());
    }

    private void showPersonDetailsPane(Person person) {
        personDetailsPaneController.showDetails(person);
        detailsPane.getChildren().setAll(personDetailsPane);
        editButton.setOnAction(event -> mainController.editPerson());
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

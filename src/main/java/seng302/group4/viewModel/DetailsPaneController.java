package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;

/**
 * Switches between detail panes depending on type of content shown.
 *
 * @see http://stackoverflow.com/a/16179194
 * @author amy
 *
 */
public class DetailsPaneController implements Initializable {
    @FXML
    private BorderPane detailsPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private GridPane projectDetailsPane;
    @FXML
    private GridPane personDetailsPane;
    @FXML
    private GridPane skillDetailsPane;
    @FXML
    private Button editButton;
    @FXML
    private ProjectDetailsPaneController projectDetailsPaneController;
    @FXML
    private PersonDetailsPaneController personDetailsPaneController;
    @FXML
    private SkillDetailsPaneController skillDetailsPaneController;

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
            } else if (objectForDisplay instanceof Skill) {
                showSkillDetailPane((Skill)objectForDisplay);
            }
        }
    }

    private void clear() {
        stackPane.getChildren().clear();
        detailsPane.getChildren().remove(editButton);
    }

    private void showSkillDetailPane(Skill skill) {
        skillDetailsPaneController.showDetails(skill);

        final ObservableList<Node> children = detailsPane.getChildren();
        if (!children.contains(skillDetailsPane)) {
            children.add(skillDetailsPane);
        }
        children.remove(projectDetailsPane);
        children.remove(personDetailsPane);

        addEditButton();
        editButton.setOnAction(event -> mainController.editSkill());
    }

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneController.showDetails(project);

        final ObservableList<Node> children = detailsPane.getChildren();
        if (!children.contains(projectDetailsPane)) {
            children.add(projectDetailsPane);
        }
        children.remove(personDetailsPane);
        children.remove(skillDetailsPane);

        addEditButton();
        editButton.setOnAction(event -> mainController.editProject());
    }

    private void showPersonDetailsPane(Person person) {
        personDetailsPaneController.showDetails(person);

        final ObservableList<Node> children = detailsPane.getChildren();
        if (!children.contains(personDetailsPane)) {
            children.add(personDetailsPane);
        }
        children.remove(projectDetailsPane);
        children.remove(skillDetailsPane);

        addEditButton();
        editButton.setOnAction(event -> mainController.editPerson());
    }

    private void addEditButton() {
        final ObservableList<Node> children = detailsPane.getChildren();
        if (!children.contains(editButton)) {
            detailsPane.setBottom(editButton);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

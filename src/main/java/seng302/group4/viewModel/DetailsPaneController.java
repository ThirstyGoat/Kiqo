package seng302.group4.viewModel;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;
import seng302.group4.Team;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Switches between detail panes depending on type of content shown.
 *
 * @author amy
 *
 */
public class DetailsPaneController implements Initializable {
    @FXML
    private BorderPane detailsPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane projectDetailsPane;
    @FXML
    private AnchorPane personDetailsPane;
    @FXML
    private AnchorPane skillDetailsPane;
    @FXML
    private AnchorPane teamDetailsPane;
    @FXML
    private Button editButton;
    @FXML
    private ProjectDetailsPaneController projectDetailsPaneController;
    @FXML
    private PersonDetailsPaneController personDetailsPaneController;
    @FXML
    private SkillDetailsPaneController skillDetailsPaneController;
    @FXML
    private TeamDetailsPaneController teamDetailsPaneController;

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
            } else if (objectForDisplay instanceof Team) {
                showTeamDetailPane((Team)objectForDisplay);
            }
        }
    }


    private void clear() {
        for (Node node : stackPane.getChildren()) {
            node.setVisible(false);
        }
        editButton.setVisible(false);
    }

    private void showSkillDetailPane(Skill skill) {
        skillDetailsPaneController.showDetails(skill);

        skillDetailsPane.setVisible(true);
        projectDetailsPane.setVisible(false);
        personDetailsPane.setVisible(false);
        teamDetailsPane.setVisible(false);

        showEditButton();
        editButton.setOnAction(event -> mainController.editSkill());
    }

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneController.showDetails(project);

        skillDetailsPane.setVisible(false);
        projectDetailsPane.setVisible(true);
        personDetailsPane.setVisible(false);
        teamDetailsPane.setVisible(false);

        showEditButton();
        editButton.setOnAction(event -> mainController.editProject());
    }

    private void showPersonDetailsPane(Person person) {
        personDetailsPaneController.showDetails(person);

        skillDetailsPane.setVisible(false);
        projectDetailsPane.setVisible(false);
        personDetailsPane.setVisible(true);
        teamDetailsPane.setVisible(false);

        showEditButton();
        editButton.setOnAction(event -> mainController.editPerson());
    }

    private void showTeamDetailPane(Team team) {
        teamDetailsPaneController.showDetails(team);

        skillDetailsPane.setVisible(false);
        projectDetailsPane.setVisible(false);
        personDetailsPane.setVisible(false);
        teamDetailsPane.setVisible(true);

        showEditButton();
        editButton.setOnAction(event -> mainController.editTeam());
    }

    private void showEditButton() {
        editButton.setVisible(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

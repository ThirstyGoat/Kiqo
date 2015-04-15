package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import seng302.group4.Item;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;
import seng302.group4.Team;

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
    private Button deleteButton;
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

        editButton.setOnAction(event -> mainController.editItem());
        deleteButton.setOnAction(event -> mainController.deleteItem());
    }


    /**
     * Display the details of the specified item.
     * 
     * @param item
     */
    public void showDetailsPane(Item item) {
        if (item == null) {
            clear();
        } else {
            if (item instanceof Project) {
                showProjectDetailsPane((Project) item);
            } else if (item instanceof Person) {
                showPersonDetailsPane((Person) item);
            } else if (item instanceof Skill) {
                showSkillDetailPane((Skill) item);
            } else if (item instanceof Team) {
                showTeamDetailPane((Team) item);
            }
        }
    }


    private void clear() {
        for (final Node node : stackPane.getChildren()) {
            node.setVisible(false);
        }
        editButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    private void showSkillDetailPane(Skill skill) {
        skillDetailsPaneController.showDetails(skill);

        skillDetailsPane.setVisible(true);
        projectDetailsPane.setVisible(false);
        personDetailsPane.setVisible(false);
        teamDetailsPane.setVisible(false);

        showOptionButtons();
    }

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneController.showDetails(project);

        skillDetailsPane.setVisible(false);
        projectDetailsPane.setVisible(true);
        personDetailsPane.setVisible(false);
        teamDetailsPane.setVisible(false);

        showOptionButtons();
    }

    private void showPersonDetailsPane(Person person) {
        personDetailsPaneController.showDetails(person);

        skillDetailsPane.setVisible(false);
        projectDetailsPane.setVisible(false);
        personDetailsPane.setVisible(true);
        teamDetailsPane.setVisible(false);

        showOptionButtons();
    }

    private void showTeamDetailPane(Team team) {
        teamDetailsPaneController.showDetails(team);

        skillDetailsPane.setVisible(false);
        projectDetailsPane.setVisible(false);
        personDetailsPane.setVisible(false);
        teamDetailsPane.setVisible(true);

        showOptionButtons();
    }

    private void showOptionButtons() {
        editButton.setVisible(true);
        deleteButton.setVisible(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
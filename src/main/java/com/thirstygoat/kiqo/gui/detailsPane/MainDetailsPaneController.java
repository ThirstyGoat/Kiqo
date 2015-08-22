package com.thirstygoat.kiqo.gui.detailsPane;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneView;
import com.thirstygoat.kiqo.gui.skill.SkillViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Team;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;

/**
 * Switches between detail panes depending on type of content shown. NOTE: Does not implement IDetailsPaneController (different purpose).
 * @author amy
 */
public class MainDetailsPaneController implements Initializable {
    @FXML
    private BorderPane detailsPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane projectDetailsPane;
    @FXML
    private AnchorPane personDetailsPane;
    @FXML
    private AnchorPane storyDetailsPane;
    @FXML
    private AnchorPane teamDetailsPane;
    @FXML
    private AnchorPane releaseDetailsPane;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private HBox buttonBox;
    @FXML
    private ProjectDetailsPaneController projectDetailsPaneController;
    @FXML
    private PersonDetailsPaneController personDetailsPaneController;
    @FXML
    private StoryDetailsPaneController storyDetailsPaneController;
    @FXML
    private TeamDetailsPaneController teamDetailsPaneController;
    @FXML
    private ReleaseDetailsPaneController releaseDetailsPaneController;

    private MainController mainController;
    private Pane[] panes;
    
    private Pane backlogDetailsPane;
    private Pane sprintDetailsPane;
    private Pane skillDetailsPane;
    private BacklogDetailsPaneViewModel backlogDetailsPaneViewModel;
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;
    private SkillViewModel skillViewModel;
    
    private Pane advancedSearchDetailsPane;
    private AdvancedSearchViewModel advancedSearchViewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clear();

        editButton.setOnAction(event -> mainController.editItem());
        deleteButton.setOnAction(event -> mainController.deleteItem());

        loadDetailsPanes();

        // Advanced Search
        ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> advancedSearchViewTuple = FluentViewLoader.fxmlView(AdvancedSearchView.class).load();
        advancedSearchDetailsPane = (Pane) advancedSearchViewTuple.getView();
        stackPane.getChildren().add(advancedSearchDetailsPane);
        advancedSearchViewModel = advancedSearchViewTuple.getViewModel();
        
        panes = new Pane[] {
                projectDetailsPane,
                personDetailsPane,
                backlogDetailsPane,
                skillDetailsPane,
                storyDetailsPane,
                teamDetailsPane,
                releaseDetailsPane,
                sprintDetailsPane,
                advancedSearchDetailsPane
        };
        clear();
    }

    private void loadDetailsPanes() {
        ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> backlogDetailsPaneViewTuple = FluentViewLoader.fxmlView(BacklogDetailsPaneView.class).load();
        backlogDetailsPane = (Pane) backlogDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(backlogDetailsPane);
        backlogDetailsPaneViewModel = backlogDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> sprintDetailsPaneViewTuple = FluentViewLoader.fxmlView(SprintDetailsPaneView.class).load();
        sprintDetailsPane = (Pane) sprintDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(sprintDetailsPane);
        sprintDetailsPaneViewModel = sprintDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<SkillDetailsPaneView, SkillViewModel> skillDetailsPaneViewTuple = FluentViewLoader.fxmlView(SkillDetailsPaneView.class).load();
        skillDetailsPane = (Pane) skillDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(skillDetailsPane);
        skillViewModel = skillDetailsPaneViewTuple.getViewModel();
    }

    /**
     * Display the details of the specified item.
     *
     * @param item item to be displayed
     */
    public void showDetailsPane(Item item) {
        detailsPane.setPadding(new Insets(20, 20, 20, 20));
        buttonBox.setPadding(new Insets(0, 0, 0, 0));
        if (item == null) {
            clear();
        } else {
            if (item instanceof Project) {
                showProjectDetailsPane((Project) item);
            } else if (item instanceof Person) {
                showPersonDetailsPane((Person) item);
            } else if (item instanceof Skill) {
                showSkillDetailsPane((Skill) item);
            } else if (item instanceof Team) {
                showTeamDetailsPane((Team) item);
            } else if (item instanceof Release) {
                showReleaseDetailPane((Release) item);
            } else if (item instanceof Story) {
                showStoryDetailPane((Story) item);
            } else if (item instanceof Backlog) {
                showBacklogDetailsPane((Backlog) item);
            } else if (item instanceof Sprint) {
                showSprintDetailsPane((Sprint) item);
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

    private void showSkillDetailsPane(Skill skill) {
        skillViewModel.load(skill, mainController.selectedOrganisationProperty.get());
        show(skillDetailsPane);
        showOptionButtons();
    }

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneController.showDetails(project);
        show(projectDetailsPane);
        showOptionButtons();
    }

    private void showPersonDetailsPane(Person person) {
        personDetailsPaneController.showDetails(person);
        show(personDetailsPane);
        showOptionButtons();
    }

    private void showTeamDetailsPane(Team team) {
        teamDetailsPaneController.showDetails(team);
        show(teamDetailsPane);
        showOptionButtons();
    }

    private void showReleaseDetailPane(Release release) {
        releaseDetailsPaneController.showDetails(release);
        show(releaseDetailsPane);
        showOptionButtons();
    }

    private void showStoryDetailPane(Story story) {
        detailsPane.setPadding(new Insets(0, 0, 0, 0));
        buttonBox.setPadding(new Insets(0, 20, 20, 0));
        storyDetailsPaneController.showDetails(story);
        show(storyDetailsPane);
        showOptionButtons();
    }

    private void showBacklogDetailsPane(Backlog backlog) {
        backlogDetailsPaneViewModel.load(backlog, mainController.selectedOrganisationProperty.get());
        show(backlogDetailsPane);
        showOptionButtons();
    }
    
    private void showSprintDetailsPane(Sprint sprint) {
        sprintDetailsPaneViewModel.load(sprint, mainController.selectedOrganisationProperty.get());
        show(sprintDetailsPane);
        showOptionButtons();
    }

    /**
     * Shows the appropriate pane
     * @param pane Pane to be shown
     */
    private void show(Pane pane) {
        // Hide all panes initially, then show the appropriate pane
        for (final Pane p : panes) {
            p.setVisible(false);
        }

        pane.setVisible(true);
    }

    public void showSearchPane() {
        show(advancedSearchDetailsPane);
    }

    private void showOptionButtons() {
        editButton.setVisible(true);
        deleteButton.setVisible(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        projectDetailsPaneController.setMainController(mainController);
        teamDetailsPaneController.setMainController(mainController);
        storyDetailsPaneController.setMainController(mainController);
        advancedSearchViewModel.setMainController(mainController);
    }
}
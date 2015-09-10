package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneView;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneView;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneView;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneView;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneView;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.*;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

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
    private AnchorPane infoPane;
    @FXML
    private AnchorPane personDetailsPane;
    @FXML
    private AnchorPane storyDetailsPane;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private HBox buttonBox;
    @FXML
    private PersonDetailsPaneController personDetailsPaneController;
    @FXML
    private StoryDetailsPaneController storyDetailsPaneController;

    private MainController mainController;
    private Pane[] panes;

    private Pane backlogDetailsPane;
    private Pane projectDetailsPane;
    private Pane sprintDetailsPane;
    private Pane skillDetailsPane;
    private Pane releaseDetailsPane;
    private Pane teamDetailsPane;
    private Loadable<Backlog> backlogDetailsPaneViewModel;
    private ProjectDetailsPaneViewModel projectDetailsPaneViewModel;
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;
    private Loadable<Skill> skillDetailsPaneViewModel;
    private Loadable<Release> releaseDetailsPaneViewModel;
    private TeamDetailsPaneViewModel teamDetailsPaneViewModel;
    
    private Pane advancedSearchDetailsPane;
    private AdvancedSearchViewModel advancedSearchViewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clear();

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
                advancedSearchDetailsPane,
                infoPane
        };
        clear();
    }

    private void loadDetailsPanes() {
        ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> backlogDetailsPaneViewTuple = FluentViewLoader.fxmlView(BacklogDetailsPaneView.class).load();
        backlogDetailsPane = (Pane) backlogDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(backlogDetailsPane);
        backlogDetailsPaneViewModel = backlogDetailsPaneViewTuple.getViewModel();

        ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> projectDetailsPaneViewTuple = FluentViewLoader.fxmlView(ProjectDetailsPaneView.class).load();
        projectDetailsPane = (AnchorPane) projectDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(projectDetailsPane);
        projectDetailsPaneViewModel = projectDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> sprintDetailsPaneViewTuple = FluentViewLoader.fxmlView(SprintDetailsPaneView.class).load();
        sprintDetailsPane = (Pane) sprintDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(sprintDetailsPane);
        sprintDetailsPaneViewModel = sprintDetailsPaneViewTuple.getViewModel();

        ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> skillDetailsPaneViewTuple = FluentViewLoader.fxmlView(SkillDetailsPaneView.class).load();
        skillDetailsPane = (Pane) skillDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(skillDetailsPane);
        skillDetailsPaneViewModel = skillDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> releaseDetailsPaneViewTuple = FluentViewLoader.fxmlView(ReleaseDetailsPaneView.class).load();
        releaseDetailsPane = (Pane) releaseDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(releaseDetailsPane);
        releaseDetailsPaneViewModel = releaseDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> teamDetailsPaneViewTuple = FluentViewLoader.fxmlView(TeamDetailsPaneView.class).load();
        teamDetailsPane = (Pane) teamDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(teamDetailsPane);
        teamDetailsPaneViewModel = teamDetailsPaneViewTuple.getViewModel();
    }

    /**
     * Display the details of the specified item.
     *
     * @param item item to be displayed
     */
    public void showDetailsPane(Item item) {
        detailsPane.setPadding(new Insets(20, 20, 20, 20));
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
        infoPane.setVisible(true);
    }

    private void showSkillDetailsPane(Skill skill) {
        skillDetailsPaneViewModel.load(skill, mainController.selectedOrganisationProperty.get());
        show(skillDetailsPane);
    }

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneViewModel.load(project, mainController.selectedOrganisationProperty.get());
        show(projectDetailsPane);
    }

    private void showPersonDetailsPane(Person person) {
        personDetailsPaneController.showDetails(person);
        show(personDetailsPane);
    }

    private void showTeamDetailsPane(Team team) {
        teamDetailsPaneViewModel.load(team, mainController.selectedOrganisationProperty.get());
        show(teamDetailsPane);
    }

    private void showReleaseDetailPane(Release release) {
        releaseDetailsPaneViewModel.load(release, mainController.selectedOrganisationProperty.get());
        show(releaseDetailsPane);
    }

    private void showStoryDetailPane(Story story) {
        detailsPane.setPadding(new Insets(0, 0, 0, 0));
        storyDetailsPaneController.showDetails(story);
        show(storyDetailsPane);
    }

    private void showBacklogDetailsPane(Backlog backlog) {
        backlogDetailsPaneViewModel.load(backlog, mainController.selectedOrganisationProperty.get());
        show(backlogDetailsPane);
    }
    
    private void showSprintDetailsPane(Sprint sprint) {
        sprintDetailsPaneViewModel.load(sprint, mainController.selectedOrganisationProperty.get());
        show(sprintDetailsPane);
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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        projectDetailsPaneViewModel.mainControllerProperty().set(mainController);
        storyDetailsPaneController.setMainController(mainController);
        advancedSearchViewModel.setMainController(mainController);
        sprintDetailsPaneViewModel.getScrumBoardViewModel().setMainController(mainController);
        teamDetailsPaneViewModel.mainControllerProperty().set(mainController);
    }
}

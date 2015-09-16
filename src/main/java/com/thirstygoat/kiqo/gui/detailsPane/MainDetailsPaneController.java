package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneView;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneView;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneView;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneView;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneView;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneView;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneView;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.*;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

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

    private MainController mainController;
    private Pane[] panes;

    private Pane backlogDetailsPane;
    private Pane projectDetailsPane;
    private Pane sprintDetailsPane;
    private Pane skillDetailsPane;
    private Pane personDetailsPane;
    private Pane releaseDetailsPane;
    private Pane teamDetailsPane;
    private Pane storyDetailsPane;
    
    private Loadable<Backlog> backlogDetailsPaneViewModel;
    private ProjectDetailsPaneViewModel projectDetailsPaneViewModel;
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;
    private Loadable<Skill> skillDetailsPaneViewModel;
    private Loadable<Release> releaseDetailsPaneViewModel;
    private TeamDetailsPaneViewModel teamDetailsPaneViewModel;
    private PersonDetailsPaneViewModel personViewModel;
    private StoryDetailsPaneViewModel storyDetailsPaneViewModel;

    private StoryDetailsPaneView storyDetailsPaneView;

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

        ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> personDetailsPaneViewTuple = FluentViewLoader.fxmlView(PersonDetailsPaneView.class).load();
        personDetailsPane = (Pane) personDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(personDetailsPane);
        personViewModel = personDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> releaseDetailsPaneViewTuple = FluentViewLoader.fxmlView(ReleaseDetailsPaneView.class).load();
        releaseDetailsPane = (Pane) releaseDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(releaseDetailsPane);
        releaseDetailsPaneViewModel = releaseDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> teamDetailsPaneViewTuple = FluentViewLoader.fxmlView(TeamDetailsPaneView.class).load();
        teamDetailsPane = (Pane) teamDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(teamDetailsPane);
        teamDetailsPaneViewModel = teamDetailsPaneViewTuple.getViewModel();

        ViewTuple<StoryDetailsPaneView, StoryDetailsPaneViewModel> storyDetailsPaneViewTuple = FluentViewLoader.fxmlView(StoryDetailsPaneView.class).load();
        storyDetailsPane = (Pane) storyDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(storyDetailsPane);
        storyDetailsPaneViewModel = storyDetailsPaneViewTuple.getViewModel();
        storyDetailsPaneView = storyDetailsPaneViewTuple.getCodeBehind();
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
        personViewModel.load(person, mainController.selectedOrganisationProperty.get());
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
        storyDetailsPaneViewModel.load(story, mainController.selectedOrganisationProperty.get());
        show(storyDetailsPane);
        storyDetailsPaneView.showDetails(new SimpleObjectProperty<>(storyDetailsPaneViewModel.getStory()));
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
        storyDetailsPaneView.setMainController(mainController);
        advancedSearchViewModel.setMainController(mainController);
        sprintDetailsPaneViewModel.getScrumBoardViewModel().setMainController(mainController);
        teamDetailsPaneViewModel.mainControllerProperty().set(mainController);
    }
}

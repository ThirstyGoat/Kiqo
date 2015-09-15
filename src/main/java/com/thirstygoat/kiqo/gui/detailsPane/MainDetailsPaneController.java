package com.thirstygoat.kiqo.gui.detailsPane;

import com.sun.istack.internal.NotNull;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneView;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.person.PersonViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneView;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneView;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneView;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneView;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneView;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.*;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneView;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.*;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Skill;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Switches between detail panes depending on type of content shown. NOTE: Does not implement IDetailsPaneController (different purpose).
 * @author amy
 */
public class MainDetailsPaneController implements Initializable {
    @FXML
    private BorderPane detailsPane;
    @FXML
    private TabPane tabPane;

    private MainController mainController;
    private Pane[] panes;

    private Pane backlogDetailsPane;
    private Pane projectDetailsPane;
    private Pane sprintDetailsPane;
    private Pane skillDetailsPane;
    private Pane personDetailsPane;
    private Pane releaseDetailsPane;
    private Pane teamDetailsPane;
    
    private Loadable<Backlog> backlogDetailsPaneViewModel;
    private ProjectDetailsPaneViewModel projectDetailsPaneViewModel;
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;
    private Loadable<Skill> skillDetailsPaneViewModel;
    private Loadable<Release> releaseDetailsPaneViewModel;
    private TeamDetailsPaneViewModel teamDetailsPaneViewModel;
    private PersonDetailsPaneViewModel personViewModel;
    
    private Pane advancedSearchDetailsPane;
    private AdvancedSearchViewModel advancedSearchViewModel;

    private Map<Item, Tab> tabMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDetailsPanes();

        // Advanced Search
        ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> advancedSearchViewTuple = FluentViewLoader.fxmlView(AdvancedSearchView.class).load();
        advancedSearchDetailsPane = (Pane) advancedSearchViewTuple.getView();
        advancedSearchViewModel = advancedSearchViewTuple.getViewModel();
        
        panes = new Pane[] {
                projectDetailsPane,
                personDetailsPane,
                backlogDetailsPane,
                skillDetailsPane,
                teamDetailsPane,
                releaseDetailsPane,
                sprintDetailsPane,
                advancedSearchDetailsPane,
        };
    }

    private void loadDetailsPanes() {
        ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> backlogDetailsPaneViewTuple = FluentViewLoader.fxmlView(BacklogDetailsPaneView.class).load();
        backlogDetailsPane = (Pane) backlogDetailsPaneViewTuple.getView();
        backlogDetailsPaneViewModel = backlogDetailsPaneViewTuple.getViewModel();

        ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> projectDetailsPaneViewTuple = FluentViewLoader.fxmlView(ProjectDetailsPaneView.class).load();
        projectDetailsPane = (AnchorPane) projectDetailsPaneViewTuple.getView();
        projectDetailsPaneViewModel = projectDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> sprintDetailsPaneViewTuple = FluentViewLoader.fxmlView(SprintDetailsPaneView.class).load();
        sprintDetailsPane = (Pane) sprintDetailsPaneViewTuple.getView();
        sprintDetailsPaneViewModel = sprintDetailsPaneViewTuple.getViewModel();

        ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> skillDetailsPaneViewTuple = FluentViewLoader.fxmlView(SkillDetailsPaneView.class).load();
        skillDetailsPane = (Pane) skillDetailsPaneViewTuple.getView();
        skillDetailsPaneViewModel = skillDetailsPaneViewTuple.getViewModel();

        ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> personDetailsPaneViewTuple = FluentViewLoader.fxmlView(PersonDetailsPaneView.class).load();
        personDetailsPane = (Pane) personDetailsPaneViewTuple.getView();
        personViewModel = personDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> releaseDetailsPaneViewTuple = FluentViewLoader.fxmlView(ReleaseDetailsPaneView.class).load();
        releaseDetailsPane = (Pane) releaseDetailsPaneViewTuple.getView();
        releaseDetailsPaneViewModel = releaseDetailsPaneViewTuple.getViewModel();
        
        ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> teamDetailsPaneViewTuple = FluentViewLoader.fxmlView(TeamDetailsPaneView.class).load();
        teamDetailsPane = (Pane) teamDetailsPaneViewTuple.getView();
        teamDetailsPaneViewModel = teamDetailsPaneViewTuple.getViewModel();
    }

    private Node getDetailsPane(Item item) {
            if (item.getClass() == Project.class) {
                return getProjectDetailsPane((Project) item);
            } else if (item.getClass() == Person.class) {
                return getPersonDetailsPane((Person) item);
            } else if (item.getClass() == Skill.class) {
                return getSkillDetailsPane((Skill) item);
            } else if (item.getClass() == Team.class) {
                return getTeamDetailsPane((Team) item);
            } else if (item.getClass() == Release.class) {
                return getReleaseDetailsPane((Release) item);
            } else if (item.getClass() == Story.class) {
                return getStoryDetailsPane((Story) item);
            } else if (item.getClass() == Backlog.class) {
                return getBacklogDetailsPane((Backlog) item);
            } else if (item.getClass() == Sprint.class) {
                return getSprintDetailsPane((Sprint) item);
            }
        return null;
    }

    public void showDetailsPane(Item item) {
        if (item == null) {
            // do nothing for now
        } else if (tabMap.containsKey(item)) {
            // Then we have a reference to the tab object
            // We check to make sure the tab hasn't been closed before showing it
            Tab chosenTab = tabMap.get(item);

            if (!tabPane.getTabs().contains(chosenTab)) {
                tabPane.getTabs().add(chosenTab);
            }

            tabPane.getSelectionModel().select(chosenTab);
        } else {
            Tab tab = new Tab();

            // Bind the tab heading text to the short name of the shown item
            tab.textProperty().bind(item.shortNameProperty());

            Node contentNode = getDetailsPane(item);
            tab.setContent(contentNode);
            contentNode.getStyleClass().add("details-pane-tab");

            // Add the tab to the map, so we can easily show it if necessary
            tabMap.put(item, tab);

            // Add the tab to the tabpane, and select it
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        }
    }

    private Node getSkillDetailsPane(Skill skill) {
        ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(SkillDetailsPaneView.class).load();
        viewTuple.getViewModel().load(skill, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getProjectDetailsPane(Project project) {
        ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(ProjectDetailsPaneView.class).load();
        viewTuple.getViewModel().mainControllerProperty().set(mainController);
        viewTuple.getViewModel().load(project, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getPersonDetailsPane(Person person) {
        ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(PersonDetailsPaneView.class).load();
        viewTuple.getViewModel().load(person, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getTeamDetailsPane(Team team) {
        ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(TeamDetailsPaneView.class).load();
        viewTuple.getViewModel().mainControllerProperty().set(mainController);
        viewTuple.getViewModel().load(team, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getReleaseDetailsPane(Release release) {
        ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(ReleaseDetailsPaneView.class).load();
        viewTuple.getViewModel().load(release, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getStoryDetailsPane(Story story) {
        // Old school way of loading a details pane
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainController.class.getClassLoader().getResource("detailsPane/story.fxml"));
        ((StoryDetailsPaneController) loader.getController()).setMainController(mainController);
        ((StoryDetailsPaneController) loader.getController()).showDetails(story);
        try {
            return loader.load();
        } catch (IOException ignored) {}
        return null;
    }

    private Node getBacklogDetailsPane(Backlog backlog) {
        ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(BacklogDetailsPaneView.class).load();
        viewTuple.getViewModel().load(backlog, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getSprintDetailsPane(Sprint sprint) {
        ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> viewTuple = FluentViewLoader.fxmlView(SprintDetailsPaneView.class).load();
        viewTuple.getViewModel().load(sprint, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
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
//        projectDetailsPaneViewModel.mainControllerProperty().set(mainController);
//        storyDetailsPaneController.setMainController(mainController);
//        advancedSearchViewModel.setMainController(mainController);
//        sprintDetailsPaneViewModel.getScrumBoardViewModel().setMainController(mainController);
//        teamDetailsPaneViewModel.mainControllerProperty().set(mainController);
    }
}

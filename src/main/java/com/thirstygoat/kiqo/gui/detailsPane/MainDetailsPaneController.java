package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneView;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneView;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneView;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.*;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.SimpleObjectProperty;
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
    private AnchorPane projectDetailsPane;
    @FXML
    private AnchorPane personDetailsPane;
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
    private TeamDetailsPaneController teamDetailsPaneController;
    @FXML
    private ReleaseDetailsPaneController releaseDetailsPaneController;

    private MainController mainController;
    private Pane[] panes;
    
    private Pane backlogDetailsPane;
    private Pane sprintDetailsPane;
    private Pane skillDetailsPane;
    private Pane storyDetailsPane;
    private BacklogDetailsPaneViewModel backlogDetailsPaneViewModel;
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;
    private SkillViewModel skillViewModel;
    private StoryDetailsPaneViewModel storyDetailsPaneViewModel;
    private StoryDetailsPaneView storyDetailsPaneView;
    
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
        
        ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> skillDetailsPaneViewTuple = FluentViewLoader.fxmlView(SkillDetailsPaneView.class).load();
        skillDetailsPane = (Pane) skillDetailsPaneViewTuple.getView();
        stackPane.getChildren().add(skillDetailsPane);
        skillViewModel = skillDetailsPaneViewTuple.getViewModel();

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
        storyDetailsPaneViewModel.load(story, mainController.selectedOrganisationProperty.get());
        show(storyDetailsPane);
        showOptionButtons();
        storyDetailsPaneView.showDetails(new SimpleObjectProperty<>(storyDetailsPaneViewModel.getStory()));
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
        advancedSearchViewModel.setMainController(mainController);
    }
}
package com.thirstygoat.kiqo.gui.detailsPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import com.thirstygoat.kiqo.gui.*;
import com.thirstygoat.kiqo.gui.backlog.*;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.person.*;
import com.thirstygoat.kiqo.gui.project.*;
import com.thirstygoat.kiqo.gui.release.*;
import com.thirstygoat.kiqo.gui.skill.*;
import com.thirstygoat.kiqo.gui.sprint.*;
import com.thirstygoat.kiqo.gui.team.*;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import com.thirstygoat.kiqo.model.*;

import de.saxsys.mvvmfx.*;


/**
 * Switches between detail panes depending on type of content shown.
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
    private AnchorPane storyDetailsPane;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private HBox buttonBox;
    @FXML
    private StoryDetailsPaneController storyDetailsPaneController;

    private MainController mainController;

    private Pane projectDetailsPane;
    private Pane sprintDetailsPane;
    private Pane skillDetailsPane;
    private Pane releaseDetailsPane;
    private Pane teamDetailsPane;

    private ProjectDetailsPaneViewModel projectDetailsPaneViewModel;
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;
    private Loadable<Skill> skillDetailsPaneViewModel;
    private Loadable<Release> releaseDetailsPaneViewModel;
    private TeamDetailsPaneViewModel teamDetailsPaneViewModel;
    
    private Pane advancedSearchDetailsPane;
    private AdvancedSearchViewModel advancedSearchViewModel;
    private DetailsPaneLoader<Backlog, BacklogDetailsPaneViewModel, BacklogDetailsPaneView> backlogDetailsPaneLoader;
    private DetailsPaneLoader<Person, PersonDetailsPaneViewModel, PersonDetailsPaneView> personDetailsPaneLoader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clear();

        loadDetailsPanes();

        // Advanced Search
        ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> advancedSearchViewTuple = FluentViewLoader.fxmlView(AdvancedSearchView.class).load();
        advancedSearchDetailsPane = (Pane) advancedSearchViewTuple.getView();
        stackPane.getChildren().add(advancedSearchDetailsPane);
        advancedSearchViewModel = advancedSearchViewTuple.getViewModel();
        
        clear();
    }
    
    private class DetailsPaneLoader<T extends Item, ViewModelType extends ModelViewModel<T>, ViewType extends FxmlView<ViewModelType>> implements BiConsumer<T, Class<ViewType>> {
        boolean loaded = false;
        ViewModelType viewModel;
        Pane view;
        
        @Override
        public void accept(T item, Class<ViewType> fxmlClass) {
            if (!loaded) {
                load(fxmlClass);
            }
            viewModel.load(item, mainController.selectedOrganisationProperty.get());
            
            show(view);
        }

        /**
         * @param fxmlClass
         */
        private void load(Class<ViewType> fxmlClass) {
            ViewTuple<ViewType, ViewModelType> viewTuple = FluentViewLoader.fxmlView(fxmlClass).load();
            viewModel = viewTuple.getViewModel();
            view = (Pane) viewTuple.getView();
            stackPane.getChildren().add(view); // add to mainDetailsPane
            loaded = true;
        };
    }

    private void loadDetailsPanes() {
        backlogDetailsPaneLoader = new DetailsPaneLoader<>();
        
        ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> projectDetailsPaneViewTuple = FluentViewLoader.fxmlView(ProjectDetailsPaneView.class).load();
        projectDetailsPane = (Pane) projectDetailsPaneViewTuple.getView();
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

        personDetailsPaneLoader = new DetailsPaneLoader<>();
        
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
                personDetailsPaneLoader.accept((Person) item, PersonDetailsPaneView.class);
            } else if (item instanceof Skill) {
                showSkillDetailsPane((Skill) item);
            } else if (item instanceof Team) {
                showTeamDetailsPane((Team) item);
            } else if (item instanceof Release) {
                showReleaseDetailPane((Release) item);
            } else if (item instanceof Story) {
                showStoryDetailPane((Story) item);
            } else if (item instanceof Backlog) {
                backlogDetailsPaneLoader.accept((Backlog) item, BacklogDetailsPaneView.class);
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

    private void showProjectDetailsPane(Project project) {
        projectDetailsPaneViewModel.load(project, mainController.selectedOrganisationProperty.get());
        show(projectDetailsPane);
    }
    
    private void showSkillDetailsPane(Skill skill) {
        skillDetailsPaneViewModel.load(skill, mainController.selectedOrganisationProperty.get());
        show(skillDetailsPane);
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
        for (final Node p : stackPane.getChildren()) {
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

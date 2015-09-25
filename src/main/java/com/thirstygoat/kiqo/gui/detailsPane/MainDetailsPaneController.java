package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.nodes.DraggableTab;
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
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

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
    private AnchorPane infoPane;
    @FXML
    private TabPane tabPane;

    private MainController mainController;

    private OptimizedDetailsPane optimizedDetailsPane;

    private Map<Item, Tab> itemTabMap = new HashMap<>();
    private Map<Tab, Item> tabItemMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        optimizedDetailsPane = new OptimizedDetailsPane();
        tabPane.visibleProperty().bind(Bindings.isNotEmpty(tabPane.getTabs()));
        infoPane.visibleProperty().bind(Bindings.isEmpty(tabPane.getTabs()));


        // Set focused item property, to always be set to the currently viewing tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            MainController.focusedItemProperty.set(tabItemMap.get(newValue));
        });
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
        } else if (itemTabMap.containsKey(item)) {
            // Then we have a reference to the tab object
            // We check to make sure the tab hasn't been closed before showing it
            Tab chosenTab = itemTabMap.get(item);

            if (tabPane.getTabs().contains(chosenTab)) {
                tabPane.getSelectionModel().select(chosenTab);
            } else if (chosenTab.getTabPane() != null && chosenTab.getTabPane().getScene().getWindow().isShowing()) {
                // Then this tab lies within another tab pane, which we need to grab and show
                chosenTab.getTabPane().getScene().getWindow().requestFocus();
            } else {
                // Then this tab has previously been created, but is not displayed anywhere, display it
                tabPane.getTabs().add(chosenTab);
                tabPane.getSelectionModel().select(chosenTab);
            }
        } else {
            Tab tab = new DraggableTab(item.shortNameProperty());
            tab.setContextMenu(generateContextMenu(item, tab));
            tab.setOnClosed(event -> {
                MainController.focusedItemProperty.set(null); // Potentially hacky
            });
            Node contentNode = getDetailsPane(item);
            tab.setContent(contentNode);
            contentNode.getStyleClass().add("details-pane-tab");

            // Add the tab to the map, so we can easily show it if necessary
            itemTabMap.put(item, tab);
            tabItemMap.put(tab, item);

            // Add the tab to the TabPane, and select it
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        }
    }

    private ContextMenu generateContextMenu(Item item, Tab tab) {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem closeTabMenuItem = new MenuItem("Close Tab");
//        closeTabMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
        MenuItem closeAllMenuItem = new MenuItem("Close All Tabs");
        MenuItem closeOthersMenuItem = new MenuItem("Close Other Tabs");

        closeTabMenuItem.setOnAction(event -> tab.getTabPane().getTabs().remove(tab));
        closeAllMenuItem.setOnAction(event -> tab.getTabPane().getTabs().clear());
        closeOthersMenuItem.setOnAction(event -> tab.getTabPane().getTabs().removeIf(tab1 -> tab1 != tab));

        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");

        editMenuItem.setOnAction(event -> mainController.editItem(item));
        deleteMenuItem.setOnAction(event -> mainController.deleteItem(item));

        contextMenu.getItems().addAll(
                closeTabMenuItem, closeOthersMenuItem,
                closeAllMenuItem, new SeparatorMenuItem(),
                editMenuItem, deleteMenuItem);

        return contextMenu;
    }

    private Node getSkillDetailsPane(Skill skill) {
        ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getSkillViewTuple();
        viewTuple.getViewModel().load(skill, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getProjectDetailsPane(Project project) {
        ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getProjectViewTuple();
        viewTuple.getViewModel().mainControllerProperty().set(mainController);
        viewTuple.getViewModel().load(project, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getPersonDetailsPane(Person person) {
        ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getPersonViewTuple();
        viewTuple.getViewModel().load(person, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getTeamDetailsPane(Team team) {
        ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getTeamViewTuple();
        viewTuple.getViewModel().mainControllerProperty().set(mainController);
        viewTuple.getViewModel().load(team, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getReleaseDetailsPane(Release release) {
        ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getReleaseViewTuple();
        viewTuple.getViewModel().load(release, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getStoryDetailsPane(Story story) {
        ViewTuple<StoryDetailsPaneView, StoryDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getStoryViewTuple();
        viewTuple.getViewModel().load(story, mainController.selectedOrganisationProperty.get());
        viewTuple.getCodeBehind().showDetails(story);
        viewTuple.getCodeBehind().setMainController(mainController);
        return viewTuple.getView();
    }

    private Node getBacklogDetailsPane(Backlog backlog) {
        ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getBacklogViewTuple();
        viewTuple.getViewModel().load(backlog, mainController.selectedOrganisationProperty.get());
        return viewTuple.getView();
    }

    private Node getSprintDetailsPane(Sprint sprint) {
        ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> viewTuple = optimizedDetailsPane.getSprintViewTuple();
        viewTuple.getViewModel().getScrumBoardViewModel().setMainController(mainController);
        viewTuple.getViewModel().load(sprint, mainController.selectedOrganisationProperty.get());
        viewTuple.getViewModel().getDetailsViewModel().setMainController(mainController);
        return viewTuple.getView();
    }


    public void showSearchPane() {
        // Advanced Search
        ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> advancedSearchViewTuple = optimizedDetailsPane.getSearchViewTuple();
        advancedSearchViewTuple.getViewModel().setMainController(mainController);
        // TODO, update heading to display what the search was for
        StringProperty advancedSearchHeading = new SimpleStringProperty("Advanced Search");

        Tab tab = new DraggableTab(advancedSearchHeading);
        tab.setContent(advancedSearchViewTuple.getView());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void closeTab(Item item) {
        if (item == null)
            return;

        if (itemTabMap.containsKey(item)) {
            Tab tab = itemTabMap.get(item);

            if (tab.getTabPane() != null)
                tab.getTabPane().getTabs().remove(tab);
        }
    }

    public void closeAllTabs() {
        itemTabMap.keySet().forEach(this::closeTab);
    }

    /**
     * Closes the currently selected tab in the main tab view
     */
    public void closeSelectedTab() {
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
    }
}

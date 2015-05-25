package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.nodes.GoatTreeItem;
import com.thirstygoat.kiqo.nodes.ProjectsTreeItem;
import com.thirstygoat.kiqo.nodes.TreeNodeHeading;
import com.thirstygoat.kiqo.util.Utilities;


/**
 * Created by samschofield and James on 14/05/15.
 */
public class SideBarController implements Initializable {
    private final ContextMenu contextMenu = new ContextMenu();
    private final Map<String, Control> tabListViewMap = new HashMap<>();
    @FXML
    private TreeView<Item> projectTreeView;
    @FXML
    private ListView<Person> peopleListView;
    @FXML
    private ListView<Skill> skillsListView;
    @FXML
    private ListView<Team> teamsListView;
    @FXML
    private Tab projectTab;
    @FXML
    private Tab peopleTab;
    @FXML
    private Tab skillsTab;
    @FXML
    private Tab teamsTab;
    @FXML
    private TabPane tabViewPane;
    private MainController mainController;

    /**
     * Attaches cell factory and selection listener to the list view.
     */
    private static <T extends Item> void initialiseListView(ListView<T> listView, ContextMenu contextMenu) {
        // derived from example at
        // http://docs.oracle.com/javafx/2/api/javafx/scene/control/Cell.html
        listView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(final ListView<T> arg0) {
                return new ListCell<T>() {
                    @Override
                    protected void updateItem(final T item, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getShortName());
                        if (item != null) {
                            setContextMenu(contextMenu);
                        }
                    }
                };
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseTabs();
    }

    /**
     * Initialise the tabs for the sidebar
     */
    private void initialiseTabs() {
        // uses getId because equals method on tabs doesnt play nicely with hashmap
        tabListViewMap.put(projectTab.getId(), projectTreeView);
        tabListViewMap.put(teamsTab.getId(), teamsListView);
        tabListViewMap.put(peopleTab.getId(), peopleListView);
        tabListViewMap.put(skillsTab.getId(), skillsListView);

        // Create listeners for lists
        final ChangeListener<Item> listViewChangeListener = (o, oldValue, newValue) -> {
            mainController.focusedItemProperty.set(newValue);
        };

        final ChangeListener<TreeItem<Item>> treeViewChangeListener = (o, oldValue, newValue) -> {
            Item toShow = (newValue != null) ? newValue.getValue() : null;
            if (newValue != null && newValue.getValue().getClass() == TreeNodeHeading.class) {
                toShow = null;
            }
            mainController.focusedItemProperty.set(toShow);
        };

        // Add the listener only when the tab is in focus, when it is out of focus, remove the listener
        tabViewPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            // Remove the change listeners
            projectTreeView.getSelectionModel().selectedItemProperty().removeListener(treeViewChangeListener);
            peopleListView.getSelectionModel().selectedItemProperty().removeListener(listViewChangeListener);
            teamsListView.getSelectionModel().selectedItemProperty().removeListener(listViewChangeListener);
            skillsListView.getSelectionModel().selectedItemProperty().removeListener(listViewChangeListener);

            // Add the change listener on the appropriate TreeView/ListView
            if (newValue == projectTab) {
                mainController.getMenuBarController().updateAfterProjectListSelected(true);
                final int selectedIndex = projectTreeView.getSelectionModel().selectedIndexProperty().get();
                projectTreeView.getSelectionModel().select(null);
                projectTreeView.getSelectionModel().selectedItemProperty().addListener(treeViewChangeListener);
                projectTreeView.getSelectionModel().select(selectedIndex == -1 ? 0 : selectedIndex);
            } else if (newValue == peopleTab) {
                mainController.getMenuBarController().updateAfterPersonListSelected(true);
                final int selectedIndex = peopleListView.getSelectionModel().selectedIndexProperty().get();
                peopleListView.getSelectionModel().select(null);
                peopleListView.getSelectionModel().selectedItemProperty().addListener(listViewChangeListener);
                peopleListView.getSelectionModel().select(selectedIndex == -1 ? 0 : selectedIndex);
            } else if (newValue == teamsTab) {
                mainController.getMenuBarController().updateAfterTeamListSelected(true);
                final int selectedIndex = teamsListView.getSelectionModel().selectedIndexProperty().get();
                teamsListView.getSelectionModel().select(null);
                teamsListView.getSelectionModel().selectedItemProperty().addListener(listViewChangeListener);
                teamsListView.getSelectionModel().select(selectedIndex == -1 ? 0 : selectedIndex);
            } else if (newValue == skillsTab) {
                mainController.getMenuBarController().updateAfterSkillListSelected(true);
                final int selectedIndex = skillsListView.getSelectionModel().selectedIndexProperty().get();
                skillsListView.getSelectionModel().select(null);
                skillsListView.getSelectionModel().selectedItemProperty().addListener(listViewChangeListener);
                skillsListView.getSelectionModel().select(selectedIndex == -1 ? 0 : selectedIndex);
            }
        });

        // Initially select the project tab
        projectTreeView.getSelectionModel().selectedItemProperty().addListener(treeViewChangeListener);
    }

    private void initializeListViews() {
        setListViewData();
        projectTreeView.setShowRoot(false);

        // Get a list of them
        final ArrayList<ListView<? extends Item>> listViews = new ArrayList<>();
        listViews.add(peopleListView);
        listViews.add(skillsListView);
        listViews.add(teamsListView);

        // All these ListViews share a single context menu
        final MenuItem editContextMenu = new MenuItem("Edit");
        final MenuItem deleteContextMenu = new MenuItem("Delete");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);
        editContextMenu.setOnAction(event -> mainController.editItem());
        deleteContextMenu.setOnAction(event -> mainController.deleteItem());

        for (final ListView<? extends Item> listView : listViews) {
            SideBarController.initialiseListView(listView, contextMenu);
        }
        setListViewListener();
    }

    /**
     * sets a change listener on the selected item of the selected listView
     */
    private void setListViewListener() {
        final Tab selectedTab = tabViewPane.getSelectionModel().getSelectedItem();
        if (tabListViewMap.get(selectedTab.getId()).getClass() != TreeView.class) {
            @SuppressWarnings("unchecked")
            final ListView<Item> castedListView = (ListView<Item>)tabListViewMap.get(selectedTab.getId());
            castedListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                mainController.focusedItemProperty.set(newValue);
            });
        }
    }

    private void setListViewData() {
        projectTreeView.setCellFactory(new Callback<TreeView<Item>, TreeCell<Item>>() {
            @Override
            public TreeCell<Item> call(TreeView<Item> param) {

                return new TreeCell<Item>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        if (item != null) {
                            textProperty().bind(item.shortNameProperty());
                            if (item.getClass() != TreeNodeHeading.class) {
                                setContextMenu(contextMenu);
                            }
                        } else {
                            textProperty().unbind();
                            textProperty().set("");
                            setContextMenu(null);
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });

        final GoatTreeItem<Project> root = new ProjectsTreeItem(projectTreeView.getSelectionModel());
        projectTreeView.setRoot(root);
        projectTreeView.setShowRoot(false);
        root.setExpanded(true);

        root.setItems(mainController.selectedOrganisationProperty().get().getProjects());
        peopleListView.setItems(Utilities.createSortedList(mainController.selectedOrganisationProperty.get().getPeople()));
        teamsListView.setItems(Utilities.createSortedList(mainController.selectedOrganisationProperty.get().getTeams()));
        skillsListView.setItems(Utilities.createSortedList(mainController.selectedOrganisationProperty.get().getSkills()));

        show(TabOption.PROJECTS);
    }

    public void show(TabOption tabOption) {
        if (tabOption == TabOption.PROJECTS) {
            tabViewPane.getSelectionModel().select(projectTab);
        } else if (tabOption == TabOption.TEAMS) {
            tabViewPane.getSelectionModel().select(teamsTab);
        } else if (tabOption == TabOption.PEOPLE) {
            tabViewPane.getSelectionModel().select(peopleTab);
        } else if (tabOption == TabOption.SKILLS) {
            tabViewPane.getSelectionModel().select(skillsTab);
        }
    }

    /**
     * Set the main controller and initialise the tabs and listviews
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        initializeListViews();
        mainController.selectedOrganisationProperty.addListener((o, oldValue, newValue) -> setListViewData());
    }

    public enum TabOption {
        PROJECTS,
        PEOPLE,
        TEAMS,
        SKILLS
    }
}
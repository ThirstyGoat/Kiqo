package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.gui.nodes.GoatTreeItem;
import com.thirstygoat.kiqo.gui.nodes.ProjectsTreeItem;
import com.thirstygoat.kiqo.gui.nodes.TreeNodeHeading;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.TreeMouseEventDispatcher;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by samschofield and James on 14/05/15.
 */
public class SideBarController implements Initializable {
    private final Map<String, Control> tabListViewMap = new HashMap<>();
    private final ObjectProperty<TabOption> selectedTabProperty = new SimpleObjectProperty<>(TabOption.PROJECTS);
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
    private <T extends Item> void initialiseListView(ListView<T> listView) {
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
                            setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    mainController.showDetailsPane(item);
                                }
                            });
                            setContextMenu(generateContextMenu(item));
                        }
                    }
                };
            }
        });
    }

    public ReadOnlyObjectProperty<TabOption> selectedTabProperty() {
        return selectedTabProperty;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseTabs();
    }

    /**
     * Initialise the tabs for the sidebar
     */
    private void initialiseTabs() {
        // uses getId because equals method on tabs doesn't play nicely with hashmap
        tabListViewMap.put(projectTab.getId(), projectTreeView);
        tabListViewMap.put(teamsTab.getId(), teamsListView);
        tabListViewMap.put(peopleTab.getId(), peopleListView);
        tabListViewMap.put(skillsTab.getId(), skillsListView);
    }

    private ContextMenu generateContextMenu(Item item) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit");
        final MenuItem deleteContextMenu = new MenuItem("Delete");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);
        editContextMenu.setOnAction(event -> mainController.editItem(item));
        deleteContextMenu.setOnAction(event -> mainController.deleteItem(item));

        return contextMenu;
    }

    private void initializeListViews() {
        setListViewData();
        projectTreeView.setShowRoot(false);

        // Get a list of them
        final ArrayList<ListView<? extends Item>> listViews = new ArrayList<>();
        listViews.add(peopleListView);
        listViews.add(skillsListView);
        listViews.add(teamsListView);

        listViews.forEach(this::initialiseListView);
    }

    private void setListViewData() {
        projectTreeView.setCellFactory(new Callback<TreeView<Item>, TreeCell<Item>>() {
            @Override
            public TreeCell<Item> call(TreeView<Item> param) {
                return new TreeCell<Item>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        if (item != null && !empty) {
                            FontAwesome fontAwesome = new FontAwesome();
                            textProperty().bind(item.shortNameProperty());
                            if (item.getClass() != TreeNodeHeading.class) {
                                EventDispatcher originalDispatcher = getEventDispatcher();
                                setEventDispatcher(new TreeMouseEventDispatcher(originalDispatcher, item, mainController));
                                setContextMenu(generateContextMenu(item));
                                setGraphic(null);
                            } else {
                                Node node = null;
                                if (item.getShortName().equals("Releases")) {
                                    node = fontAwesome.create(FontAwesome.Glyph.CALENDAR);
                                } else if (item.getShortName().equals("Backlogs")) {
                                    node = fontAwesome.create(FontAwesome.Glyph.LIST);
                                } else if (item.getShortName().equals("Unallocated Stories")) {
                                    node = fontAwesome.create(FontAwesome.Glyph.BOOK);
                                }
                                setGraphic(node);
                            }
                        } else {
                            textProperty().unbind();
                            textProperty().set("");
                            setContextMenu(null);
                            setGraphic(null);
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
        MainController.focusedItemProperty.addListener((o, oldValue, newValue) -> selectItem(newValue));
    }

    private void selectItem(Item newValue) {
        if (newValue == null) { return; }
        Class itemClass = newValue.getClass();
        if (itemClass == Project.class || itemClass == Backlog.class || itemClass == Story.class ||
                itemClass == Release.class || itemClass == TreeNodeHeading.class) {
            tabViewPane.getSelectionModel().select(projectTab);
            projectTreeView.getSelectionModel().select(getTreeViewItem(newValue, projectTreeView.getRoot()));
            projectTreeView.scrollTo(projectTreeView.getSelectionModel().getSelectedIndex());
        } else if (itemClass == Team.class) {
            tabViewPane.getSelectionModel().select(teamsTab);
            teamsListView.getSelectionModel().select((Team)newValue);
            teamsListView.scrollTo(teamsListView.getSelectionModel().getSelectedIndex());
        } else if (itemClass == Person.class) {
            tabViewPane.getSelectionModel().select(peopleTab);
            peopleListView.getSelectionModel().select((Person)newValue);
            peopleListView.scrollTo(peopleListView.getSelectionModel().getSelectedIndex());
        } else if (itemClass == Skill.class) {
            tabViewPane.getSelectionModel().select(skillsTab);
            skillsListView.getSelectionModel().select((Skill)newValue);
            skillsListView.scrollTo(skillsListView.getSelectionModel().getSelectedIndex());
        } else if (itemClass == Sprint.class) {
            tabViewPane.getSelectionModel().select(projectTab);
            projectTreeView.getSelectionModel().select(getTreeViewItem(newValue, projectTreeView.getRoot()));
            projectTreeView.scrollTo(projectTreeView.getSelectionModel().getSelectedIndex());
        }
    }

    private TreeItem<Item> getTreeViewItem(Item item, TreeItem<Item> treeItem) {
        if (treeItem.getValue() == item) {
            return treeItem;
        }
        for (TreeItem<Item> treeItem1 : treeItem.getChildren()) {
            TreeItem<Item> treeItem2 = getTreeViewItem(item, treeItem1);
            if (treeItem2 != null) {
                return treeItem2;
            }
        }
        return null;
    }

    public enum TabOption {
        PROJECTS,
        PEOPLE,
        TEAMS,
        SKILLS
    }
}

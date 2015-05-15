package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;


/**
 * Created by samschofield and James on 14/05/15.
 */
public class SideBarController implements Initializable {
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
    private Map<String, Control> tabListViewMap = new HashMap<>();
    private Control selectedListView;
    private ObjectProperty<Organisation> organisationProperty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // do nothing
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

        tabViewPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedListView = tabListViewMap.get(newValue.getId());

            if (selectedListView.getClass() != TreeView.class) {
                ListView<Item> castedListView = ((ListView<Item>) selectedListView);
                if (castedListView.getSelectionModel().getSelectedItem() == null) {
                    castedListView.getSelectionModel().selectFirst();
                }
                mainController.focusedItemProperty.set(castedListView.getSelectionModel().getSelectedItem());
                setListViewListener();
            }
        });
    }

    private void initializeProjectTreeView() {
        projectTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldValue1, newValue1) -> {
            System.out.println(newValue1);
            mainController.focusedItemProperty.set(newValue1.getValue());
            // TODO fix brokeness
        });

    }

    private void initializeListViews() {
        setListViewData();
        TreeItem<Item> root = new TreeItem<>();
        projectTreeView.setRoot(root);
        projectTreeView.setShowRoot(false);
        root.setExpanded(true);
        for (Project project : mainController.getSelectedOrganisationProperty().get().getProjects()) {
            System.out.println(project.getShortName());
            root.getChildren().add(new TreeItem<>(project));
        }

        // Get a list of them
        final ArrayList<ListView<? extends Item>> listViews = new ArrayList<>();
        listViews.add(peopleListView);
        listViews.add(skillsListView);
        listViews.add(teamsListView);

        // All these ListViews share a single context menu
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit");
        final MenuItem deleteContextMenu = new MenuItem("Delete");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);
        editContextMenu.setOnAction(event -> mainController.editItem());
        deleteContextMenu.setOnAction(event -> mainController.deleteItem());

        for (final ListView<? extends Item> listView : listViews) {
            initialiseListView(listView, contextMenu);
        }
        setListViewListener();
    }

    /**
     * sets a change listener on the selected item of the selected listView
     */
    private void setListViewListener() {
        Tab selectedTab = tabViewPane.getSelectionModel().getSelectedItem();
        if (tabListViewMap.get(selectedTab.getId()).getClass() != TreeView.class) {
            ListView<Item> castedListView = (ListView<Item>)tabListViewMap.get(selectedTab.getId());
            castedListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                mainController.focusedItemProperty.set(newValue);
            });
        }
    }

    /**
     * Attaches cell factory and selection listener to the list view.
     */
    private <T extends Item> void initialiseListView(ListView<T> listView, ContextMenu contextMenu) {
        // derived from example at
        // http://docs.oracle.com/javafx/2/api/javafx/scene/control/Cell.html
        listView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(final ListView<T> arg0) {
                final ListCell<T> listCell = new ListCell<T>() {
                    @Override
                    protected void updateItem(final T item, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getShortName());
                        if (item != null) {
                            item.shortNameProperty().addListener((observable, oldValue, newValue) -> {
                                setText(newValue);
                            });
                            setContextMenu(contextMenu);
                        }
                    }
                };
                return listCell;
            }
        });
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
                        } else {
                            textProperty().unbind();
                            textProperty().set("");
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });
        addProjectsToTree(mainController.getSelectedOrganisationProperty().get().getProjects());

        mainController.getSelectedOrganisationProperty().get().getProjects().addListener(new ListChangeListener<Project>() {
            @Override
            public void onChanged(Change<? extends Project> c) {
                c.next();
                addProjectsToTree(c.getAddedSubList());
                removeProjectsFromTree(c.getRemoved());
            }
        });



        peopleListView.setItems(mainController.selectedOrganisationProperty.get().getPeople());
        teamsListView.setItems(mainController.selectedOrganisationProperty.get().getTeams());
        skillsListView.setItems(mainController.selectedOrganisationProperty.get().getSkills());

        switchToProjectList();
    }

    private void removeProjectsFromTree(List<? extends Project> removedProjects) {
        Iterator<TreeItem<Item>> i = projectTreeView.getRoot().getChildren().iterator();
        while (i.hasNext()) {
            TreeItem<Item> projectTreeItem = i.next();
            if (removedProjects.contains(projectTreeItem.getValue())) {
                projectTreeItem.getParent().getChildren().remove(projectTreeItem);
            }
        }

    }

    private void addProjectsToTree(List<? extends Project> projects) {
        for (Project project : projects) {
            TreeItem<Item> projectItem = new TreeItem<>(project);
            projectTreeView.getRoot().getChildren().add(projectItem);

            TreeItem<Item> releaseRootItem = new TreeItem<>();
            releaseRootItem.setGraphic(new Label("Releases"));
            projectItem.getChildren().add(releaseRootItem);

            for (Release release : project.getReleases()) {
                TreeItem<Item> releaseItem = new TreeItem<>(release);
                releaseRootItem.getChildren().add(releaseItem);
            }
        }
    }

    public void switchToSkillList() {
        tabViewPane.getSelectionModel().select(skillsTab);
    }

    public void switchToPersonList() {
        tabViewPane.getSelectionModel().select(peopleTab);
    }

    public void switchToTeamList() {
        tabViewPane.getSelectionModel().select(teamsTab);
    }

    public void switchToProjectList() {
        tabViewPane.getSelectionModel().select(projectTab);
    }


    public void setOrganisationProperty(ObjectProperty<Organisation> organisationProperty) {
        this.organisationProperty = organisationProperty;
    }

    /**
     * Set the main controller and initialise the tabs and listviews
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        initialiseTabs();
        initializeListViews();
        initializeProjectTreeView();
        mainController.selectedOrganisationProperty.addListener((o, oldValue, newValue) -> setListViewData());
    }
}
package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by samschofield and James on 14/05/15.
 */
public class SideBarController implements Initializable {
    @FXML
    private ListView<Project> projectListView;
    @FXML
    private ListView<Person> peopleListView;
    @FXML
    private ListView<Skill> skillsListView;
    @FXML
    private ListView<Team> teamsListView;
    @FXML
    private ListView<Release> releasesListView;
    @FXML
    private Tab projectTab;
    @FXML
    private Tab peopleTab;
    @FXML
    private Tab skillsTab;
    @FXML
    private Tab teamsTab;
    @FXML
    private Tab releasesTab;
    @FXML
    private TabPane tabViewPane;

    private MainController mainController;
    private Map<String, ListView<? extends Item>> tabListViewMap = new HashMap<>();
    private ListView<? extends Item> selectedListView;
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
        tabListViewMap.put(projectTab.getId(), projectListView);
        tabListViewMap.put(teamsTab.getId(), teamsListView);
        tabListViewMap.put(peopleTab.getId(), peopleListView);
        tabListViewMap.put(skillsTab.getId(), skillsListView);
        tabListViewMap.put(releasesTab.getId(), releasesListView);

        tabViewPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedListView = tabListViewMap.get(newValue.getId());

            if (selectedListView.getSelectionModel().getSelectedItem() == null) {
                selectedListView.getSelectionModel().selectFirst();
            }
            mainController.focusedItemProperty.set(selectedListView.getSelectionModel().getSelectedItem());
            setListViewListener();
        });
    }

    private void initializeListViews() {
        setListViewData();

        // Get a list of them
        final ArrayList<ListView<? extends Item>> listViews = new ArrayList<>();
        listViews.add(projectListView);
        listViews.add(peopleListView);
        listViews.add(skillsListView);
        listViews.add(teamsListView);
        listViews.add(releasesListView);

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
        tabListViewMap.get(selectedTab.getId()).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mainController.focusedItemProperty.set(newValue);
        });
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
        projectListView.setItems(mainController.selectedOrganisationProperty.get().getProjects());
        peopleListView.setItems(mainController.selectedOrganisationProperty.get().getPeople());
        teamsListView.setItems(mainController.selectedOrganisationProperty.get().getTeams());
        skillsListView.setItems(mainController.selectedOrganisationProperty.get().getSkills());
        // releases are looked after by projectListView selectionChangeListener

        switchToProjectList();
        projectListView.getSelectionModel().select(0);
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

    public void switchToReleaseList() {
        tabViewPane.getSelectionModel().select(releasesTab);
    }

    public void setSelectedTab(int tab) {
        switch (tab) {
            case 0:
                tabViewPane.getSelectionModel().select(projectTab);
                break;
            case 1:
                tabViewPane.getSelectionModel().select(teamsTab);
                break;
            case 2:
                tabViewPane.getSelectionModel().select(peopleTab);
                break;
            case 3:
                tabViewPane.getSelectionModel().select(skillsTab);
                break;
            case 4:
                tabViewPane.getSelectionModel().select(releasesTab);
                break;
        }
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
        mainController.selectedOrganisationProperty.addListener((o, oldValue, newValue) -> setListViewData());
    }
}
package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.UndoManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuBarController implements Initializable {
    @FXML
    private MenuItem newOrganisationMenuItem;
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem newTeamMenuItem;
    @FXML
    private MenuItem newPersonMenuItem;
    @FXML
    private MenuItem newSkillMenuItem;
    @FXML
    private MenuItem newReleaseMenuItem;
    @FXML
    private MenuItem newBacklogMenuItem;
    @FXML
    private MenuItem newStoryMenuItem;
    @FXML
    private MenuItem revertMenuItem;
    @FXML
    private MenuItem generateStatusReportMenuItem;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem editMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private RadioMenuItem listShowProjectsMenuItem;
    @FXML
    private RadioMenuItem listShowTeamsMenuItem;
    @FXML
    private RadioMenuItem listShowPeopleMenuItem;
    @FXML
    private RadioMenuItem listShowSkillsMenuItem;
    @FXML
    private ToggleGroup selectedTab;
    @FXML
    private MenuItem quitMenuItem;
    private MainController mainController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        addMenuItemHandlers();
        addKeyboardShortcuts();
    }

    /**
     * Disables menu buttons which should not be usable when application is initially started.
     */
    private void setMenuButtons() {
        newTeamMenuItem.setDisable(false);
        newPersonMenuItem.setDisable(false);
        newSkillMenuItem.setDisable(false);

        revertMenuItem.disableProperty().bind(mainController.changesSavedProperty());

        editMenuItem.disableProperty().bind(Bindings.isNull(mainController.focusedItemProperty));
        deleteMenuItem.disableProperty().bind(Bindings.isNull(mainController.focusedItemProperty));

        mainController.getSideBarController().selectedTabProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == SideBarController.TabOption.PROJECTS) {
                selectedTab.selectToggle(listShowProjectsMenuItem);
            } else if (newValue == SideBarController.TabOption.TEAMS) {
                selectedTab.selectToggle(listShowTeamsMenuItem);
            } else if (newValue == SideBarController.TabOption.PEOPLE) {
                selectedTab.selectToggle(listShowPeopleMenuItem);
            } else if (newValue == SideBarController.TabOption.SKILLS) {
                selectedTab.selectToggle(listShowSkillsMenuItem);
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMenuButtons();
    }

    private void addMenuItemHandlers() {
        newOrganisationMenuItem.setOnAction(event -> mainController.newOrganisation());
        newProjectMenuItem.setOnAction(event -> mainController.newProject());
        newReleaseMenuItem.setOnAction(event -> mainController.newRelease());
        newTeamMenuItem.setOnAction(event -> mainController.newTeam());
        newPersonMenuItem.setOnAction(event -> mainController.newPerson());
        newSkillMenuItem.setOnAction(event -> mainController.newSkill());
        newBacklogMenuItem.setOnAction(event -> mainController.newBacklog());
        newStoryMenuItem.setOnAction(event -> mainController.newStory());

        undoMenuItem.setOnAction(event -> mainController.undo());
        redoMenuItem.setOnAction(event -> mainController.redo());
        revertMenuItem.setOnAction(event -> mainController.promptBeforeRevert());

        generateStatusReportMenuItem.setOnAction(event -> mainController.statusReport());
        openMenuItem.setOnAction(event -> mainController.openOrganisation(null));
        saveMenuItem.setOnAction(event -> mainController.saveOrganisation(false));
        saveAsMenuItem.setOnAction(event -> mainController.saveOrganisation(true));
        quitMenuItem.setOnAction(event -> mainController.exit());

        editMenuItem.setOnAction(event -> mainController.editItem());
        deleteMenuItem.setOnAction(event -> mainController.deleteItem());


        listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mainController.setListVisible(newValue);
        });

        selectedTab.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == listShowProjectsMenuItem) {
                mainController.getSideBarController().show(SideBarController.TabOption.PROJECTS);
            } else if (newValue == listShowTeamsMenuItem) {
                mainController.getSideBarController().show(SideBarController.TabOption.TEAMS);
            } else if (newValue == listShowPeopleMenuItem) {
                mainController.getSideBarController().show(SideBarController.TabOption.PEOPLE);
            } else if (newValue == listShowSkillsMenuItem) {
                mainController.getSideBarController().show(SideBarController.TabOption.SKILLS);
            }
        });
    }

    /**
     * Sets the keyboard shortcuts for MenuItems
     */
    private void addKeyboardShortcuts() {
        newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newTeamMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN));
        newPersonMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        newSkillMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.K, KeyCombination.SHORTCUT_DOWN));
        newReleaseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        newBacklogMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN));
        newStoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        listToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));

        // Undo/redo
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));

        // Tab switching
        listShowProjectsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN));
        listShowTeamsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN));
        listShowPeopleMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN));
        listShowSkillsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN));

        // Edit/delete buttons
        editMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
    }

    public void setListenersOnUndoManager(UndoManager undoManager) {
        undoMenuItem.textProperty().bind(Bindings.concat("Undo ", undoManager.undoTypeProperty));
        redoMenuItem.textProperty().bind(Bindings.concat("Redo ", undoManager.redoTypeProperty));

        undoMenuItem.disableProperty().bind(Bindings.equal("", undoManager.undoTypeProperty));
        redoMenuItem.disableProperty().bind(Bindings.equal("", undoManager.redoTypeProperty));
    }
}
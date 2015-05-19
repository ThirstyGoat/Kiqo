package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.UndoManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

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
    private CheckMenuItem listShowProjectMenuItem;
    @FXML
    private CheckMenuItem listShowTeamMenuItem;
    @FXML
    private CheckMenuItem listShowPersonMenuItem;
    @FXML
    private CheckMenuItem listShowSkillMenuItem;
    @FXML
    private MenuItem quitMenuItem;


    private MainController mainController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        addMenuItemHandlers();
        addUndoHandlers();
        addKeyboardShortcuts();
        setMenuButtons();
    }

    /**
     * Disables menu buttons which should not be usable when application is initially started.
     */
    private void setMenuButtons() {
        newTeamMenuItem.setDisable(true);
        newPersonMenuItem.setDisable(true);
        newSkillMenuItem.setDisable(true);
        undoMenuItem.setDisable(true);
        redoMenuItem.setDisable(true);
        editMenuItem.setDisable(true);
        deleteMenuItem.setDisable(true);

        // listShowProjectMenuItem is disabled here, because it is the default list view.
        listShowProjectMenuItem.setDisable(true);
        listShowProjectMenuItem.setSelected(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        addEditDeleteShortcuts();
    }

    private void addEditDeleteShortcuts() {
        final KeyCombination editKey = new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination deleteKey = new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination projectTabKey = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination teamTabKey = new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination peopleTabKey = new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination skillTabKey = new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN);

        mainController.getPrimaryStage().getScene().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (editKey.match(event)) {
                    mainController.editItem();
                } else if (deleteKey.match(event)) {
                    mainController.deleteItem();
                } else if (projectTabKey.match(event)) {
                    mainController.getSideBarController().show(SideBarController.TabOption.PROJECTS);
                } else if (teamTabKey.match(event)) {
                    mainController.getSideBarController().show(SideBarController.TabOption.TEAMS);
                } else if (peopleTabKey.match(event)) {
                    mainController.getSideBarController().show(SideBarController.TabOption.PEOPLE);
                } else if (skillTabKey.match(event)) {
                    mainController.getSideBarController().show(SideBarController.TabOption.SKILLS);
                }
            }
        });
    }

    private void addMenuItemHandlers() {
        newProjectMenuItem.setOnAction(event -> mainController.newProject());
        newOrganisationMenuItem.setOnAction(event -> mainController.newOrganisation());
        newTeamMenuItem.setOnAction(event -> mainController.newTeam());
        newPersonMenuItem.setOnAction(event -> mainController.newPerson());
        newSkillMenuItem.setOnAction(event -> mainController.newSkill());
        newReleaseMenuItem.setOnAction(event -> mainController.newRelease());
        generateStatusReportMenuItem.setOnAction(event -> mainController.saveStatusReport());
        openMenuItem.setOnAction(event -> mainController.openOrganisation(null));
        saveMenuItem.setOnAction(event -> mainController.saveOrganisation());
        saveAsMenuItem.setOnAction(event -> mainController.saveAsOrganisation());
        quitMenuItem.setOnAction(event -> mainController.exit());

        editMenuItem.setOnAction(event -> mainController.editItem());
        deleteMenuItem.setOnAction(event -> mainController.deleteItem());

        listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mainController.setListVisible(newValue);
        });
        listShowProjectMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowTeamMenuItem.setSelected(false);
                listShowTeamMenuItem.setDisable(false);
                listShowPersonMenuItem.setSelected(false);
                listShowPersonMenuItem.setDisable(false);
                listShowSkillMenuItem.setSelected(false);
                listShowSkillMenuItem.setDisable(false);

                mainController.getSideBarController().show(SideBarController.TabOption.PROJECTS);
                listShowProjectMenuItem.setDisable(true);
            }
        });
        listShowTeamMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowProjectMenuItem.setSelected(false);
                listShowProjectMenuItem.setDisable(false);
                listShowPersonMenuItem.setSelected(false);
                listShowPersonMenuItem.setDisable(false);
                listShowSkillMenuItem.setSelected(false);
                listShowSkillMenuItem.setDisable(false);

                mainController.getSideBarController().show(SideBarController.TabOption.TEAMS);
                listShowTeamMenuItem.setDisable(true);
                listShowTeamMenuItem.setSelected(true);
            }
        });
        listShowPersonMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowProjectMenuItem.setSelected(false);
                listShowProjectMenuItem.setDisable(false);
                listShowTeamMenuItem.setSelected(false);
                listShowTeamMenuItem.setDisable(false);
                listShowSkillMenuItem.setSelected(false);
                listShowSkillMenuItem.setDisable(false);

                mainController.getSideBarController().show(SideBarController.TabOption.PEOPLE);
                listShowPersonMenuItem.setDisable(true);
                listShowPersonMenuItem.setSelected(true);
            }
        });
        listShowSkillMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowProjectMenuItem.setSelected(false);
                listShowProjectMenuItem.setDisable(false);
                listShowTeamMenuItem.setSelected(false);
                listShowTeamMenuItem.setDisable(false);
                listShowPersonMenuItem.setSelected(false);
                listShowPersonMenuItem.setDisable(false);

                mainController.getSideBarController().show(SideBarController.TabOption.SKILLS);
                listShowSkillMenuItem.setDisable(true);
            }
        });
    }

    /**
     * Set the undo/redo item handlers, and also set their state depending on
     * the undoManager.
     */
    private void addUndoHandlers() {
        undoMenuItem.setOnAction(event -> mainController.undo());

        redoMenuItem.setOnAction(event -> mainController.redo());
    }

    /**
     * Sets the hotkeys
     */
    private void addKeyboardShortcuts() {
        newProjectMenuItem.setAccelerator(
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newTeamMenuItem.setAccelerator(
            new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN));
        newPersonMenuItem.setAccelerator(
            new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        newSkillMenuItem.setAccelerator(
            new KeyCodeCombination(KeyCode.K, KeyCombination.SHORTCUT_DOWN));
        newReleaseMenuItem.setAccelerator(
                new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        listToggleCheckMenuItem.setAccelerator(
                new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));

        // Undo/redo
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN));
    }

    public void setListenersOnUndoManager(UndoManager undoManager) {
        undoManager.canUndoProperty.addListener((observable, oldValue, newValue) -> {
            undoMenuItem.setDisable(!newValue);
            if (newValue) {
                // Update text to say (eg. Undo 'Create Project')
                undoMenuItem.setText("Undo " + undoManager.getUndoType());
            } else {
                undoMenuItem.setText("Undo");
            }
        });

        undoManager.canRedoProperty.addListener((observable, oldValue, newValue) -> {
            redoMenuItem.setDisable(!newValue);
            if (newValue) {
                // Update text to say (eg. Redo 'Create Project');
                redoMenuItem.setText("Redo " + undoManager.getRedoType());
            } else {
                redoMenuItem.setText("Redo");
                if (undoManager.canUndoProperty.get()) {
                    undoMenuItem.setText("Undo " + undoManager.getUndoType());
                }
            }
        });

        undoManager.shouldUpdateMenuProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (undoManager.canUndoProperty.get()) {
                    undoMenuItem.setText("Undo " + undoManager.getUndoType());
                }
                if (undoManager.canRedoProperty.get()) {
                    redoMenuItem.setText("Redo " + undoManager.getRedoType());
                }
                undoManager.shouldUpdateMenuProperty.set(false);
            }
        });
    }

    public void updateAfterAnyObjectSelected(boolean enabled) {
        editMenuItem.setDisable(!enabled);
        deleteMenuItem.setDisable(!enabled);
    }

    public void updateAfterProjectListSelected(boolean selected) {
        listShowProjectMenuItem.selectedProperty().set(selected);
    }

    public void updateAfterTeamListSelected(boolean selected) {
        listShowTeamMenuItem.selectedProperty().set(selected);
    }

    public void updateAfterPersonListSelected(boolean selected) {
        listShowPersonMenuItem.selectedProperty().set(selected);
    }

    public void updateAfterSkillListSelected(boolean selected) {
        listShowSkillMenuItem.selectedProperty().set(selected);
    }


    public void enableNewTeam() {
        newTeamMenuItem.setDisable(false);
    }

    public void enableNewPerson() {
        newPersonMenuItem.setDisable(false);
    }

    public void enableNewSkill() {
        newSkillMenuItem.setDisable(false);
    }


}

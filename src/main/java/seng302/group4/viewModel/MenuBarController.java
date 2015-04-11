package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import seng302.group4.undo.UndoManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuBarController implements Initializable {
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
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem editProjectMenuItem;
    @FXML
    private MenuItem editTeamMenuItem;
    @FXML
    private MenuItem editPersonMenuItem;
    @FXML
    private MenuItem editSkillMenuItem;
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
    private CheckMenuItem listShowReleaseMenuItem;
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
        newReleaseMenuItem.setDisable(true);
        saveMenuItem.setDisable(true);
        undoMenuItem.setDisable(true);
        redoMenuItem.setDisable(true);
        editProjectMenuItem.setDisable(true);
        editTeamMenuItem.setDisable(true);
        editPersonMenuItem.setDisable(true);
        editSkillMenuItem.setDisable(true);

        // listShowProjectMenuItem is disabled here, because it is the default list view.
        listShowProjectMenuItem.setDisable(true);
        listShowProjectMenuItem.setSelected(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void addMenuItemHandlers() {
        newProjectMenuItem.setOnAction(event -> mainController.newProject());
        newTeamMenuItem.setOnAction(event -> mainController.newTeam());
        newPersonMenuItem.setOnAction(event -> mainController.newPerson());
        newSkillMenuItem.setOnAction(event -> mainController.newSkill());
        newReleaseMenuItem.setOnAction(event -> mainController.newRelease());
        openMenuItem.setOnAction(event -> mainController.openProject());
        saveMenuItem.setOnAction(event -> mainController.saveProject());
        quitMenuItem.setOnAction(event -> mainController.exit());

        editProjectMenuItem.setOnAction(event -> mainController.editProject());
        editTeamMenuItem.setOnAction(event -> mainController.editTeam());
        editPersonMenuItem.setOnAction(event -> mainController.editPerson());
        editSkillMenuItem.setOnAction(event-> mainController.editSkill());

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
                listShowReleaseMenuItem.setSelected(false);
                listShowReleaseMenuItem.setDisable(false);

                mainController.switchToProjectList();
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
                listShowReleaseMenuItem.setSelected(false);
                listShowReleaseMenuItem.setDisable(false);

                mainController.switchToTeamList();
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
                listShowReleaseMenuItem.setSelected(false);
                listShowReleaseMenuItem.setDisable(false);

                mainController.switchToPersonList();
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
                listShowReleaseMenuItem.setSelected(false);
                listShowReleaseMenuItem.setDisable(false);

                mainController.switchToSkillList();
                listShowSkillMenuItem.setDisable(true);
            }
        });
        listShowReleaseMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowProjectMenuItem.setSelected(false);
                listShowProjectMenuItem.setDisable(false);
                listShowTeamMenuItem.setSelected(false);
                listShowTeamMenuItem.setDisable(false);
                listShowPersonMenuItem.setSelected(false);
                listShowPersonMenuItem.setDisable(false);
                listShowSkillMenuItem.setSelected(false);
                listShowSkillMenuItem.setDisable(false);

                mainController.switchToReleaseList();
                listShowReleaseMenuItem.setDisable(true);
            }
        });
    }

    /**
     * Set the undo/redo item handlers, and also set their state depending on
     * the undoManager.
     */
    private void addUndoHandlers() {
        undoMenuItem.setOnAction(event -> {
            mainController.undo();
        });

        redoMenuItem.setOnAction(event -> {
            mainController.redo();
        });
    }

    /**
     * Sets the hotkeys
     */
    private void addKeyboardShortcuts() {
        newProjectMenuItem.setAccelerator(
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newReleaseMenuItem.setAccelerator(
                new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        newTeamMenuItem.setAccelerator(
            new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN));
        newPersonMenuItem.setAccelerator(
            new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        newSkillMenuItem.setAccelerator(
            new KeyCodeCombination(KeyCode.K, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
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

    public void updateAfterProjectSelected(boolean selected) {
        // disable things
        editTeamMenuItem.setDisable(selected);
        editPersonMenuItem.setDisable(selected);
        editSkillMenuItem.setDisable(selected);

        // enable things
        editProjectMenuItem.setDisable(!selected);
        saveMenuItem.setDisable(!selected);
    }

    public void updateAfterTeamSelected(boolean selected) {
        // disable things
        editProjectMenuItem.setDisable(selected);
        editPersonMenuItem.setDisable(selected);
        editSkillMenuItem.setDisable(selected);

        // enable things
        editTeamMenuItem.setDisable(!selected);
    }

    public void updateAfterPersonSelected(boolean selected) {
        // disable things
        editProjectMenuItem.setDisable(selected);
        editTeamMenuItem.setDisable(selected);
        editSkillMenuItem.setDisable(selected);

        // enable things
        editPersonMenuItem.setDisable(!selected);
    }

    public void updateAfterSkillSelected(boolean selected) {
        // disable things
        editProjectMenuItem.setDisable(selected);
        editTeamMenuItem.setDisable(selected);
        editPersonMenuItem.setDisable(selected);

        // enable things
        editSkillMenuItem.setDisable(!selected);
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

    public void updateAfterReleasesListSelected(boolean selected) {
        listShowReleaseMenuItem.selectedProperty().set(selected);
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

    public void enableNewRelease() {
        newReleaseMenuItem.setDisable(false);
    }
}

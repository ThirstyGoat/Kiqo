package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import seng302.group4.undo.UndoManager;

public class MenuBarController implements Initializable {
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem newPersonMenuItem;
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
    private MenuItem editPersonMenuItem;
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private CheckMenuItem listShowProjectMenuItem;
    @FXML
    private CheckMenuItem listShowPersonMenuItem;
    @FXML
    private MenuItem quitMenuItem;

    private MainController mainController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        addMenuItemHandlers();
        addUndoHandlers();
        addKeyboardShortcuts();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void addMenuItemHandlers() {
        newProjectMenuItem.setOnAction(event -> {
            mainController.newProject();
        });
        newPersonMenuItem.setOnAction(event -> {
            mainController.newPerson();
        });
        openMenuItem.setOnAction(event -> {
            mainController.openProject();
        });
        saveMenuItem.setOnAction(event -> {
            mainController.saveProject();
        });
        quitMenuItem.setOnAction(event -> {
            mainController.exit();
        });

        editProjectMenuItem.setOnAction(event -> {
            mainController.editProject();
        });
        editPersonMenuItem.setOnAction(event -> {
            mainController.editPerson();
        });

        listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mainController.setListVisible(newValue);
        });
        listShowProjectMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowPersonMenuItem.setSelected(false);
                mainController.switchToProjectList();
                listShowProjectMenuItem.setDisable(true);
                listShowPersonMenuItem.setDisable(false);
            }
        });
        listShowPersonMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listShowProjectMenuItem.setSelected(false);
                mainController.switchToPersonList();
                listShowProjectMenuItem.setDisable(false);
                listShowPersonMenuItem.setDisable(true);
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
        newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newPersonMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        listToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));

        // Undo/redo
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
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
        updateAfterPersonSelected(false);
        newPersonMenuItem.setDisable(!selected);
        editProjectMenuItem.setDisable(!selected);
        saveMenuItem.setDisable(!selected);
    }

    public void updateAfterPersonSelected(boolean selected) {
        editPersonMenuItem.setDisable(!selected);
        editProjectMenuItem.setDisable(selected);
    }

    public void updateAfterPersonListSelected() {
        listShowPersonMenuItem.selectedProperty().set(true);
    }
}

package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.UndoManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;


public class ToolBarController implements Initializable {
    private MainController mainController;
    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button newButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button reportButton;
    @FXML
    private ToolBar toolBar;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeButtons();
        addMenuItemHandlers();
        setListenersOnUndoManager();

    }

    private void initializeButtons() {
        Button projectButton = new Button();
        projectButton.setOnAction(event -> {
            mainController.newProject();
        });
        Button personButton = new Button();
        personButton.setOnAction(event -> {
            mainController.newPerson();
        });
        Button skillButton = new Button();
        skillButton.setOnAction(event -> {
            mainController.newSkill();
        });
        Button teamButton = new Button();
        teamButton.setOnAction(event -> {
            mainController.newTeam();
        });
        Button releaseButton = new Button();
        releaseButton.setOnAction(event -> {
            mainController.newRelease();
        });
        Button storyButton = new Button();
        storyButton.setOnAction(event -> {
            mainController.newStory();
        });
        Button backlogButton = new Button();
        backlogButton.setOnAction(event -> {
            mainController.newBacklog();
        });
        Button sprintButton = new Button();
        sprintButton.setOnAction(event -> {
            mainController.newSprint();
        });


    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void addMenuItemHandlers() {
        undoButton.setOnAction(event -> mainController.undo());
        redoButton.setOnAction(event -> mainController.redo());

        PopOver newItemPopOver = new PopOver();
        GridPane gridpane = new GridPane();
        newItemPopOver.setContentNode((new GridPane()));
        newItemPopOver.setDetachable(false);
        newButton.setOnAction(event -> newItemPopOver.show(newButton));
        newItemPopOver.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                newItemPopOver.hide();
            }
        });

        reportButton.setOnAction(event -> mainController.statusReport());
        openButton.setOnAction(event -> mainController.openOrganisation(null));
        saveButton.setOnAction(event -> mainController.saveOrganisation(false));
    }



    private void setListenersOnUndoManager() {
        undoButton.disableProperty().bind(Bindings.equal("", UndoManager.getUndoManager().undoTypeProperty));
        redoButton.disableProperty().bind(Bindings.equal("", UndoManager.getUndoManager().redoTypeProperty));
    }

    public void setVisible(Boolean state) {
        toolBar.setVisible(state);
        toolBar.setManaged(state);
    }
}
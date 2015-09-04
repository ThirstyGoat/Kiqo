package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.UndoManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        HBox projectButton = new HBox();
        projectButton.getChildren().addAll(new Label("Project"));
        projectButton.setOnMouseClicked(event -> {
            mainController.newProject();
        });
        HBox personButton = new HBox();
        personButton.getChildren().addAll(new Label("Person"));
        personButton.setOnMouseClicked(event -> {
            mainController.newPerson();
        });
        HBox skillButton = new HBox();
        skillButton.getChildren().addAll(new Label("Skill"));
        skillButton.setOnMouseClicked(event -> {
            mainController.newSkill();
        });
        HBox teamButton = new HBox();
        teamButton.getChildren().addAll(new Label("Team"));
        teamButton.setOnMouseClicked(event -> {
            mainController.newTeam();
        });
        HBox releaseButton = new HBox();
        releaseButton.getChildren().addAll(new Label("Release"));
        releaseButton.setOnMouseClicked(event -> {
            mainController.newRelease();
        });
        HBox storyButton = new HBox();
        storyButton.getChildren().addAll(new Label("Story"));
        storyButton.setOnMouseClicked(event -> {
            mainController.newStory();
        });
        HBox backlogButton = new HBox();
        backlogButton.getChildren().addAll(new Label("Backlog"));
        backlogButton.setOnMouseClicked(event -> {
            mainController.newBacklog();
        });
        HBox sprintButton = new HBox();
        sprintButton.getChildren().addAll(new Label("Sprint"));
        sprintButton.setOnMouseClicked(event -> {
            mainController.newSprint();
        });

        PopOver newItemPopOver = new PopOver();
        newItemPopOver.setDetachable(false);

        HBox row1 = new HBox();
        HBox row2 = new HBox();
        HBox row3 = new HBox();

        row1.getChildren().addAll(projectButton, personButton, skillButton);
        row2.getChildren().addAll(teamButton, releaseButton, storyButton);
        row3.getChildren().addAll(backlogButton, sprintButton);

        VBox vb = new VBox();
        vb.getChildren().addAll(row1,row2,row3);

        newItemPopOver.setContentNode(vb);

        newButton.setOnAction(event -> newItemPopOver.show(newButton));
        newItemPopOver.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                newItemPopOver.hide();
            }
        });

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void addMenuItemHandlers() {
        undoButton.setOnAction(event -> mainController.undo());
        redoButton.setOnAction(event -> mainController.redo());

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
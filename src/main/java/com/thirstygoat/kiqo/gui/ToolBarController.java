package com.thirstygoat.kiqo.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

import com.thirstygoat.kiqo.command.UndoManager;


public class ToolBarController implements Initializable {
    private MainController mainController;
    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
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
        addMenuItemHandlers();
        setListenersOnUndoManager();
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
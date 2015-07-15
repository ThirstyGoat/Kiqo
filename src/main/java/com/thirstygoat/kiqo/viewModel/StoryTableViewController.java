package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Story;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * TableView for allocations of teams to projects
 */
public class StoryTableViewController implements Initializable {
    private MainController mainController;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> storyTableColumn;
    @FXML
    private TableColumn<Story, Number> priorityTableColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        storyTableView.setPlaceholder(new Label("No stories in backlog"));
    }

    private void initializeTable() {
        // center the dates
        storyTableColumn.setStyle("-fx-alignment: CENTER;");
        priorityTableColumn.setStyle("-fx-alignment: CENTER;");

        // set the cell factories
        storyTableColumn.setCellValueFactory(cellData -> cellData.getValue().shortNameProperty());
        priorityTableColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());

        // fixes ghost column issue and resizing
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    public void setItems(ObservableList<Story> items) {
        storyTableView.setItems(items);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
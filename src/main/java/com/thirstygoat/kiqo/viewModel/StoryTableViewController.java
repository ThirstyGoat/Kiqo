package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.model.Story;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * TableView for allocations of teams to projects
 */
public class StoryTableViewController implements Initializable {

    private StoryTableViewModel viewModel;

    private Label placeHolder = new Label();

    @FXML
    private TableView<StoryTableEntryViewModel> storyTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storyTableView.setPlaceholder(placeHolder);
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void bindFields() {
        placeHolder.textProperty().set(viewModel.PLACEHOLDER);
        storyTableView.setItems(viewModel.stories());
    }

    public void setViewModel(StoryTableViewModel viewModel) {
        this.viewModel = viewModel;
        bindFields();
    }
}
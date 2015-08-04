package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneView implements FxmlView<SprintDetailsPaneViewModel> {

    @InjectViewModel
    private SprintDetailsPaneViewModel sprintDetailsPaneViewModel;

    @FXML
    private Label longNameLabel;
    @FXML
    private Label goalLabel;
    @FXML
    private Label teamLabel;
    @FXML
    private Label backlogLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label releaseLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;

    private Label placeHolder = new Label();


//    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        longNameLabel.textProperty().bind(sprintDetailsPaneViewModel.longNameProperty());
        goalLabel.textProperty().bind(sprintDetailsPaneViewModel.goalProperty());
        startDateLabel.textProperty().bind(sprintDetailsPaneViewModel.startDateProperty().asString());
        endDateLabel.textProperty().bind(sprintDetailsPaneViewModel.endDateProperty().asString());
        releaseLabel.textProperty().bind(sprintDetailsPaneViewModel.releaseProperty().asString());
        descriptionLabel.textProperty().bind(sprintDetailsPaneViewModel.descriptionProperty());
        teamLabel.textProperty().bind(sprintDetailsPaneViewModel.teamProperty().asString());
        backlogLabel.textProperty().bind(sprintDetailsPaneViewModel.backlogProperty().asString());

//        shortNameTableColumn.setCellFactory(param -> new StoryListCell(sprintDetailsPaneViewModel));
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(sprintDetailsPaneViewModel.getStories());

        placeHolder.textProperty().set(sprintDetailsPaneViewModel.PLACEHOLDER);

    }



    }

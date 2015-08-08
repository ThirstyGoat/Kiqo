package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneView implements FxmlView<SprintDetailsPaneViewModel>, Initializable {
    
    private Label placeHolder = new Label();
    
    @InjectViewModel
    private SprintDetailsPaneViewModel viewModel;

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

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        longNameLabel.textProperty().bind(viewModel.longNameProperty());
        goalLabel.textProperty().bind(viewModel.goalProperty());
        startDateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (viewModel.startDateProperty().get() != null) {
                return viewModel.startDateProperty().get().format(datetimeFormat);
            }
            return "";
        }, viewModel.startDateProperty()));
        endDateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (viewModel.endDateProperty().get() != null) {
                return viewModel.endDateProperty().get().format(datetimeFormat);
            }
            return "";
        }, viewModel.endDateProperty()));
        releaseLabel.textProperty().bind(viewModel.releaseShortNameProperty());
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
        teamLabel.textProperty().bind(viewModel.teamShortNameProperty());
        backlogLabel.textProperty().bind(viewModel.backlogShortNameProperty());

//        shortNameTableColumn.setCellFactory(param -> new StoryListCell(viewModel));
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(viewModel.stories());

        placeHolder.textProperty().set(SprintDetailsPaneViewModel.PLACEHOLDER);
    }
}

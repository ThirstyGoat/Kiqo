package com.thirstygoat.kiqo.viewModel.detailsPane;

import com.thirstygoat.kiqo.viewModel.StoryTableEntryViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;


//@Override
//public void initialize(URL location, ResourceBundle resources) {
//        storyTableView.setPlaceholder(placeHolder);
//        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        }

//public void bindFields() {
//        placeHolder.textProperty().set(viewModel.PLACEHOLDER);
//        storyTableView.setItems(viewModel.stories());
//        }

/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneView implements FxmlView<BacklogDetailsPaneViewModel>, Initializable {

    @InjectViewModel
    private BacklogDetailsPaneViewModel backlogDetailsPaneViewModel;
    
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label productOwnerLabel;
    @FXML
    private Label scaleLabel;
    @FXML
    private TableView<StoryTableEntryViewModel> storyTableView;

    private Label placeHolder = new Label();


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) { 

        shortNameLabel.textProperty().bind(backlogDetailsPaneViewModel.shortNameProperty());
        longNameLabel.textProperty().bind(backlogDetailsPaneViewModel.longNameProperty());
        descriptionLabel.textProperty().bind(backlogDetailsPaneViewModel.descriptionProperty());
        productOwnerLabel.textProperty().bind(backlogDetailsPaneViewModel.productOwnerStringProperty());
        scaleLabel.textProperty().bind(backlogDetailsPaneViewModel.scaleStringProperty());



//        storyTableViewModel.setStories(viewModel.stories());
//        storyTableViewController.setViewModel(storyTableViewModel);

        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(backlogDetailsPaneViewModel.tableViewStories());

        placeHolder.textProperty().set(backlogDetailsPaneViewModel.PLACEHOLDER);

        scaleLabel.textProperty().bind(backlogDetailsPaneViewModel.scaleProperty().asString());
    }
}

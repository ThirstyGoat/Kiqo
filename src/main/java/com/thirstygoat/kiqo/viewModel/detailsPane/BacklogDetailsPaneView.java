package com.thirstygoat.kiqo.viewModel.detailsPane;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.viewModel.StoryTableViewController;
import com.thirstygoat.kiqo.viewModel.StoryTableViewModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;


/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneView implements FxmlView<BacklogDetailsPaneViewModel>, Initializable {
    private StoryTableViewModel storyTableViewModel;
    
    @InjectViewModel
    private BacklogDetailsPaneViewModel viewModel;
    
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
    private StoryTableViewController storyTableViewController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) { 
        storyTableViewModel = new StoryTableViewModel();
        
        shortNameLabel.textProperty().bind(viewModel.shortNameProperty());
        longNameLabel.textProperty().bind(viewModel.longNameProperty());
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
        productOwnerLabel.textProperty().bind(viewModel.productOwnerStringProperty());
        scaleLabel.textProperty().bind(viewModel.scaleStringProperty());
        
        storyTableViewModel.setStories(viewModel.stories());
        storyTableViewController.setViewModel(storyTableViewModel);
        
        scaleLabel.textProperty().bind(viewModel.scaleProperty().asString());
    }
}

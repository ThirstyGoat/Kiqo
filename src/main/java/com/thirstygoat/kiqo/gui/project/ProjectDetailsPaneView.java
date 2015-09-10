package com.thirstygoat.kiqo.gui.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.fxml.*;
import javafx.scene.control.Button;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.util.FxUtils;

import de.saxsys.mvvmfx.*;

/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class ProjectDetailsPaneView implements FxmlView<ProjectDetailsPaneViewModel>, Initializable {

    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private Button allocateTeamButton;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private ProjectDetailsPaneViewModel viewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL arg0, ResourceBundle arg1) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(),
                viewModel.shortNameValidation());
        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation(), "Add a description...");

        // Using the traditional controller for the allocations table, allocations might be null initially. Therefore,
        // a listener is setup to set the items only when allocations is not null.
        viewModel.allocations().addListener((ListChangeListener) change -> {
            if (viewModel.allocations().get() != null) {
                allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.TEAM);
                allocationsTableViewController.setMainController(viewModel.mainControllerProperty().get());
                allocationsTableViewController.setItems(viewModel.allocations());
            }
        });
    }
}

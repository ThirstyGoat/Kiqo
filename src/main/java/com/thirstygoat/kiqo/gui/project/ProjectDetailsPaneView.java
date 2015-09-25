package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.gui.nodes.AllocationsTableViewController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

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
                viewModel.descriptionValidation());

        // Using the traditional controller for the allocations table, allocations might be null initially. Therefore,
        // a listener is setup to set the items only when allocations is not null.
        viewModel.allocations().addListener((ListChangeListener.Change<? extends Allocation> change) -> {
            if (viewModel.allocations().get() != null) {
                allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.TEAM, viewModel.getWrappedObject());
                allocationsTableViewController.setMainController(viewModel.mainControllerProperty().get());
                allocationsTableViewController.setItems(viewModel.allocations());
            }
        });
    }
}

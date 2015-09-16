package com.thirstygoat.kiqo.gui.team;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.util.Callback;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.util.FxUtils;

import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.*;

public class TeamDetailsPaneView implements FxmlView<TeamDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private FilteredListBiControl<TeamMemberListItemViewModel> teamMemberList;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private TeamDetailsPaneViewModel viewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                        viewModel.descriptionValidation(), "Add a description...");
        final Callback<ListView<TeamMemberListItemViewModel>, ListCell<TeamMemberListItemViewModel>> createForFxmlView = CachedViewModelCellFactory.createForFxmlView(TeamMemberListItemView.class);
        FxUtils.initGoatLabel(teamMemberList, viewModel, viewModel.teamMemberViewModels(), viewModel.eligibleTeamMembers(), createForFxmlView);

        // Using the traditional controller for the allocations table, allocations might be null initially. Therefore,
        // a listener is setup to set the items only when allocations is not null.
        viewModel.allocations().addListener((ListChangeListener) change -> {
            if (viewModel.allocations().get() != null) {
                allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.PROJECT);
                allocationsTableViewController.setMainController(viewModel.mainControllerProperty().get());
                allocationsTableViewController.setItems(viewModel.allocations());
            }
        });
    }
}

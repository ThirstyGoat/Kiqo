package com.thirstygoat.kiqo.gui.team;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.fxml.*;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.*;

public class TeamDetailsPaneView implements FxmlView<TeamDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private Label teamMembersLabel;
    @FXML
    private GoatLabelTextField poLabel;
    @FXML
    private GoatLabelTextField smLabel;
    @FXML
    private Label devTeamLabel;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private TeamDetailsPaneViewModel viewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "No Description");
        FxUtils.initGoatLabel(poLabel, viewModel, viewModel.productOwnerProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.productOwnerValidation(), "None");
        FxUtils.setTextFieldSuggester(poLabel.getEditField(), viewModel.productOwnerSupplier());
        FxUtils.initGoatLabel(smLabel, viewModel, viewModel.scrumMasterProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.scrumMasterValidation(), "None");
        FxUtils.setTextFieldSuggester(smLabel.getEditField(), viewModel.scrumMasterSupplier());

//      FxUtils.initGoatLabel(teamMembersLabel, viewModel, viewModel.teamMembersProperty(), viewModel.teamMembersValidation(), "None");
//      FxUtils.initGoatLabel(devTeamLabel, viewModel, viewModel.devTeamProperty(), viewModel.devTeamValidation(), "None");
        
        teamMembersLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(viewModel.teamMembersProperty()));
        devTeamLabel.textProperty().bind(Utilities.commaSeparatedValuesProperty(viewModel.devTeamProperty()));

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
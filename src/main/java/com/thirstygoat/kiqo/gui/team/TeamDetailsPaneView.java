package com.thirstygoat.kiqo.gui.team;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.*;
import javafx.scene.control.ListView;


public class TeamDetailsPaneView implements FxmlView<TeamDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private GoatLabelTextField productOwnerLabel;
    @FXML
    private GoatLabelTextField scrumMasterLabel;
    @FXML
    private GoatLabelFilteredListSelectionView<Person> devTeamLabel;
    @FXML
    private FilteredListBiControl<ListView<Person>, Person> teamMemberList;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private TeamDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        
        FxUtils.setTextFieldSuggester(productOwnerLabel.getEditField(), viewModel.productOwnerSupplier());
        FxUtils.initGoatLabel(productOwnerLabel, viewModel, viewModel.productOwnerProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.productOwnerValidation());
        
        FxUtils.setTextFieldSuggester(scrumMasterLabel.getEditField(), viewModel.scrumMasterSupplier());
        FxUtils.initGoatLabel(scrumMasterLabel, viewModel, viewModel.scrumMasterProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.scrumMasterValidation());
        
        FxUtils.initGoatLabel(devTeamLabel, viewModel, viewModel.devTeamProperty(), viewModel.eligibleDevs());
        FxUtils.initFilteredListBiControl(teamMemberList, viewModel, viewModel.teamMembersProperty(),
                        viewModel.eligibleTeamMembers());

        // Using the traditional controller for the allocations table, allocations might be null initially. Therefore,
        // a listener is setup to set the items only when allocations is not null.
        viewModel.allocations().addListener((ListChangeListener.Change<? extends Allocation> change) -> {
            if (viewModel.allocations().get() != null) {
                allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.PROJECT, viewModel.getWrappedObject());
                allocationsTableViewController.setMainController(viewModel.mainControllerProperty().get());
                allocationsTableViewController.setItems(viewModel.allocations());
            }
        });
    }
}

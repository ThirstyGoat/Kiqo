package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;


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
    private FilteredListBiControl<Label, Person> devTeamLabel;
    @FXML
    private FilteredListBiControl<ListView<Person>, Person> teamMemberList;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;
    
    @FXML
    private Label devTeamLabel_debug1; // TODO remove
    @FXML
    private Label devTeamLabel_debug2; // TODO remove
    @FXML
    private Label devTeamLabel_debug3; // TODO remove

    @InjectViewModel
    private TeamDetailsPaneViewModel viewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(),
                        viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                        viewModel.descriptionValidation());
        FxUtils.initGoatLabel(productOwnerLabel, viewModel, viewModel.productOwnerProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.productOwnerValidation());
        FxUtils.setTextFieldSuggester(productOwnerLabel.getEditField(), viewModel.productOwnerSupplier());
        
        devTeamLabel_debug1.textProperty().bind(Utilities.commaSeparatedValuesBinding(viewModel.devTeamProperty()));
        devTeamLabel_debug2.textProperty().bind(Utilities.commaSeparatedValuesProperty(viewModel.eligibleDevs()));
        devTeamLabel_debug3.textProperty().bind(Utilities.commaSeparatedValuesProperty(viewModel.teamMembersProperty()));

        FxUtils.initLabelFilteredListBiControl(devTeamLabel, viewModel, viewModel.devTeamProperty(), viewModel.eligibleDevs());
        FxUtils.initListViewFilteredListBiControl(teamMemberList, viewModel, viewModel.teamMembersProperty(),
                        viewModel.eligibleTeamMembers());

        // Using the traditional controller for the allocations table, allocations might be null initially. Therefore,
        // a listener is setup to set the items only when allocations is not null.
        viewModel.allocations().addListener((ListChangeListener) change -> {
            if (viewModel.allocations().get() != null) {
                allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.PROJECT, viewModel.getWrappedObject());
                allocationsTableViewController.setMainController(viewModel.mainControllerProperty().get());
                allocationsTableViewController.setItems(viewModel.allocations());
            }
        });
    }
}

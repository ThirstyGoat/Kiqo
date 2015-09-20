package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.gui.nodes.AllocationsTableViewController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;


public class TeamDetailsPaneView implements FxmlView<TeamDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private FilteredListBiControl<Person> teamMemberList;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private TeamDetailsPaneViewModel teamViewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, teamViewModel, teamViewModel.shortNameProperty(),
                        teamViewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, teamViewModel, teamViewModel.descriptionProperty(),
                        teamViewModel.descriptionValidation(), "Add a description...");
        FxUtils.initGoatLabel(teamMemberList, teamViewModel, teamViewModel.teamMembersProperty(),
                        teamViewModel.eligibleTeamMembers());

        // Using the traditional controller for the allocations table, allocations might be null initially. Therefore,
        // a listener is setup to set the items only when allocations is not null.
        teamViewModel.allocations().addListener((ListChangeListener) change -> {
            if (teamViewModel.allocations().get() != null) {
                allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.PROJECT);
                allocationsTableViewController.setMainController(teamViewModel.mainControllerProperty().get());
                allocationsTableViewController.setItems(teamViewModel.allocations());
            }
        });
    }
}

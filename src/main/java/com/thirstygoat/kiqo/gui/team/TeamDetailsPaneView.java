package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.gui.nodes.AllocationsTableViewController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.gui.team.PersonListItemViewModel.Role;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class TeamDetailsPaneView implements FxmlView<TeamDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private FilteredListBiControl<PersonListItemViewModel> teamMemberList;
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
        Callback<ListView<PersonListItemViewModel>, ListCell<PersonListItemViewModel>> displayCellFactory = listView -> {
        	return new ListCell<PersonListItemViewModel>() {
        		@Override
        		public void updateItem(PersonListItemViewModel item, boolean empty) {
        			super.updateItem(item, empty);
        			if (!empty) {
        				Label personShortNameLabel = new Label();
        	        	personShortNameLabel.textProperty().bind(item.shortNameProperty());
        	        	Region region = new Region();
        				HBox.setHgrow(region, Priority.ALWAYS);
        	        	Label roleBadge = new Label();
        	        	roleBadge.textProperty().bind(item.roleProperty().asString());
        	        	roleBadge.textFillProperty().bind(item.roleColorBinding());
        				setGraphic(new HBox(personShortNameLabel, region, roleBadge));
        			}
        		}
        	};
        };
        Callback<PersonListItemViewModel, Node> editCellFactory = personViewModel -> {
        	Label personShortNameLabel = new Label();
        	personShortNameLabel.textProperty().bind(personViewModel.shortNameProperty());
        	Region region = new Region();
			HBox.setHgrow(region, Priority.ALWAYS);
        	ComboBox<Role> comboBox = new ComboBox<Role>();
        	comboBox.getItems().addAll(Role.values());
			return new HBox(personShortNameLabel, region, comboBox);
        };
        ListProperty<PersonListItemViewModel> teamMemberViewModels = new SimpleListProperty<>();
		teamMemberViewModels.bind(Bindings.createObjectBinding(() -> {
			return teamViewModel.teamMembersProperty().stream().map(PersonListItemViewModel::new).collect(GoatCollectors.toObservableList());
		}, teamViewModel.teamMembersProperty()));
		ObjectBinding<ObservableList<PersonListItemViewModel>> eligibleTeamMemberViewModels = Bindings.createObjectBinding(() -> {
			return teamViewModel.eligibleTeamMembers().get().stream().map(PersonListItemViewModel::new).collect(GoatCollectors.toObservableList());
		}, teamViewModel.eligibleTeamMembers());
		FxUtils.initGoatLabel(teamMemberList, teamViewModel, teamMemberViewModels,
                        eligibleTeamMemberViewModels, 
                        displayCellFactory, editCellFactory, PersonListItemViewModel::shortNameProperty);
        

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

package com.thirstygoat.kiqo.gui.team;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.ListChangeListener;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.gui.team.PersonListItemViewModel.Role;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.FxUtils;


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
    private TeamDetailsPaneViewModel viewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                        viewModel.descriptionValidation(), "Add a description...");
        FxUtils.initGoatLabel(teamMemberList, viewModel, viewModel.teamMemberViewModels(), viewModel.eligibleTeamMembers(), 
                CachedViewModelCellFactory.createForFxmlView(TeamMemberListItemView.class), 
                this::createGraphic,
                PersonListItemViewModel::shortNameProperty);

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
    
    private Node createGraphic(PersonListItemViewModel personViewModel) {
        final Label label = new Label();
        label.textProperty().bind(personViewModel.shortNameProperty());
        final Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        final RadioButton radioPo = new RadioButton();
        radioPo.setStyle("-fx-mark-color: blue;");
        final RadioButton radioSm = new RadioButton();
        radioSm.setStyle("-fx-mark-color: red;");
        final RadioButton radioDev = new RadioButton();
        radioDev.setStyle("-fx-mark-color: green;");
        final RadioButton radioOther = new RadioButton();
        
        final ToggleGroup radioGroup = new ToggleGroup();
        radioGroup.getToggles().addAll(radioPo, radioSm, radioDev, radioOther);

        final HBox hBox = new HBox();
        hBox.getChildren().addAll(label, region, radioPo, radioSm, radioDev, radioOther);
        
        Person person = personViewModel.getPerson();
        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == radioPo) {
//                    personViewModel.roleProperty().set(Role.PRODUCT_OWNER);
                    viewModel.productOwnerProperty().set(person);
                } else if (newValue == radioSm) {
//                    personViewModel.roleProperty().set(Role.SCRUM_MASTER);
                    viewModel.scrumMasterProperty().set(person);
                } else if (newValue == radioDev) {
//                    personViewModel.roleProperty().set(Role.DEVELOPMENT);
                    viewModel.devTeamProperty().add(person);
                } else if (newValue == radioOther) {
//                    personViewModel.roleProperty().set(Role.OTHER);
                    // TODO Other role
                }
            });
        
        Consumer<Role> updateRadioButtons = role -> {
            if (role == Role.PRODUCT_OWNER) {
                radioGroup.selectToggle(radioPo);
            } else if (role == Role.SCRUM_MASTER) {
                radioGroup.selectToggle(radioSm);
            } else if (role == Role.DEVELOPMENT) {
                radioGroup.selectToggle(radioDev);
            } else if (role == Role.OTHER) {
                radioGroup.selectToggle(radioOther);
            }
        };
//        personViewModel.roleProperty().addListener((observable, oldValue, newValue) -> updateRadioButtons.accept(newValue));
        updateRadioButtons.accept(personViewModel.roleProperty().get()); // init
        
        viewModel.productOwnerProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == person) {
                personViewModel.roleProperty().set(Role.PRODUCT_OWNER);
            }
        });
        viewModel.scrumMasterProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == person) {
                personViewModel.roleProperty().set(Role.SCRUM_MASTER);
            }
        });
        viewModel.devTeamProperty().addListener((ListChangeListener.Change<? extends Person> change) -> {
            change.next();
            if (change.getAddedSubList().contains(person)) {
                personViewModel.roleProperty().set(Role.DEVELOPMENT);
            }
        });
        // TODO Other role
//        viewModel.productOwnerProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue == person) {
//                personViewModel.roleProperty().set(Role.OTHER);
//            }
//        });
        
        
        return hBox;
    }
}

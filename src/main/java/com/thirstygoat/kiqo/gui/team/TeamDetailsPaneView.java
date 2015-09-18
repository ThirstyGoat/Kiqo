package com.thirstygoat.kiqo.gui.team;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
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
    private TeamDetailsPaneViewModel teamViewModel;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, teamViewModel, teamViewModel.shortNameProperty(), teamViewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, teamViewModel, teamViewModel.descriptionProperty(),
                        teamViewModel.descriptionValidation(), "Add a description...");
        FxUtils.initGoatLabel(teamMemberList, teamViewModel, teamViewModel.teamMemberViewModels(), teamViewModel.eligibleTeamMembers(), 
                CachedViewModelCellFactory.createForFxmlView(TeamMemberListItemView.class), 
                this::createGraphic,
                PersonListItemViewModel::shortNameProperty);

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
    
    private Node createGraphic(PersonListItemViewModel personViewModel) {
        final Label label = new Label();
        label.textProperty().bind(personViewModel.shortNameProperty());
        
        final Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

//        Node roleControl = createRadioButtons(personViewModel.roleProperty());
        Node roleControl = createComboBox(personViewModel);
        bindPersonViewModel(personViewModel);

        final HBox hBox = new HBox();
        hBox.getChildren().addAll(label, region, roleControl);
        
        return hBox;
    }
        
    private void bindPersonViewModel(PersonListItemViewModel personViewModel) {
        personViewModel.roleProperty().bind(Bindings.createObjectBinding(
                () -> { 
                    Person person = personViewModel.getPerson();
                    if (teamViewModel.productOwnerProperty().get() == person) {
                        return Role.PRODUCT_OWNER;
                    } else if (teamViewModel.scrumMasterProperty().get() == person) {
                        return Role.SCRUM_MASTER;
                    } else if (teamViewModel.devTeamProperty().contains(person)) { //////////////// bound
                        return Role.DEVELOPMENT;
                    } else {
                        return Role.OTHER;
                    }
                },
                teamViewModel.productOwnerProperty(),
                teamViewModel.scrumMasterProperty(),
                teamViewModel.devTeamProperty()));
    }

    private Node createComboBox(PersonListItemViewModel personViewModel) {
        final ComboBox<Role> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(Role.values());
//        comboBox.valueProperty().bindBidirectional(personViewModel.roleProperty());
        return comboBox;
    }

    @Deprecated // TODO remove
    private Node createRadioButtons(PersonListItemViewModel personViewModel) {
        final RadioButton radioPo = new RadioButton();
        radioPo.setStyle("-fx-mark-color: " + PersonListItemViewModel.getColorString(Role.PRODUCT_OWNER) + ";");
        final RadioButton radioSm = new RadioButton();
        radioSm.setStyle("-fx-mark-color: " + PersonListItemViewModel.getColorString(Role.SCRUM_MASTER) + ";");
        final RadioButton radioDev = new RadioButton();
        radioDev.setStyle("-fx-mark-color: " + PersonListItemViewModel.getColorString(Role.DEVELOPMENT) + ";");
        final RadioButton radioOther = new RadioButton();
        radioOther.setStyle("-fx-mark-color: " + PersonListItemViewModel.getColorString(Role.OTHER) + ";");
        
        final ToggleGroup radioGroup = new ToggleGroup();
        radioGroup.getToggles().addAll(radioPo, radioSm, radioDev, radioOther);
        
        Node roleControl = new HBox(radioPo, radioSm, radioDev, radioOther);
        return roleControl;

//      Person person = personViewModel.getPerson();
//      radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
//              if (newValue == radioPo) {
////                  personViewModel.roleProperty().set(Role.PRODUCT_OWNER);
//                  teamViewModel.productOwnerProperty().set(person);
//              } else if (newValue == radioSm) {
////                  personViewModel.roleProperty().set(Role.SCRUM_MASTER);
//                  teamViewModel.scrumMasterProperty().set(person);
//              } else if (newValue == radioDev) {
////                  personViewModel.roleProperty().set(Role.DEVELOPMENT);
//                  teamViewModel.devTeamProperty().add(person);
//              } else if (newValue == radioOther) {
////                  personViewModel.roleProperty().set(Role.OTHER);
//                  // TODO Other role
//              }
//          });
//      
//      Consumer<Role> updateRadioButtons = role -> {
//          if (role == Role.PRODUCT_OWNER) {
//              radioGroup.selectToggle(radioPo);
//          } else if (role == Role.SCRUM_MASTER) {
//              radioGroup.selectToggle(radioSm);
//          } else if (role == Role.DEVELOPMENT) {
//              radioGroup.selectToggle(radioDev);
//          } else if (role == Role.OTHER) {
//              radioGroup.selectToggle(radioOther);
//          }
//      };
////      personViewModel.roleProperty().addListener((observable, oldValue, newValue) -> updateRadioButtons.accept(newValue));
//      updateRadioButtons.accept(personViewModel.roleProperty().get()); // init
//      
//      teamViewModel.productOwnerProperty().addListener((observable, oldValue, newValue) -> {
//          if (newValue == person) {
//              personViewModel.roleProperty().set(Role.PRODUCT_OWNER);
//          }
//      });
//      teamViewModel.scrumMasterProperty().addListener((observable, oldValue, newValue) -> {
//          if (newValue == person) {
//              personViewModel.roleProperty().set(Role.SCRUM_MASTER);
//          }
//      });
//      teamViewModel.devTeamProperty().addListener((ListChangeListener.Change<? extends Person> change) -> {
//          change.next();
//          if (change.getAddedSubList().contains(person)) {
//              personViewModel.roleProperty().set(Role.DEVELOPMENT);
//          }
//      });
//      // TODO Other role
////      teamViewModel.productOwnerProperty().addListener((observable, oldValue, newValue) -> {
////          if (newValue == person) {
////              personViewModel.roleProperty().set(Role.OTHER);
////          }
////      });
      
      
      

    }
}

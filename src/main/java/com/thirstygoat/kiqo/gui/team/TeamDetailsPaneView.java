package com.thirstygoat.kiqo.gui.team;

import java.net.URL;
import java.util.ResourceBundle;

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
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "No Description");
//        FxUtils.initGoatLabel(teamMembersLabel, viewModel, viewModel.teamMembersProperty(), viewModel.teamMembersValidation(), "None");
        FxUtils.initGoatLabel(poLabel, viewModel, viewModel.productOwnerProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.productOwnerValidation(), "None");
        FxUtils.setTextFieldSuggester(poLabel.getEditField(), viewModel.productOwnerSupplier());
        FxUtils.initGoatLabel(smLabel, viewModel, viewModel.scrumMasterProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()), viewModel.scrumMasterValidation(), "None");
        FxUtils.setTextFieldSuggester(smLabel.getEditField(), viewModel.scrumMasterSupplier());
//        FxUtils.initGoatLabel(devTeamLabel, viewModel, viewModel.devTeamProperty(), viewModel.devTeamValidation(), "None");
    }
}
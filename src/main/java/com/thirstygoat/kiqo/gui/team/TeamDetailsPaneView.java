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
    private Label poLabel;
    @FXML
    private Label smLabel;
    @FXML
    private Label devTeamLabel;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private TeamDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "Description");
    }
}
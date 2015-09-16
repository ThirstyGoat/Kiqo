package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

/**
 * Created by leroy on 15/09/15.
 */
public class TeamMemberListItemView implements FxmlView<TeamMemberListItemViewModel> {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label roleBadge;

    @InjectViewModel
    private TeamMemberListItemViewModel viewModel;

    public void initialize() {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(),
                        viewModel.nameValidation());

        roleBadge.textProperty().bind(viewModel.roleNameProperty());
        roleBadge.styleProperty().bind(viewModel.roleColorProperty());
    }
}

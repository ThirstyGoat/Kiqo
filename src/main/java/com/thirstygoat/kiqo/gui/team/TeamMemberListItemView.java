package com.thirstygoat.kiqo.gui.team;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import de.saxsys.mvvmfx.*;

/**
 * Created by leroy on 15/09/15.
 */
public class TeamMemberListItemView implements FxmlView<PersonListItemViewModel> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label roleBadge;

    @InjectViewModel
    private PersonListItemViewModel viewModel;

    public void initialize() {
        shortNameLabel.textProperty().bind(viewModel.shortNameProperty());

        roleBadge.textProperty().bind(viewModel.roleProperty().asString());
        roleBadge.textFillProperty().bind(viewModel.roleColorBinding());
    }
}

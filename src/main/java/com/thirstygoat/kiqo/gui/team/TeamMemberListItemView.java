package com.thirstygoat.kiqo.gui.team;

import javafx.beans.binding.Bindings;
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

        roleBadge.textProperty().bind(Bindings.createStringBinding(() -> {
            return viewModel.roleProperty().get().getName();
        }, viewModel.roleProperty()));
        roleBadge.styleProperty().bind(Bindings.createStringBinding(() -> {
            return viewModel.roleProperty().get().getStyle();
        }, viewModel.roleProperty()));
    }
}

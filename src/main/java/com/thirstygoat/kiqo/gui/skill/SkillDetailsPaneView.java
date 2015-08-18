package com.thirstygoat.kiqo.gui.skill;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Labeled;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

/**
* Created by Bradley Kirwan on 14/08/2015.
*/
public class SkillDetailsPaneView implements FxmlView<SkillViewModel>, Initializable {
    @FXML
    private Labeled shortNameLabel;
    @FXML
    private Labeled descriptionLabel;

    @InjectViewModel
    private SkillViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shortNameLabel.textProperty().bindBidirectional(viewModel.shortNameProperty());
        descriptionLabel.textProperty().bindBidirectional(viewModel.descriptionProperty());
    }
}

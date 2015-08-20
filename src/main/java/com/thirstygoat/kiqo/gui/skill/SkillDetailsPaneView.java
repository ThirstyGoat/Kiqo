package com.thirstygoat.kiqo.gui.skill;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.model.Scale;

import de.saxsys.mvvmfx.*;

/**
* Created by Bradley Kirwan on 14/08/2015.
*/
public class SkillDetailsPaneView implements FxmlView<SkillViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private SkillViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.skillProperty().addListener((observable, oldValue, newValue) -> {
            shortNameLabel.displayTextProperty().bind(newValue.shortNameProperty());
            descriptionLabel.displayTextProperty().bind(newValue.descriptionProperty());

            shortNameLabel.getEditField().textProperty().bindBidirectional(viewModel.shortNameProperty());
            descriptionLabel.getEditField().textProperty().bindBidirectional(viewModel.descriptionProperty());
        });

        shortNameLabel.commandProperty().bind(viewModel.commandProperty());
        descriptionLabel.commandProperty().bind(viewModel.commandProperty());
    }
}

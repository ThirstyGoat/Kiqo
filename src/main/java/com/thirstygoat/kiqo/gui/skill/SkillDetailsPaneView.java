package com.thirstygoat.kiqo.gui.skill;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Bradley Kirwan on 14/08/2015.
*/
public class SkillDetailsPaneView implements FxmlView<SkillDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private SkillDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.nameProperty(), viewModel.nameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());

        shortNameLabel.editableProperty().bind(viewModel.disableSkillEditing());
    }

}

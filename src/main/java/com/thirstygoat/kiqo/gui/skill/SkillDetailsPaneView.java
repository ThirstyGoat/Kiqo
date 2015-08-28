package com.thirstygoat.kiqo.gui.skill;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.util.FxUtils;
import javafx.fxml.*;

import com.thirstygoat.kiqo.gui.nodes.*;

import de.saxsys.mvvmfx.*;

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
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
    }
}

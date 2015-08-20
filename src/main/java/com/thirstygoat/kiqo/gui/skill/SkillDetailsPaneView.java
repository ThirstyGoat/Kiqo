package com.thirstygoat.kiqo.gui.skill;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;

import com.thirstygoat.kiqo.gui.nodes.*;

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
        });

        shortNameLabel.getEditField().textProperty().bindBidirectional(viewModel.nameProperty());
        descriptionLabel.getEditField().textProperty().bindBidirectional(viewModel.descriptionProperty());

        shortNameLabel.setCommandSupplier(viewModel::createCommand);
        descriptionLabel.setCommandSupplier(viewModel::createCommand);
        
        shortNameLabel.validationStatus().set(viewModel.nameValidation());
        descriptionLabel.validationStatus().set(viewModel.descriptionValidation());
    }
}

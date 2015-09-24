package com.thirstygoat.kiqo.gui.person;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.ListView;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.FxUtils;

import de.saxsys.mvvmfx.*;

/**
 * Created by Carina on 25/03/2015.
 */
public class PersonDetailsPaneView implements FxmlView<PersonDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private GoatLabelTextField userIdLabel;
    @FXML
    private GoatLabelTextField emailLabel;
    @FXML
    private GoatLabelTextField phoneLabel;
    @FXML
    private GoatLabelTextField departmentLabel;
    @FXML
    private FilteredListBiControl<ListView<Skill>, Skill> skillsLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private PersonDetailsPaneViewModel viewModel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());
        FxUtils.initGoatLabel(userIdLabel, viewModel, viewModel.userIdProperty(), viewModel.userIdValidation());
        FxUtils.initGoatLabel(emailLabel, viewModel, viewModel.emailProperty(), viewModel.emailValidation());
        FxUtils.initGoatLabel(phoneLabel, viewModel, viewModel.phoneNumberProperty(), viewModel.phoneNumberValidation());
        FxUtils.initGoatLabel(departmentLabel, viewModel, viewModel.departmentProperty(), viewModel.departmentValidation());
        FxUtils.initListViewFilteredListBiControl(skillsLabel, viewModel, viewModel.skills(), viewModel.availableSkills());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
    }
}

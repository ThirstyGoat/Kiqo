package com.thirstygoat.kiqo.gui.person;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelFilteredListSelectionView;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

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
    private GoatLabelFilteredListSelectionView<Skill> skillsLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private PersonDetailsPaneViewModel viewModel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), null);
        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), null);
        FxUtils.initGoatLabel(userIdLabel, viewModel, viewModel.userIdProperty(), null);
        FxUtils.initGoatLabel(emailLabel, viewModel, viewModel.emailProperty(), null);
        FxUtils.initGoatLabel(phoneLabel, viewModel, viewModel.phoneNumberProperty(), null);
        FxUtils.initGoatLabel(departmentLabel, viewModel, viewModel.departmentProperty(), null);
//        FxUtils.initGoatLabel(skillsLabel, viewModel, viewModel.skills(), viewModel.organisationProperty().get().getSkills(), null);
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
    }


}

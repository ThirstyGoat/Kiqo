package com.thirstygoat.kiqo.gui.release;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.util.FxUtils;

import de.saxsys.mvvmfx.*;

public class ReleaseDetailsPaneView implements FxmlView<ReleaseDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private Label projectLabel;
    @FXML
    private GoatLabelDatePicker releaseDateLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private ReleaseDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(releaseDateLabel, viewModel, viewModel.dateProperty(), viewModel.dateStringProperty(), viewModel.dateValidation());
        projectLabel.textProperty().bind(viewModel.projectNameProperty());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "Description");
    }
}
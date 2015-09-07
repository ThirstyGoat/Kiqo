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
    private Label releaseDateLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private ReleaseDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        projectLabel.textProperty().bind(viewModel.projectNameProperty());
        releaseDateLabel.textProperty().bind(viewModel.dateStringProperty());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "Description");
    }
}
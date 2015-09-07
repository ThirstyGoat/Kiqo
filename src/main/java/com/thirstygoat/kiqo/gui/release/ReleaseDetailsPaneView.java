package com.thirstygoat.kiqo.gui.release;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.Label;
import de.saxsys.mvvmfx.*;

public class ReleaseDetailsPaneView implements FxmlView<ReleaseDetailsPaneViewModel>, Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label projectLabel;
    @FXML
    private Label releaseDateLabel;
    @FXML
    private Label descriptionLabel;

    @InjectViewModel
    private ReleaseDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shortNameLabel.textProperty().bind(viewModel.shortNameProperty());
        projectLabel.textProperty().bind(viewModel.projectNameProperty());
        releaseDateLabel.textProperty().bind(viewModel.dateStringProperty());
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
    }
}
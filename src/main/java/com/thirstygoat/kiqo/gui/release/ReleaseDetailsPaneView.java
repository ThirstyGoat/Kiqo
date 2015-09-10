package com.thirstygoat.kiqo.gui.release;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.Label;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.*;

public class ReleaseDetailsPaneView implements FxmlView<ReleaseDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField projectLabel;
    @FXML
    private GoatLabelDatePicker releaseDatePicker;
    @FXML
    private GoatLabelTextArea descriptionLabel;

    @InjectViewModel
    private ReleaseDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(projectLabel, viewModel, viewModel.projectProperty(), viewModel.projectValidation(),
                StringConverters.projectStringConverter(viewModel.organisationProperty()));
        FxUtils.setTextFieldSuggester(projectLabel.getEditField(), viewModel.projectSupplier());
        FxUtils.initGoatLabel(releaseDatePicker, viewModel, viewModel.dateProperty(), viewModel.dateStringProperty(), viewModel.dateValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "Description");
    }
}
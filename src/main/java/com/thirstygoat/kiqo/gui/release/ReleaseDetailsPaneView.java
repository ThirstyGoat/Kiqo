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
    private GoatLabelTextField shortNameTextField;
    @FXML
    private GoatLabelTextField projectTextField;
    @FXML
    private GoatLabelDatePicker releaseDatePicker;
    @FXML
    private GoatLabelTextArea descriptionTextField;

    @InjectViewModel
    private ReleaseDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameTextField, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(projectTextField, viewModel, viewModel.projectProperty(), StringConverters.projectStringConverter(viewModel.organisationProperty()),
                viewModel.projectValidation());
        FxUtils.setTextFieldSuggester(projectTextField.getEditField(), viewModel.projectsSupplier());
        FxUtils.initGoatLabel(releaseDatePicker, viewModel, viewModel.dateProperty(), viewModel.dateStringProperty(), viewModel.dateValidation());
    }
}
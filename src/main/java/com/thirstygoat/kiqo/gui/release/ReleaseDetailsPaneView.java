package com.thirstygoat.kiqo.gui.release;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelDatePicker;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ReleaseDetailsPaneView implements FxmlView<ReleaseDetailsPaneViewModel>, Initializable {
    @FXML
    private GoatLabelTextField shortNameTextField;
    @FXML
    private GoatLabelTextField projectTextField;
    @FXML
    private GoatLabelDatePicker releaseDatePicker;
    @FXML
    private GoatLabelTextArea descriptionTextArea;

    @InjectViewModel
    private ReleaseDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtils.initGoatLabel(shortNameTextField, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(projectTextField, viewModel, viewModel.projectProperty(), StringConverters.projectStringConverter(viewModel.organisationProperty()),
                viewModel.projectValidation());
        FxUtils.setTextFieldSuggester(projectTextField.getEditField(), viewModel.projectsSupplier());
        FxUtils.initGoatLabel(releaseDatePicker, viewModel, viewModel.dateProperty(), viewModel.dateStringProperty(), viewModel.dateValidation());
        FxUtils.initGoatLabel(descriptionTextArea, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation(), "Add a description...");
    }
}
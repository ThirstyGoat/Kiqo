package com.thirstygoat.kiqo.gui.sprint;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.*;

import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.*;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneDetailsView implements FxmlView<SprintDetailsPaneDetailsViewModel>, Initializable {
    
    private Label placeHolder = new Label();
    
    @InjectViewModel
    private SprintDetailsPaneDetailsViewModel viewModel;
    
    @FXML
    private GoatLabelTextField teamLabel;
    @FXML
    private GoatLabelTextField backlogLabel;
    @FXML
    private GoatLabelDatePicker startDateLabel;
    @FXML
    private GoatLabelDatePicker endDateLabel;
    @FXML
    private GoatLabelTextField releaseLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        FxUtils.initGoatLabel(startDateLabel, viewModel, viewModel.startDateProperty(),
                viewModel.startDateStringProperty(), viewModel.startDateValidation());
        FxUtils.initGoatLabel(endDateLabel, viewModel, viewModel.endDateProperty(), viewModel.endDateStringProperty(),
                viewModel.endDateValidation());
        FxUtils.initGoatLabel(releaseLabel, viewModel, viewModel.releaseProperty(),
                StringConverters.releaseStringConverter(viewModel.organisationProperty()), viewModel.releaseValidation());
        FxUtils.setTextFieldSuggester(releaseLabel.getEditField(), viewModel.releasesSupplier());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation(), "Add a description...");
        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.teamProperty(),
                StringConverters.teamStringConverter(viewModel.organisationProperty()), viewModel.teamValidation());
        FxUtils.setTextFieldSuggester(teamLabel.getEditField(), viewModel.teamsSupplier());
        FxUtils.initGoatLabel(backlogLabel, viewModel, viewModel.backlogProperty(),
                StringConverters.backlogStringConverter(viewModel.organisationProperty()), viewModel.backlogValidation());
        FxUtils.setTextFieldSuggester(backlogLabel.getEditField(), viewModel.backlogsSupplier());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());

        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.itemsProperty().bind(viewModel.stories());

        placeHolder.textProperty().set(SprintDetailsPaneDetailsViewModel.PLACEHOLDER);
    }

    public SprintDetailsPaneDetailsViewModel getViewModel() {
        return viewModel;
    }
}

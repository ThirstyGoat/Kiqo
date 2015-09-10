package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelDatePicker;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneDetailsView implements FxmlView<SprintDetailsPaneDetailsViewModel>, Initializable {
    
    private Label placeHolder = new Label();
    
    @InjectViewModel
    private SprintDetailsPaneDetailsViewModel viewModel;

    @FXML
    private GoatLabelTextField longNameLabel;
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
    private GoatLabelTextField descriptionLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());
        FxUtils.initGoatLabel(startDateLabel, viewModel, viewModel.startDateProperty(),
                viewModel.startDateStringProperty(), viewModel.startDateValidation());
        FxUtils.initGoatLabel(endDateLabel, viewModel, viewModel.endDateProperty(), viewModel.endDateStringProperty(),
                viewModel.endDateValidation());
        FxUtils.initGoatLabel(releaseLabel, viewModel, viewModel.releaseProperty(), viewModel.releaseValidation(),
                StringConverters.releaseStringConverter(viewModel.organisationProperty()));
        FxUtils.setTextFieldSuggester(releaseLabel.getEditField(), viewModel.releasesSupplier());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());
        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.teamProperty(), viewModel.teamValidation(),
                StringConverters.teamStringConverter(viewModel.organisationProperty()));
        FxUtils.setTextFieldSuggester(teamLabel.getEditField(), viewModel.teamsSupplier());
        FxUtils.initGoatLabel(backlogLabel, viewModel, viewModel.backlogProperty(), viewModel.backlogValidation(),
                StringConverters.backlogStringConverter(viewModel.organisationProperty()));
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

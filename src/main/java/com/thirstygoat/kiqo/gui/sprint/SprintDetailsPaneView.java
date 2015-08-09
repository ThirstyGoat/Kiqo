package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.nodes.GoatLabel;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelView;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
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
public class SprintDetailsPaneView implements FxmlView<SprintDetailsPaneViewModel>, Initializable {
    
    private Label placeHolder = new Label();
    
    @InjectViewModel
    private SprintDetailsPaneViewModel viewModel;

    @FXML
    private GoatLabelView longNameLabel;
    @FXML
    private Label goalLabel;
    @FXML
    private Label teamLabel;
    @FXML
    private Label backlogLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label releaseLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        longNameLabel.setViewModel(viewModel);
        longNameLabel.textProperty().bindBidirectional(viewModel.longNameProperty());
        longNameLabel.getEditField().textProperty().bindBidirectional(viewModel.longNameProperty());
        longNameLabel.doneButton().disableProperty().bind(viewModel.allValidation().validProperty().not());

        goalLabel.textProperty().bind(viewModel.goalProperty());
        startDateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (viewModel.startDateProperty().get() != null) {
                return viewModel.startDateProperty().get().format(datetimeFormat);
            }
            return "";
        }, viewModel.startDateProperty()));
        endDateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (viewModel.endDateProperty().get() != null) {
                return viewModel.endDateProperty().get().format(datetimeFormat);
            }
            return "";
        }, viewModel.endDateProperty()));
        releaseLabel.textProperty().bindBidirectional(viewModel.releaseProperty(),
                StringConverters.releaseStringConverter(viewModel.organisationProperty()));
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
        teamLabel.textProperty().bindBidirectional(viewModel.teamProperty(),
                StringConverters.teamStringConverter(viewModel.organisationProperty()));
        backlogLabel.textProperty().bindBidirectional(viewModel.backlogProperty(),
                StringConverters.backlogStringConverter(viewModel.organisationProperty()));

        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(viewModel.stories());

        placeHolder.textProperty().set(SprintDetailsPaneViewModel.PLACEHOLDER);
    }
}

package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Bradley Kirwan on 14/08/2015.
*/
public class SprintDetailsPaneView implements FxmlView<SprintDetailsPaneViewModel>, Initializable {

    @InjectViewModel
    private SprintDetailsPaneViewModel viewModel;

    @FXML
    private SprintDetailsPaneDetailsView detailsViewController; // Ignore naming convention here
    @FXML
    private ScrumBoardView scrumBoardViewController; // Ignore naming convention here
    @FXML
    private SprintDetailsPaneBurnDownView burndownViewController; // Ignore naming convention here
    @FXML
    private AnchorPane detailsView;
    @FXML
    private AnchorPane scrumBoardView;
    @FXML
    private AnchorPane burndownView;

    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;
    @FXML
    private ToggleButton scrumboardToggleButton;
    @FXML
    private ToggleButton burndownToggleButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideAllViews();

        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.goalProperty(), viewModel.goalValidation());
        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());


        // Add listener on segmentedButton
        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
            // Prevent deselection
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else if (newValue == detailsToggleButton) {
                // Show Details View
                show(detailsView);
            } else if (newValue == scrumboardToggleButton) {
                show(scrumBoardView);
            } else if (newValue == burndownToggleButton) {
                show(burndownView);
            } else {
                hideAllViews();
            }
        });

        detailsToggleButton.setSelected(true);
        viewModel.setDetailsViewModel(detailsViewController.getViewModel());
        viewModel.setScrumboardViewModel(scrumBoardViewController.getViewModel());
        viewModel.setBurndownViewModel(burndownViewController.getViewModel());
    }

    /**
     * Hides all views and then shows the given view
     * @param pane View to be shown
     */
    private void show(Pane pane) {
        hideAllViews();

        pane.setManaged(true);
        pane.setVisible(true);
    }

    /**
     * Hides all views
     */
    private void hideAllViews() {
        detailsView.setVisible(false);
        detailsView.setManaged(false);

        scrumBoardView.setVisible(false);
        scrumBoardView.setManaged(false);

        burndownView.setVisible(false);
        burndownView.setManaged(false);
    }
}

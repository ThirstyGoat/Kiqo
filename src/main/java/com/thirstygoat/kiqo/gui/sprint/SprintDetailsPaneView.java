package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Bradley Kirwan on 14/08/2015.
*/
public class SprintDetailsPaneView implements FxmlView<SprintDetailsPaneViewModel>, Initializable {

    @FXML
    private SprintDetailsPaneDetailsView detailsViewController; // Ignore naming convention here
    @FXML
    private AnchorPane detailsView;

    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private ToggleButton scrumboardToggleButton;

    @InjectViewModel
    private SprintDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideAllViews();

        // Add listener on segmentedButton
        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
            // Prevent deselection
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else if (newValue == detailsToggleButton) {
                // Show Details View
                show(detailsView);
            } else {
                hideAllViews();
            }
        });

        detailsToggleButton.setSelected(true);

        viewModel.setDetailsViewModel(detailsViewController.getViewModel());
    }

    /**
     * Hides all views and then shows the given view
     * @param node View to be shown
     */
    private void show(Node node) {
        hideAllViews();

        node.setManaged(true);
        node.setVisible(true);
    }

    /**
     * Hides all views
     */
    private void hideAllViews() {
        detailsView.setVisible(false);
        detailsView.setManaged(false);
    }
}

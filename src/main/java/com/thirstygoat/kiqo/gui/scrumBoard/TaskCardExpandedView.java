package com.thirstygoat.kiqo.gui.scrumBoard;

import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by james on 19/08/15.
 */
public class TaskCardExpandedView implements FxmlView<TaskCardViewModel>, Initializable {

    @InjectViewModel
    TaskCardViewModel viewModel;
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private Label teamLabel;
    @FXML
    private GoatLabelTextArea estimatedHoursLabel;
    @FXML
    private Label commentsLabel;
    @FXML
    private TextField spentEffortTextField;
    @FXML
    private GridPane detailsGridPane;
    @FXML
    private GridPane loggingGridPane;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Button loggingButton;
    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private ToggleButton loggingToggleButton;
    @FXML
    private HBox buttonsHBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else {
                if (newValue == detailsToggleButton) {
                    showNode(detailsGridPane);
                } else if (newValue == loggingToggleButton) {
                    showNode(loggingGridPane);
                }
            }
        });
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        FxUtils.initGoatLabel(estimatedHoursLabel, viewModel, viewModel.estimateProperty(), viewModel.estimateValidation());  //TODO fix the parsing error when "-" is typed into the box

        //TODO add the assigned people to form after creating a new GoatLabel for filtered selection thingy
//        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.getTask().get().getAssignedPeople(), viewModel.teamValidation());

        // TODO implement the comments/impediments

        // TODO add logging functionality

    }



    private void showNode(Node node) {
        for (Node node1 : mainAnchorPane.getChildren()) {
            node1.setManaged(false);
            node1.setVisible(false);
        }
        node.setManaged(true);
        node.setVisible(true);
    }


}

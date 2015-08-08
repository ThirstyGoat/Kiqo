package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.StoryListCell;
import com.thirstygoat.kiqo.gui.nodes.GoatComboBoxLabel;
import com.thirstygoat.kiqo.gui.nodes.GoatLabel;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneView implements FxmlView<BacklogDetailsPaneViewModel>, Initializable {

    @InjectViewModel
    private BacklogDetailsPaneViewModel backlogDetailsPaneViewModel;
    
    @FXML
    private GoatLabel shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label productOwnerLabel;
    @FXML
    private GoatComboBoxLabel scaleLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;
    @FXML
    private CheckBox highlightCheckBox;
    @FXML
    private Hyperlink highlightHyperLink;

    private Label placeHolder = new Label();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) { 

        shortNameLabel.textProperty().bind(backlogDetailsPaneViewModel.shortNameProperty());
        ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
        validationVisualizer.initVisualization(backlogDetailsPaneViewModel.shortNameValidation(), shortNameLabel.getEditField(), true);
        shortNameLabel.doneButton().disableProperty().bind(Bindings.not(backlogDetailsPaneViewModel.shortNameValidation().validProperty()));


        longNameLabel.textProperty().bind(backlogDetailsPaneViewModel.longNameProperty());
        descriptionLabel.textProperty().bind(backlogDetailsPaneViewModel.descriptionProperty());
        productOwnerLabel.textProperty().bind(backlogDetailsPaneViewModel.productOwnerStringProperty());
        scaleLabel.textProperty().bind(backlogDetailsPaneViewModel.scaleStringProperty());
        setHyperlink();

        shortNameTableColumn.setCellFactory(param -> {
            StoryListCell storyListCell = new StoryListCell(backlogDetailsPaneViewModel);
            storyListCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() > 1) {
                    MainController.focusedItemProperty.set((Story) storyListCell.getTableRow().getItem());
                }
            });
            return storyListCell;
        });
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(backlogDetailsPaneViewModel.getStories());

        placeHolder.textProperty().set(backlogDetailsPaneViewModel.PLACEHOLDER);

        scaleLabel.textProperty().bind(backlogDetailsPaneViewModel.scaleProperty().asString());

        highlightCheckBox.selectedProperty().bindBidirectional(backlogDetailsPaneViewModel.highlightStoryStateProperty());


        // Added
        backlogDetailsPaneViewModel.backlog.addListener((observable, oldValue, newValue) -> {
            shortNameLabel.setItem(backlogDetailsPaneViewModel.backlog.getValue(), "shortName",
                    backlogDetailsPaneViewModel.backlog.getValue().shortNameProperty());


            scaleLabel.setItem(backlogDetailsPaneViewModel.backlog.getValue(), "scale",
                    backlogDetailsPaneViewModel.backlog.getValue().scaleProperty(), Scale.values());
        });
    }

    private void setHyperlink() {
        PopOver popOver = new PopOver();
        popOver.setDetachable(false);

        HBox greenHbox = new HBox();
        HBox orangeHbox = new HBox();
        HBox redHbox = new HBox();

        VBox vb = new VBox();

        vb.getChildren().addAll(greenHbox, orangeHbox, redHbox);
        vb.setSpacing(2);
        greenHbox.setAlignment(Pos.CENTER_LEFT);
        orangeHbox.setAlignment(Pos.CENTER_LEFT);
        redHbox.setAlignment(Pos.CENTER_LEFT);
        greenHbox.setSpacing(5);
        orangeHbox.setSpacing(5);
        redHbox.setSpacing(5);

        Rectangle g = new Rectangle(10, 10);
        Rectangle o = new Rectangle(10, 10);
        Rectangle r = new Rectangle(10, 10);

        g.setFill(Color.GREEN);
        o.setFill(Color.ORANGE);
        r.setFill(Color.RED);

        greenHbox.getChildren().addAll(g, new Label("Story is ready"));
        orangeHbox.getChildren().addAll(o, new Label("Story can be estimated"));
        redHbox.getChildren().addAll(r, new Label("Story has a higher priority than its dependencies"));

        vb.setPadding(new Insets(10));

        popOver.setContentNode(vb);

        highlightHyperLink.setOnAction(e -> popOver.show(highlightHyperLink));
        highlightHyperLink.focusedProperty().addListener((observable, oldValue, newValue) -> popOver.hide(Duration.millis(0)));

        highlightHyperLink.visibleProperty().bind(highlightCheckBox.selectedProperty());
    }
}

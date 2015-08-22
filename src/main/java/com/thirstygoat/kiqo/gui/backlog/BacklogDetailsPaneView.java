package com.thirstygoat.kiqo.gui.backlog;

import java.net.URL;
import java.util.ResourceBundle;

import com.thirstygoat.kiqo.util.StringConverters;
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

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.StoryListCell;
import com.thirstygoat.kiqo.model.Story;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;


/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneView implements FxmlView<BacklogDetailsPaneViewModel>, Initializable {

    @InjectViewModel
    private BacklogDetailsPaneViewModel viewModel;
    
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label productOwnerLabel;
    @FXML
    private Label scaleLabel;
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

        shortNameLabel.textProperty().bind(viewModel.shortNameProperty());
        longNameLabel.textProperty().bind(viewModel.longNameProperty());
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
        productOwnerLabel.textProperty().bindBidirectional(viewModel.productOwnerProperty(),
                StringConverters.personStringConverter(viewModel.organisationProperty()));
        scaleLabel.textProperty().bindBidirectional(viewModel.scaleProperty(), StringConverters.scaleStringConverter());
        setHyperlink();

        shortNameTableColumn.setCellFactory(param -> {
            StoryListCell storyListCell = new StoryListCell(viewModel);
            storyListCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() > 1) {
                    MainController.focusedItemProperty.set((Story) storyListCell.getTableRow().getItem());
                }
            });
            return storyListCell;
        });
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(viewModel.stories());

        placeHolder.textProperty().set(viewModel.PLACEHOLDER);

        scaleLabel.textProperty().bindBidirectional(viewModel.scaleProperty(), StringConverters.scaleStringConverter());

        highlightCheckBox.selectedProperty().bindBidirectional(viewModel.highlightStoryStateProperty());
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

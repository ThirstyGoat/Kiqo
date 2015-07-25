package com.thirstygoat.kiqo.viewModel.detailsPane;

import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.viewModel.StoryListCell;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;


//@Override
//public void initialize(URL location, ResourceBundle resources) {
//        storyTableView.setPlaceholder(placeHolder);
//        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        }

//public void bindFields() {
//        placeHolder.textProperty().set(viewModel.PLACEHOLDER);
//        storyTableView.setItems(viewModel.stories());
//        }

/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneView implements FxmlView<BacklogDetailsPaneViewModel>, Initializable {

    @InjectViewModel
    private BacklogDetailsPaneViewModel backlogDetailsPaneViewModel;
    
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

        shortNameLabel.textProperty().bind(backlogDetailsPaneViewModel.shortNameProperty());
        longNameLabel.textProperty().bind(backlogDetailsPaneViewModel.longNameProperty());
        descriptionLabel.textProperty().bind(backlogDetailsPaneViewModel.descriptionProperty());
        productOwnerLabel.textProperty().bind(backlogDetailsPaneViewModel.productOwnerStringProperty());
        scaleLabel.textProperty().bind(backlogDetailsPaneViewModel.scaleStringProperty());
        setHyperlink();

        setStoryCellFactory();
        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.setItems(backlogDetailsPaneViewModel.getStories());

        placeHolder.textProperty().set(backlogDetailsPaneViewModel.PLACEHOLDER);

        scaleLabel.textProperty().bind(backlogDetailsPaneViewModel.scaleProperty().asString());

        highlightCheckBox.selectedProperty().bindBidirectional(backlogDetailsPaneViewModel.highlightStoryStateProperty());
    }

    private void setHyperlink() {
        PopOver popOver = new PopOver();
        popOver.setDetachable(false);

        HBox greenHbox = new HBox();
        HBox orangeHbox = new HBox();
        HBox redHbox = new HBox();
        HBox transparentHbox = new HBox();

        VBox vb = new VBox();

        vb.getChildren().addAll(greenHbox, orangeHbox, redHbox, transparentHbox);
        vb.setSpacing(2);
        greenHbox.setAlignment(Pos.CENTER_LEFT);
        orangeHbox.setAlignment(Pos.CENTER_LEFT);
        redHbox.setAlignment(Pos.CENTER_LEFT);
        transparentHbox.setAlignment(Pos.CENTER_LEFT);
        greenHbox.setSpacing(5);
        orangeHbox.setSpacing(5);
        redHbox.setSpacing(5);
        transparentHbox.setSpacing(5);

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

    private void setStoryCellFactory() {
        shortNameTableColumn.setCellFactory(param -> new StoryListCell(backlogDetailsPaneViewModel));
    }
}

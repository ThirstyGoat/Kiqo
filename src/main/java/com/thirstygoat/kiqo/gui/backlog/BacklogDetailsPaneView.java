package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.StoryListCell;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelComboBox;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.DirectedData;
import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.Edge;
import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.GraphVisualiser;
import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.Vertex;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.util.*;


/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class BacklogDetailsPaneView implements FxmlView<BacklogDetailsPaneViewModel>, Initializable {

    @InjectViewModel
    private BacklogDetailsPaneViewModel viewModel;
    
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private GoatLabelTextField descriptionLabel;
    @FXML
    private GoatLabelTextField productOwnerLabel;
    @FXML
    private GoatLabelComboBox<Scale> scaleLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;
    @FXML
    private CheckBox highlightCheckBox;
    @FXML
    private Hyperlink highlightHyperLink;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private VBox detailsVbox;
    @FXML
    private AnchorPane visualisationVbox;
    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private ToggleButton visualisationToggleButton;
    @FXML
    private FlowPane visualisationPane;

    private Label placeHolder = new Label();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        FxUtils.initGoatLabel(productOwnerLabel, viewModel, viewModel.productOwnerProperty(), StringConverters.personStringConverter(viewModel.organisationProperty()),
                viewModel.productOwnerValidation());
        FxUtils.setTextFieldSuggester(productOwnerLabel.getEditField(), viewModel.productOwnerSupplier());
        FxUtils.initGoatLabel(scaleLabel, viewModel, Scale.values(), viewModel.scaleProperty(),
                StringConverters.scaleStringConverter());

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

        highlightCheckBox.selectedProperty().bindBidirectional(viewModel.highlightStoryStateProperty());

        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else {
                if (newValue == detailsToggleButton) {
                    showNode(detailsVbox);
                } else if (newValue == visualisationToggleButton) {
                    showNode(visualisationVbox);
                }
            }
        });
        visualiseDependencies();
        viewModel.stories().addListener((observable, oldValue, newValue) -> {
            visualiseDependencies();
        });
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

    /**
     * Draws the dependency graph
     */
    private void visualiseDependencies() {
        visualisationPane.getChildren().clear();
        GraphVisualiser<Story> gv = new GraphVisualiser<>();
        gv.setNodeCallback(story -> {
            VBox node = new VBox();
            node.setPadding(new Insets(5, 10, 5, 10));
            node.setAlignment(Pos.CENTER);

            Label label = new Label();
            label.textProperty().bind(story.shortNameProperty());
            label.setPadding(new Insets(5, 5, 5, 5));
            node.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    MainController.focusedItemProperty.setValue(story);
                }
            });

            Label priority = new Label();
            priority.textProperty().bind(Bindings.createStringBinding(() -> Float.toString(story.priorityProperty().get()), story.priorityProperty()));

            node.getChildren().addAll(label, priority);
            long count = story.getDependencies().stream().filter(dependency -> dependency.priorityProperty().get() > story.priorityProperty().get()).count();
            if (count > 0) {
                node.getStyleClass().add("in-progress");
            } else {
                node.getStyleClass().add("done");
            }
            return node;
        });

        Map<Story, Vertex<Story>> storyVertexMap = new HashMap<>();
        for (Story story : viewModel.stories()) {
            storyVertexMap.put(story, new Vertex<>(story));
        }

        Set<Edge<Story>> edges = new HashSet<>();
        for (Story story : viewModel.stories()) {
            for (DirectedData<Story> dependent : story.getDirectedChildren()) {
                edges.add(new Edge<>(storyVertexMap.get(story), storyVertexMap.get(dependent.get())));
            }
        }

        gv.getVertices().addAll(storyVertexMap.values());
        gv.getEdges().addAll(edges);
        gv.go();

        visualisationPane.setAlignment(Pos.CENTER);
        visualisationPane.getChildren().add(gv);
    }

    /**
     * Hides all views
     */
    private void hideAllViews() {
        visualisationVbox.setVisible(false);
        visualisationVbox.setManaged(false);

//        scrumBoardView.setVisible(false);
//        scrumBoardView.setManaged(false);
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

package com.thirstygoat.kiqo.gui.nodes.GoatTree;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bradley on 22/09/15.
 */
public class GoatTree<T> extends StackPane {
    private ObjectProperty<List<HierarchicalData<T>>> root = new SimpleObjectProperty<>();
    private IntegerProperty nodeChildSpacing = new SimpleIntegerProperty(50);
    private IntegerProperty siblingSpacing = new SimpleIntegerProperty(30);

    private HBox rootElements = new HBox();
    private Pane branchPane = new Pane();

    private Label label = new Label();

    public GoatTree() {
        // Attach a change listener to the rootProperty, so we can draw the tree, when the root changes
        rootProperty().addListener((obs, oldValue, newValue) -> drawTree());

        rootElements.spacingProperty().bind(siblingSpacingProperty());
        rootElements.setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(rootElements, branchPane, label);
        Line randomLine = new Line();
        randomLine.setStartX(100);
        randomLine.setStartY(100);
        randomLine.setStartX(300);
        randomLine.setStartY(300);
        branchPane.getChildren().add(randomLine);
    }

    private void drawTree() {
        branchPane.getChildren().clear();
        rootElements.getChildren().setAll(getRoot().stream().map(this::draw).collect(Collectors.toList()));
    }

    private VBox draw(HierarchicalData<T> node) {
        VBox vBox = new VBox();
        vBox.setStyle("-fx-border-color: gainsboro");
        vBox.spacingProperty().bind(nodeChildSpacingProperty());
        vBox.setAlignment(Pos.CENTER);

        Node label = new Label("Point"); // Sample label
        label.setStyle("-fx-border-color: gainsboro");

        HBox children = new HBox();
        children.setStyle("-fx-border-color: gainsboro");
        children.spacingProperty().bind(siblingSpacingProperty());

        children.setAlignment(Pos.TOP_CENTER);
        for (HierarchicalData<T> child : node.getChildren()) {
            Node childNode = draw(child);
            children.getChildren().add(childNode);
            drawBranch(label, childNode);
        }

        vBox.getChildren().addAll(label, children);
        return vBox;
    }

    /**
     * Draws a branch from the source to the destination
     * @param source
     * @param destination
     */
    private void drawBranch(Node source, Node destination) {
        // Strange things happen when this line is removed.
        rootElements.boundsInParentProperty().addListener((obs, oldV, newV) -> {});


        Line line = new Line();
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(0);

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> getMidX(source), rootElements.boundsInParentProperty()));

        line.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            return source.localToScene(source.getLayoutBounds()).getMaxY();
        }, rootElements.boundsInParentProperty()));

        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            return getHeightBetween(source, destination);
        }, rootElements.boundsInParentProperty()));

        line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            return getWidthBetween(source, destination);
        }, rootElements.boundsInParentProperty()));


        branchPane.getChildren().add(line);


        source.localToScene(source.getBoundsInParent());
    }

    private double getMidX(Node node) {
        return (node.localToScene(node.getLayoutBounds()).getMinX() +
                node.localToScene(node.getLayoutBounds()).getMaxX()) / 2;
    }

    private double getHeightBetween(Node node1, Node node2) {
        double bottom = node1.localToScene(node1.getLayoutBounds()).getMaxY();
        double top = node2.localToScene(node2.getLayoutBounds()).getMinY();
        return Math.abs(top - bottom);
    }

    private  double getWidthBetween(Node node1, Node node2) {
        return getMidX(node2) - getMidX(node1);
    }

    public ObjectProperty<List<HierarchicalData<T>>> rootProperty() {
        return root;
    }

    public List<HierarchicalData<T>> getRoot() {
        return root.get();
    }

    public void setRoot(List<HierarchicalData<T>> root) {
        this.root.set(root);
    }

    public int getNodeChildSpacing() {
        return nodeChildSpacing.get();
    }

    public IntegerProperty nodeChildSpacingProperty() {
        return nodeChildSpacing;
    }

    public void setSpacing(int spacingProperty) {
        nodeChildSpacing.set(spacingProperty);
    }

    public int getSiblingSpacing() {
        return siblingSpacing.get();
    }

    public void setSiblingSpacing(int siblingSpacing) {
        this.siblingSpacing.set(siblingSpacing);
    }

    public IntegerProperty siblingSpacingProperty() {
        return siblingSpacing;
    }
}

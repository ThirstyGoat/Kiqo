package com.thirstygoat.kiqo.gui.nodes.GoatTree;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bradley on 22/09/15.
 */
public class GoatTree<T> extends StackPane {
    private ObjectProperty<List<HierarchicalData<T>>> root = new SimpleObjectProperty<>();
    private IntegerProperty nodeChildSpacing = new SimpleIntegerProperty(50);
    private IntegerProperty siblingSpacing = new SimpleIntegerProperty(30);
    private ObjectProperty<Callback<T, Node>> nodeFactory = new SimpleObjectProperty<>();

    private TilePane nodes = new TilePane();
    private Pane branchPane = new Pane();

    private Map<T, Node> nodeMap = new HashMap<>();

    public GoatTree() {
        // Attach a change listener to the rootProperty, so we can draw the tree, when the root changes
        rootProperty().addListener((obs, oldValue, newValue) -> drawTree());

        // Default Node Factory, simply returns each node as a label marked with X
        setNodeFactory(T -> new Label("X"));

        getChildren().addAll(nodes, branchPane);
    }

    public void setNodeFactory(Callback<T, Node> callback) {
        nodeFactory.set(callback);
    }

    public Callback<T, Node> getCallback() {
        return nodeFactory.get();
    }

    private void drawTree() {
        branchPane.getChildren().clear();
        nodes.getChildren().setAll(getRoot().stream().map(this::draw).collect(Collectors.toList()));
    }

    private Node draw(HierarchicalData<T> node) {
        Node object = getCallback().call(node.getItem());

        // Add the object to the nodeMap so we can keep track of it, and don't accidentally re-create it
        nodeMap.put(node.getItem(), object);

        for (HierarchicalData<T> child : node.getChildren()) {
            if (nodeMap.containsKey(child.getItem())) {
                // Then we don't need to re-draw it, just draw a line to it
                drawBranch(object, nodeMap.get(child.getItem()));
            } else {
                Node object1 = draw(child);
                drawBranch(object, object1);
            }
        }

        nodes.getChildren().add(object);

        return object;
    }

    /**
     * Draws a branch from the source to the destination
     * @param source
     * @param destination
     */
    private void drawBranch(Node source, Node destination) {
        // Strange things happen when this line is removed.
        nodes.boundsInParentProperty().addListener((obs, oldV, newV) -> {});

        Line line = new Line();
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(0);

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> getMidX(source), nodes.boundsInParentProperty()));
        line.layoutYProperty().bind(Bindings.createDoubleBinding(() -> getMidY(source), nodes.boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> getHeightBetween(source, destination), nodes.boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> getWidthBetween(source, destination), nodes.boundsInParentProperty()));

        branchPane.getChildren().add(line);

        source.localToScene(source.getBoundsInParent());
    }

    private double getMidX(Node node) {
        return (node.localToScene(node.getLayoutBounds()).getMinX() +
                node.localToScene(node.getLayoutBounds()).getMaxX()) / 2;
    }

    private double getMidY(Node node) {
        return (node.localToScene(node.getLayoutBounds()).getMinY() +
                node.localToScene(node.getLayoutBounds()).getMaxY()) / 2;
    }

    private double getHeightBetween(Node node1, Node node2) {
        return getMidY(node2) - getMidY(node1);
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

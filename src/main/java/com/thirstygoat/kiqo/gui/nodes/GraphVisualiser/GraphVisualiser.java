package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bradley on 23/09/15.
 */
public class GraphVisualiser<T> extends FlowPane {
    private Callback<T, Node> nodeCallback = t -> new Label("X");

    private Map<String, Vertex<T>> vertexMap = new HashMap<>();

    private ObservableList<Vertex<T>> vertices = FXCollections.observableArrayList();
    private ObservableList<Edge<T>> edges = FXCollections.observableArrayList();

    public GraphVisualiser() {
    }

    public void setNodeCallback(Callback<T, Node> nodeCallback) {
        this.nodeCallback = nodeCallback;
    }

    public void test() {
        Arrow a = new Arrow();
        a.layoutXProperty().set(100);
        a.layoutYProperty().set(100);
        a.endXProperty().set(100);
        a.endYProperty().set(-50);
        getChildren().add(a);
    }

    public void go() {

    }

    private Set<Vertex<T>> getConnected(Vertex<T> vertex) {
        return null;
    }

    private void addEdgeToSet(Edge<T> edge, Set<Vertex<T>> vertices) {
        vertices.add(edge.getStart());
        vertices.add(edge.getEnd());
    }

    private void computePositions(ObservableList<Vertex<T>> vertices, ObservableList<Edge<T>> edges) {
        String resourcePath = getClass().getClassLoader().getResource("grandalf/").getFile();

        PythonInterpreter py = new PythonInterpreter();
        py.exec("import sys");
        py.exec("sys.path.append('" + resourcePath + "')");
        py.exec("from vertexCoords import *");

        PyFunction getVertexPositions = (PyFunction)py.get("getVertexPositions");
        PyFunction createVertexFunction = (PyFunction)py.get("createVertex");
        PyFunction createEdgeFunction = (PyFunction)py.get("createEdge");

        PyList verticesPy = new PyList();
        PyList edgesPy = new PyList();

        Map<String, PyObject> pyObjectMap = new HashMap<>();

        // Iterate over all vertices and create PyObject representing vertex
        for (Vertex<T> vertex : vertices) {
            String hash = String.valueOf(vertex.hashCode());
            vertexMap.put(hash, vertex);
            PyObject v = createVertexFunction.__call__(new PyString(hash));
            pyObjectMap.put(hash, v);
            verticesPy.add(v);
        }

        for (Edge<T> edge : edges) {
            PyObject start = pyObjectMap.get(String.valueOf(edge.getStart().hashCode()));
            PyObject end = pyObjectMap.get(String.valueOf(edge.getEnd().hashCode()));
            edgesPy.add(createEdgeFunction.__call__(start, end));
        }

        PyList positions = (PyList)getVertexPositions.__call__(verticesPy, edgesPy);

        for (Object list : positions) {
            PyList l = (PyList)list;
            String hash = (String) l.get(0);
            Vertex<T> vertex = vertexMap.get(hash);
            double xPos = (Double) l.get(1);
            double yPos = (Double) l.get(2);
            vertex.xPosProperty().set(xPos);
            vertex.yPosProperty().set(yPos);
        }

        getChildren().add(drawGraph(vertices, edges));
    }

    private double getMinXPos(List<Vertex<T>> vertices) {
        return vertices.stream().mapToDouble(vertex -> vertex.xPosProperty().get()).min().getAsDouble();
    }

    private double getMinYPos(List<Vertex<T>> vertices) {
        return vertices.stream().mapToDouble(vertex -> vertex.yPosProperty().get()).min().getAsDouble();
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

    /**
     * Draws a branch from the source to the destination
     * @param source
     * @param destination
     */
    private void drawBranch(Node source, Node destination, Pane section) {
        // Strange things happen when this line is removed.
        boundsInParentProperty().addListener((obs, oldV, newV) -> {
        });

        Arrow line = new Arrow();

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> getMidX(source), boundsInParentProperty()));
        line.layoutYProperty().bind(Bindings.createDoubleBinding(() -> getMidY(source), boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> getHeightBetween(source, destination), boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> getWidthBetween(source, destination), boundsInParentProperty()));

        section.getChildren().add(line);
        line.toBack();
    }

    private double getAngleBetweenNodes(Node source, Node destination) {
        double x1 = (source.getLayoutBounds().getMinX() + source.getLayoutBounds().getMaxX()) / 2;
        double y1 = (source.getLayoutBounds().getMinY() + source.getLayoutBounds().getMaxY()) / 2;

        double x2 = (destination.getLayoutBounds().getMinX() + destination.getLayoutBounds().getMaxX()) / 2;
        double y2 = (destination.getLayoutBounds().getMinY() + destination.getLayoutBounds().getMaxY()) / 2;

        return 0; // TODO In Progress
    }

    private Pane drawGraph(ObservableList<Vertex<T>> vertices, ObservableList<Edge<T>> edges) {
        Pane pane = new Pane();
        double xOffset = Math.min(0, getMinXPos(vertices));
        double yOffset = Math.min(0, getMinYPos(vertices));

        Map<Vertex<T>, Node> vertexNodeMap = new HashMap<>();

        for (Vertex<T> vertex : vertices) {
            Node node = nodeCallback.call(vertex.getObject());
            vertexNodeMap.put(vertex, node);

            node.layoutXProperty().bind(Bindings.add(-xOffset, vertex.xPosProperty()));
            node.layoutYProperty().bind(Bindings.add(-yOffset, vertex.yPosProperty()));

            pane.getChildren().add(node);
        }

        for (Edge<T> edge : edges) {
            drawBranch(vertexNodeMap.get(edge.getStart()), vertexNodeMap.get(edge.getEnd()), pane);
        }

        return pane;
    }

    public ObservableList<Edge<T>> getEdges() {
        return edges;
    }

    public ObservableList<Vertex<T>> getVertices() {
        return vertices;
    }
}
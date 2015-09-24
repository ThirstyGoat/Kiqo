package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bradley on 23/09/15.
 */
public class GraphVisualiser<T> extends FlowPane {
    private Callback<T, Node> nodeCallback = t -> new Label("X");

    private Map<String, Vertex<T>> vertexMap = new HashMap<>();

    private ObservableSet<Vertex<T>> vertices = FXCollections.observableSet();
    private ObservableSet<Edge<T>> edges = FXCollections.observableSet();

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
        for (Set<Vertex<T>> s : getConnectedSubGraphs(getVertices(), getEdges())) {
            Set<Edge<T>> edgeSet = getEdges().stream().filter(
                    e -> s.contains(e.getStart()) || s.contains(e.getEnd())).collect(Collectors.toSet());
            computePositions(s, edgeSet);

        }
    }

    private void computePositions(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
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

    private double getMinXPos(Set<Vertex<T>> vertices) {
        return vertices.stream().mapToDouble(vertex -> vertex.xPosProperty().get()).min().getAsDouble();
    }

    private double getMinYPos(Set<Vertex<T>> vertices) {
        return vertices.stream().mapToDouble(vertex -> vertex.yPosProperty().get()).min().getAsDouble();
    }

    private double getMidX(Node node) {
        return (node.getBoundsInParent().getMinX() + node.getBoundsInParent().getMaxX()) / 2;
    }

    private double getMidY(Node node) {
        return (node.getBoundsInParent().getMinY() + node.getBoundsInParent().getMaxY()) / 2;
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

    private Pane drawGraph(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
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

    /**
     * Splits the given graph (made up of vertices and edges) into its connected sub-graphs
     * @param vertices vertices of the graph
     * @param edges edges of the graph
     * @return a set containing the sets of vertices for each connected sub-graph
     */
    public static <T> Set<Set<Vertex<T>>> getConnectedSubGraphs(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
        Set<Set<Vertex<T>>> totalSet = new HashSet<>();

        while (!vertices.isEmpty()) {
            Set<Vertex<T>> island = new HashSet<>();
            floodFill(vertices.iterator().next(), island, edges);
            vertices.removeAll(island);
            totalSet.add(island);
        }
        return totalSet;
    }

    /**
     * Recursively perform the flood fill algorithm
     */
    private static <T> void floodFill(Vertex<T> vertex, Set<Vertex<T>> island, Set<Edge<T>> edges) {
        if (!island.contains(vertex)) {
            island.add(vertex);
        } else {
            return;
        }
        getConnectedVertices(vertex, edges).forEach(v -> floodFill(v, island, edges));
    }

    /**
     * Gets all the vertices connected directly to the given vertex
     * @param vertex the start vertex
     * @param edges all the edges in the graph
     * @return
     */
    private static <T> Set<Vertex<T>> getConnectedVertices(Vertex<T> vertex, Set<Edge<T>> edges) {
        Set<Vertex<T>> connectedVertices = new HashSet<>();

        edges.stream().filter(edge -> edge.getStart().equals(vertex) || edge.getEnd().equals(vertex))
                .forEach(edge1 -> {
                    connectedVertices.add(edge1.getStart());
                    connectedVertices.add(edge1.getEnd());
                });
        return connectedVertices;
    }

    public ObservableSet<Edge<T>> getEdges() {
        return edges;
    }

    public ObservableSet<Vertex<T>> getVertices() {
        return vertices;
    }
}
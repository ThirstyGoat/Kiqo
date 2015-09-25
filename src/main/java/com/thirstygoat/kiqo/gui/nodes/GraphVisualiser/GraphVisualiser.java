package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private Callback<T, Node> nodeCallback;
    private Map<String, Vertex<T>> vertexMap = new HashMap<>();
    private ObservableSet<Vertex<T>> vertices = FXCollections.observableSet();
    private ObservableSet<Edge<T>> edges = FXCollections.observableSet();
    private static PythonInterpreter py;

    public GraphVisualiser() {
        // Set the default nodeCallback to create the display node
        // for a given object of type T
        // Default is simply a label with X
        setAlignment(Pos.CENTER);
        nodeCallback = t -> new Label("X");
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

    public void setNodeCallback(Callback<T, Node> nodeCallback) {
        this.nodeCallback = nodeCallback;
    }

    /**
     * Draws the graph. finds all connected subgraphs, positions all nodes and adds them to the flow pane
     */
    public void go() {
        Thread t1 = new Thread(() -> {
            // code goes here.
            for (Set<Vertex<T>> s : getConnectedSubGraphs(getVertices(), getEdges())) {
                Set<Edge<T>> edgeSet = getEdges().stream().filter(
                        e -> s.contains(e.getStart()) || s.contains(e.getEnd())).collect(Collectors.toSet());

                // Graphs with fewer than 3 vertices and 2 edges can not be spaced
                // by the algorithm implementation. We set their spacing manually.
                if (s.size() > 2) {
                    computePositions(s, edgeSet);
                } else {
                    computePositionsMicro(s, edgeSet);
                }
            }
        });
        t1.start();
    }

    private void computePositionsMicro(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
        double verticalSeparation = 150;
        double yPos = 0;
        for (Vertex<T> vertex : vertices) {
            vertex.xPosProperty().set(0);
            vertex.yPosProperty().set(yPos);
            yPos += verticalSeparation;
        }
        Platform.runLater(() -> getChildren().add(drawGraph(vertices, edges)));
    }

    /**
     * Uses the python library grandalf to generate the x, y coordinates for a set of connected vertices
     * @param vertices
     * @param edges
     */
    private void computePositions(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
        String resourcePath = getClass().getClassLoader().getResource("grandalf/").getFile();

        py = new PythonInterpreter();
        py.exec("import sys");
        py.exec("sys.path.append('" + resourcePath + "')");
        py.exec("from grandalf.vertexCoords import *");

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

        Platform.runLater(() -> getChildren().add(drawGraph(vertices, edges)));
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

    /**
     * calculates the y value for the point where a line from the source to the destination will intersect with the
     * border of the destination node
     * @param source
     * @param destination
     * @return
     */
    private double getHeightBetween(Node source, Node destination) {
        double w = destination.getLayoutBounds().getWidth() / 2;
        double theta = getAngleBetweenNodesRads(source, destination);
        double offset = Math.min(w * Math.tan(theta), destination.getLayoutBounds().getHeight() / 2);

        if (getMidY(destination) > getMidY(source)) {
            return getMidY(destination) - getMidY(source) - offset;
        }
        return getMidY(destination) - getMidY(source) + offset;

    }

    /**
     * calculates the x value for the point where a line from the source to the destination will intersect with the
     * border of the destination node
     * @param source
     * @param destination
     * @return
     */
    private  double getWidthBetween(Node source, Node destination) {
        double h = destination.getLayoutBounds().getHeight() / 2;
        double theta = Math.toRadians(90) - getAngleBetweenNodesRads(source, destination);
        double offset = Math.min(h * theta, destination.getLayoutBounds().getWidth() / 2);

        if (getMidX(destination) > getMidX(source)) {
            return getMidX(destination) - getMidX(source) - offset;
        }
        return getMidX(destination) - getMidX(source) + offset;
    }

    /**
     * Gets the the angle at the source end of the line between two nodes assuming that the line connecting them is
     * the hypotenuse of a right angled triangle
     * @param source
     * @param destination
     * @return
     */
    private double getAngleBetweenNodesRads(Node source, Node destination) {
        double x1 = source.getBoundsInParent().getMinX();
        double y1 = source.getBoundsInParent().getMinY();

        double x2 = destination.getBoundsInParent().getMinX();
        double y2 = destination.getBoundsInParent().getMinY();

        double xDiff = Math.max(0.000001, Math.abs(x1 - x2));
        double yDiff = Math.abs(y1 - y2);

        return Math.atan(yDiff / xDiff);
    }

    /**
     * Draws a branch from the source to the destination
     * @param source
     * @param destination
     */
    private void drawBranch(Node source, Node destination, Pane section) {
        // Strange things happen when this line is removed.
        boundsInParentProperty().addListener((obs, oldV, newV) -> {});

        Arrow line = new Arrow();
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> getMidX(source), boundsInParentProperty()));
        line.layoutYProperty().bind(Bindings.createDoubleBinding(() -> getMidY(source), boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> getHeightBetween(source, destination), boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> getWidthBetween(source, destination), boundsInParentProperty()));

        section.getChildren().add(line);
        line.toBack();
    }

    /**
     * Draws a graph from a set of vertices and edges to a pane containing nodes supplied by the vertices
     * @param vertices
     * @param edges
     * @return pane containing the nodes (vertices) connected by edges
     */
    private Pane drawGraph(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10, 10, 10, 10));
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

    public ObservableSet<Edge<T>> getEdges() {
        return edges;
    }

    public ObservableSet<Vertex<T>> getVertices() {
        return vertices;
    }

    public static PythonInterpreter startPython() {
        if (py == null) {
            py = new PythonInterpreter();
        }
        return py;
    }

}
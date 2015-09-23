package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradley on 23/09/15.
 */
public class GraphVisualiser<T> extends Pane {

    private ObservableList<Vertex<T>> vertices = FXCollections.observableArrayList();
    private ObservableList<Edge<T>> edges = FXCollections.observableArrayList();
    private Callback<T, Node> nodeCallback = t -> new Label("X");

    private Map<String, Vertex<T>> vertexMap = new HashMap<>();

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

    public void computePositions() {
        String resourcePath = getClass().getClassLoader().getResource("grandalf/").getFile();

        PythonInterpreter py = new PythonInterpreter();
        py.exec("import sys");
        py.exec("sys.path.append('" + resourcePath + "')");
        py.exec("from vertexCoords import *");

        PyFunction getVertexPositions = (PyFunction)py.get("getVertexPositions");
        PyFunction createVertexFunction = (PyFunction)py.get("createVertex");
        PyFunction createEdgeFunction = (PyFunction)py.get("createEdge");

        PyList vertices = new PyList();
        PyList edges = new PyList();

        Map<String, PyObject> pyObjectMap = new HashMap<>();

        // Iterate over all vertices and create PyObject representing vertex
        for (Vertex<T> vertex : getVertices()) {
            String hash = String.valueOf(vertex.hashCode());
            vertexMap.put(hash, vertex);
            PyObject v = createVertexFunction.__call__(new PyString(hash));
            pyObjectMap.put(hash, v);
            vertices.add(v);
        }

        for (Edge<T> edge : getEdges()) {
            PyObject start = pyObjectMap.get(String.valueOf(edge.getStart().hashCode()));
            PyObject end = pyObjectMap.get(String.valueOf(edge.getEnd().hashCode()));
            edges.add(createEdgeFunction.__call__(start, end));
        }

        PyList positions = (PyList)getVertexPositions.__call__(vertices, edges);

        for (Object list : positions) {
            PyList l = (PyList)list;
            String hash = (String) l.get(0);
            Vertex<T> vertex = vertexMap.get(hash);
            double xPos = (Double) l.get(1);
            double yPos = (Double) l.get(2);
            vertex.xPosProperty().set(xPos);
            vertex.yPosProperty().set(yPos);
        }
        drawGraph();
    }

    private double getMinXPos() {
        return getVertices().stream().mapToDouble(vertex -> vertex.xPosProperty().get()).min().getAsDouble();
    }

    private double getMinYPos() {
        return getVertices().stream().mapToDouble(vertex -> vertex.yPosProperty().get()).min().getAsDouble();
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
    private void drawBranch(Node source, Node destination) {
        // Strange things happen when this line is removed.
        boundsInParentProperty().addListener((obs, oldV, newV) -> {
        });

        Arrow line = new Arrow();


        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> getMidX(source), boundsInParentProperty()));
        line.layoutYProperty().bind(Bindings.createDoubleBinding(() -> getMidY(source), boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> getHeightBetween(source, destination), boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> getWidthBetween(source, destination), boundsInParentProperty()));

        getChildren().add(line);
        line.toBack();



//        source.localToScene(source.getBoundsInParent());
    }

    private void drawGraph() {
        double xOffset = Math.min(0, getMinXPos());
        double yOffset = Math.min(0, getMinYPos());

        Map<Vertex<T>, Node> vertexNodeMap = new HashMap<>();

        for (Vertex<T> vertex : getVertices()) {
            Node node = nodeCallback.call(vertex.getObject());
            vertexNodeMap.put(vertex, node);

            node.layoutXProperty().bind(Bindings.add(-xOffset, vertex.xPosProperty()));
            node.layoutYProperty().bind(Bindings.add(-yOffset, vertex.yPosProperty()));

            getChildren().add(node);
        }

        for (Edge<T> edge : getEdges()) {
            drawBranch(vertexNodeMap.get(edge.getStart()), vertexNodeMap.get(edge.getEnd()));
        }
    }

    public ObservableList<Vertex<T>> getVertices() {
        return vertices;
    }

    public ObservableList<Edge<T>> getEdges() {
        return edges;
    }
}
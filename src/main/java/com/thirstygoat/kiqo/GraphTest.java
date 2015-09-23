package com.thirstygoat.kiqo;

import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.Edge;
import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.GraphVisualiser;
import com.thirstygoat.kiqo.gui.nodes.GraphVisualiser.Vertex;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bradley on 23/09/15.
 */
public class GraphTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void run() {
        String resourcePath = getClass().getClassLoader().getResource("grandalf/").getFile();

        PythonInterpreter py = new PythonInterpreter();
        py.exec("import sys");
        py.exec("sys.path.append('" + resourcePath + "')");
        py.exec("from vertexCoords import *");

        PyFunction getVertexPositions = (PyFunction)py.get("getVertexPositions");

        PyFunction createVertexFunction = (PyFunction)py.get("createVertex");
        PyFunction createEdgeFunction = (PyFunction)py.get("createEdge");

        PyObject vertexA = createVertexFunction.__call__(new PyString("A"), new PyInteger(10), new PyInteger(10));
        PyObject vertexB = createVertexFunction.__call__(new PyString("B"), new PyInteger(10), new PyInteger(10));
        PyObject vertexC = createVertexFunction.__call__(new PyString("C"), new PyInteger(10), new PyInteger(10));

        PyObject edgeAB = createEdgeFunction.__call__(vertexA, vertexB);
        PyObject edgeBC = createEdgeFunction.__call__(vertexB, vertexC);

        PyList vertices = new PyList();
        vertices.add(vertexA);
        vertices.add(vertexB);
        vertices.add(vertexC);
        PyList edges = new PyList();
        edges.add(edgeAB);
        edges.add(edgeBC);

        PyList positions = (PyList)getVertexPositions.__call__(vertices, edges);
        System.out.println(positions);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GraphVisualiser<Point> gv = new GraphVisualiser<>();
        gv.setNodeCallback(point -> {
            Label label = new Label(point.getLabel());
//            label.setStyle("-fx-background-color: white");
            return label;
        });

        Point a = new Point("A");
        Point b = new Point("B");
        Point c = new Point("C");
        Point d = new Point("D");
        Point e = new Point("E");

        Point f = new Point("F");
        Point g = new Point("G");

        a.getDependents().add(b);
        a.getDependents().add(c);
        a.getDependents().add(d);
        a.getDependents().add(e);

        b.getDependents().add(d);
        c.getDependents().add(d);
        e.getDependents().add(d);

        f.getDependents().add(g);

        Map<Point, Vertex<Point>> map = new HashMap<>();
        map.put(a, new Vertex<>(a));
        map.put(b, new Vertex<>(b));
        map.put(c, new Vertex<>(c));
        map.put(d, new Vertex<>(d));
        map.put(e, new Vertex<>(e));
        map.put(f, new Vertex<>(f));
        map.put(g, new Vertex<>(g));


        for (Vertex<Point> vertex : map.values()) {
            gv.getVertices().add(vertex);
        }

        List<Edge<Point>> edges = new ArrayList<>();
        for (Vertex<Point> vertex : gv.getVertices()) {
            Point point = vertex.getObject();
            for (DirectedData<Point> dependent : point.getDependents()) {
                edges.add(new Edge<>(vertex, map.get(dependent.get())));
            }
        }

        gv.getEdges().addAll(edges);

        gv.go();

        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(new Scene(gv));
        primaryStage.show();
    }

    interface DirectedData<T> {
        public T get();

        public List<DirectedData<T>> getDependents();
    }

    class Point implements DirectedData<Point> {
        List<DirectedData<Point>> dependents = new ArrayList<>();

        String label;

        public Point(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public Point get() {
            return this;
        }

        @Override
        public List<DirectedData<Point>> getDependents() {
            return dependents;
        }
    }
}



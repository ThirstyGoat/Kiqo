package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bradley on 24/09/15.
 */
public class GraphVisualiserTest {
    @Test
    public void base_case() {
        Vertex a = new Vertex<>("A");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(a);

        Set<Edge> edges = new HashSet<>();

        Set<Set<Vertex>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.contains(a)).hasSize(1);
    }

    @Test
    public void twoConnectedVertices() {
        Vertex a = new Vertex<>("A");
        Vertex b = new Vertex<>("B");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(a);
        vertices.add(b);

        Edge ab = new Edge<>(a, b);
        Set<Edge> edges = new HashSet<>();
        edges.add(ab);

        Set<Set<Vertex>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.containsAll(vertices)).hasSize(1);
    }

    @Test
    public void twoUnConnectedVertices() {
        Vertex a = new Vertex<>("A");
        Vertex b = new Vertex<>("B");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(a);
        vertices.add(b);

        Set<Edge> edges = new HashSet<>();

        Set<Set<Vertex>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.contains(a) || set.contains(b)).hasSize(2);
    }

    @Test
    public void fullGraphTest() {
        Vertex a = new Vertex<>("A");
        Vertex b = new Vertex<>("B");
        Vertex c = new Vertex<>("C");
        Vertex d = new Vertex<>("D");
        Vertex e = new Vertex<>("E");
        Vertex f = new Vertex<>("F");
        Vertex g = new Vertex<>("G");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);
        vertices.add(d);
        vertices.add(e);
        vertices.add(f);
        vertices.add(g);

        Edge ab = new Edge<>(a, b);
        Edge ac = new Edge<>(a, c);
        Edge ad = new Edge<>(a, d);
        Edge ae = new Edge<>(a, e);
        Edge bd = new Edge<>(b, d);
        Edge ed = new Edge<>(e, d);
        Edge cd = new Edge<>(c, d);
        Edge fg = new Edge<>(f, g);

        Set<Edge> edges = new HashSet<>();
        edges.add(ab);
        edges.add(ac);
        edges.add(ad);
        edges.add(ae);
        edges.add(bd);
        edges.add(ed);
        edges.add(cd);
        edges.add(fg);

        Set<Vertex> resultSetA = new HashSet<>();
        resultSetA.add(a);
        resultSetA.add(b);
        resultSetA.add(c);
        resultSetA.add(d);
        resultSetA.add(e);

        Set<Vertex> resultSetB = new HashSet<>();
        resultSetB.add(f);
        resultSetB.add(g);

        Set<Set<Vertex>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);

        Iterator<Set<Vertex>> i = newVertexSet.iterator();
        Set<Vertex> set1 = i.next();
        Set<Vertex> set2 = i.next();

        assertThat((set1.containsAll(resultSetA) && set2.containsAll(resultSetB)) ||
                (set1.containsAll(resultSetB) && set2.containsAll(resultSetA)));
    }


}

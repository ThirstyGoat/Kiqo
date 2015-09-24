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
        Vertex<String> a = new Vertex<>("A");

        Set<Vertex<String>> vertices = new HashSet<>();
        vertices.add(a);

        Set<Edge<String>> edges = new HashSet<>();

        Set<Set<Vertex<String>>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.contains(a)).hasSize(1);
    }

    @Test
    public void twoConnectedVertices() {
        Vertex<String> a = new Vertex<>("A");
        Vertex<String> b = new Vertex<>("B");

        Set<Vertex<String>> vertices = new HashSet<>();
        vertices.add(a);
        vertices.add(b);

        Edge<String> ab = new Edge<>(a, b);
        Set<Edge<String>> edges = new HashSet<>();
        edges.add(ab);

        Set<Set<Vertex<String>>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.containsAll(vertices)).hasSize(1);
    }

    @Test
    public void twoUnConnectedVertices() {
        Vertex<String> a = new Vertex<>("A");
        Vertex<String> b = new Vertex<>("B");

        Set<Vertex<String>> vertices = new HashSet<>();
        vertices.add(a);
        vertices.add(b);

        Set<Edge<String>> edges = new HashSet<>();

        Set<Set<Vertex<String>>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.contains(a) || set.contains(b)).hasSize(2);
    }

    @Test
    public void fullGraphTest() {
        Vertex<String> a = new Vertex<>("A");
        Vertex<String> b = new Vertex<>("B");
        Vertex<String> c = new Vertex<>("C");
        Vertex<String> d = new Vertex<>("D");
        Vertex<String> e = new Vertex<>("E");
        Vertex<String> f = new Vertex<>("F");
        Vertex<String> g = new Vertex<>("G");

        Set<Vertex<String>> vertices = new HashSet<>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);
        vertices.add(d);
        vertices.add(e);
        vertices.add(f);
        vertices.add(g);

        Edge<String> ab = new Edge<>(a, b);
        Edge<String> ac = new Edge<>(a, c);
        Edge<String> ad = new Edge<>(a, d);
        Edge<String> ae = new Edge<>(a, e);
        Edge<String> bd = new Edge<>(b, d);
        Edge<String> ed = new Edge<>(e, d);
        Edge<String> cd = new Edge<>(c, d);
        Edge<String> fg = new Edge<>(f, g);

        Set<Edge<String>> edges = new HashSet<>();
        edges.add(ab);
        edges.add(ac);
        edges.add(ad);
        edges.add(ae);
        edges.add(bd);
        edges.add(ed);
        edges.add(cd);
        edges.add(fg);

        Set<Vertex<String>> resultSetA = new HashSet<>();
        resultSetA.add(a);
        resultSetA.add(b);
        resultSetA.add(c);
        resultSetA.add(d);
        resultSetA.add(e);

        Set<Vertex<String>> resultSetB = new HashSet<>();
        resultSetB.add(f);
        resultSetB.add(g);

        Set<Set<Vertex<String>>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);

        Iterator<Set<Vertex<String>>> i = newVertexSet.iterator();
        Set<Vertex<String>> set1 = i.next();
        Set<Vertex<String>> set2 = i.next();

        assertThat((set1.containsAll(resultSetA) && set2.containsAll(resultSetB)) ||
                (set1.containsAll(resultSetB) && set2.containsAll(resultSetA)));
    }


}

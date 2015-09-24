package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by bradley on 24/09/15.
 */
public class GraphVisualiserTest {
    @Test
    public void base_case() {
        Vertex a = new Vertex<>("A");

        Set<Vertex> vertices = new HashSet<>();
        vertices.add(a);

        Set<Edge> edges = new HashSet<>();

//        GraphVisualiser<String> gv = new GraphVisualiser<>();

        Set<Set<Vertex>> newVertexSet = GraphVisualiser.getConnectedSubGraphs(vertices, edges);
        assertThat(newVertexSet).filteredOn(set -> set.contains(a)).hasSize(1);
    }
}

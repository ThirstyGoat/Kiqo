package com.thirstygoat.kiqo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leroy on 24/09/15.
 */
public class FloodFillAlgorithm {
    public ArrayList<List<Node>> islands;
    public ArrayList<Node> nodes;

    public static class Node {
        public ArrayList<Node> neighbors = new ArrayList<>();
        public String label;

        public Node(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public FloodFillAlgorithm(List<Node> nodes) {
        this.nodes = new ArrayList<>();
        this.nodes.addAll(nodes);
    }

    public void floodFill(Node node, List<Node> island) {
        if (!island.contains(node)) {
            island.add(node);
        } else {
            return;
        }
        node.neighbors.forEach(neighbor -> floodFill(neighbor, island));
    }

    public List<List<Node>> execute() {
        islands = new ArrayList<>();
        while (nodes.size() > 0) {
            ArrayList<Node> island = new ArrayList<>();
            floodFill(nodes.get(0), island);
            nodes.removeAll(island);
            islands.add(island);
        }
        return islands;
    }
}

package com.thirstygoat.kiqo.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leroy on 24/09/15.
 */
public class FloodFillAlgorithmTest {

    @Test
    public void floodFillAlgorithmTest() {
        FloodFillAlgorithm.Node A = new FloodFillAlgorithm.Node("A");
        FloodFillAlgorithm.Node B = new FloodFillAlgorithm.Node("B");
        FloodFillAlgorithm.Node C = new FloodFillAlgorithm.Node("C");
        FloodFillAlgorithm.Node D = new FloodFillAlgorithm.Node("D");
        FloodFillAlgorithm.Node E = new FloodFillAlgorithm.Node("E");
        FloodFillAlgorithm.Node F = new FloodFillAlgorithm.Node("F");
        A.neighbors.addAll(Arrays.asList(B, C));
        B.neighbors.addAll(Arrays.asList(A, C));
        C.neighbors.addAll(Arrays.asList(A, B, D));
        D.neighbors.add(C);
        E.neighbors.add(F);
        F.neighbors.add(E);
        ArrayList<FloodFillAlgorithm.Node> nodes = new ArrayList<>(Arrays.asList(A, B, C, D, E, F));

        FloodFillAlgorithm floodFillAlgorithm = new FloodFillAlgorithm(nodes);
        floodFillAlgorithm.execute();

        System.out.println(floodFillAlgorithm.islands);
    }
}

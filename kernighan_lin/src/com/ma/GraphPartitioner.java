package com.ma;

import java.util.HashSet;

/**
 * Solving the graph partitioning problem using the Kernighan–Lin algorithm is a heuristic algorithm.
 *
 * Created by simplay on 16/02/16.
 */
public class GraphPartitioner {

    private Graph graph;
    private final HashSet<Vertex> setA = new HashSet<Vertex>();
    private final HashSet<Vertex> setB = new HashSet<Vertex>();

    public GraphPartitioner(Graph graph) {
        this.graph = graph;
        // initially, set A is the empty set and set B is V
        for (Vertex v : graph.vertices) {
            setB.add(v);
        }

    }

    public void runKernighanLin() {
        for(Vertex v : graph.vertices) {
            v.computeD(setA, setB);
        }
    }
}

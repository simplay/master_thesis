package com.ma;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Solving the graph partitioning problem using the Kernighan–Lin algorithm is a heuristic algorithm.
 *
 * Created by simplay on 16/02/16.
 */
public class GraphPartitioner {

    private Graph graph;
    private final HashSet<Vertex> setA = new HashSet<>();
    private final HashSet<Vertex> setB = new HashSet<>();
    private final List<Float> gv = new ArrayList<>();
    private final List<Vertex> av = new ArrayList<>();
    private final List<Vertex> bv = new ArrayList<>();

    public GraphPartitioner(Graph graph) {
        this.graph = graph;

        // initially, set A is the empty set and set B is V
        for (Vertex v : graph.vertices) {
            setB.add(v);
        }

    }

    public void runKernighanLin() {
        float max_gv = 0.0f;
        do {
            // compute D values for all a in A and b in B
            for(Vertex v : graph.vertices) {
                v.computeD(setA, setB);
            }

            for (int n = 0; n <= (graph.vertexCount()/2) + 1; n++) {
                // find a from A and b from B, such that g = D[a] + D[b] - 2*E(a, b) is maximal
                // remove a and b from further consideration in this pass
                // add g to gv, a to av, and b to bv
                // update D values for the elements of A = A \ a and B = B \ b
            }

            // find k which maximizes g_max, the sum of gv[1],...,gv[k]

            // TODO: should k_idx be set to -1 instead, to avoid unnecessary swaps?
            int k_idx = 0;
            for (Float gv_value : gv) {
                if (max_gv <= gv_value + max_gv) {
                    max_gv += gv_value;
                    k_idx++;
                } else {
                    break;
                }
            }

            if (max_gv > 0.0f) {
              // Exchange av[1],av[2],...,av[k] with bv[1],bv[2],...,bv[k]
              // TODO: is here an check necessary? k
              for (int k = 0; k_idx <= k; k++) {
                  // perform a vertex swap
                  Vertex tmp = av.get(k);
                  av.set(k, bv.get(k));
                  bv.set(k, tmp);
              }
            }
        } while(max_gv > 0.0f);
    }
}

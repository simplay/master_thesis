package com.ma;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Solving the graph partitioning problem using the Kernighan–Lin algorithm is a heuristic algorithm.
 * <p>
 * Created by simplay on 16/02/16.
 */
public class GraphPartitioner {

    private Graph graph;
    private final HashSet<Vertex> setA = new HashSet<>();
    private final HashSet<Vertex> setB = new HashSet<>();
    private final List<Float> gv = new ArrayList<>();
    private final List<Vertex> av = new ArrayList<>();
    private final List<Vertex> bv = new ArrayList<>();
    private final int MAXITER = 100;

    public GraphPartitioner(Graph graph) {

        this.graph = graph;

        // determine a balanced initial partition of the nodes into sets A and B
        int n = graph.vertexCount();
        int leftHalf = n / 2;

        for (int a_l = 0; a_l < leftHalf; a_l++) {
            setA.add(graph.vertices.get(a_l));
        }

        for (int b_l = leftHalf; b_l < n; b_l++) {
            setB.add(graph.vertices.get(b_l));
        }

    }

    public void runKernighanLin() {
        float max_gv = 0.0f;
        int iter = 0;



        do {
            // compute D values for all a in A and b in B
            for (Vertex v : graph.vertices) {
                v.computeD(setA, setB);
            }

            for (int n = 0; n < (graph.vertexCount() / 2) + 1; n++) {
                // find a from A and b from B, such that g = D[a] + D[b] - 2*E(a, b) is maximal

                List<Vertex> sortedByDvalueA = new ArrayList(setA);
                Collections.sort(sortedByDvalueA);

                List<Vertex> sortedByDvalueB = new ArrayList(setB);
                Collections.sort(sortedByDvalueB);

                int lastAIndex = setA.size() - 1;
                int lastBIndex = setB.size() - 1;
                if (lastAIndex < 0 || lastBIndex < 0) break;

                Vertex topA = sortedByDvalueA.get(lastAIndex);
                Vertex topB = sortedByDvalueB.get(lastBIndex);
                int bestIter = 0;
                float g = 0.0f;
                for (int idx = 1; idx < Math.min(lastAIndex, lastBIndex); idx++) {

                    int idxa = lastAIndex - idx;
                    int idxb = lastBIndex - idx;

                    Vertex topANext = sortedByDvalueA.get(idxa);
                    Vertex topBNext = sortedByDvalueB.get(idxb);

                    float e_ab = graph.getWeight(topA.getId(), topB.getId());
                    float e_ab_next = graph.getWeight(topA.getId(), topB.getId());
                    g = topA.getDValue() + topB.getDValue() - 2.0f * e_ab;
                    float g_next = topANext.getDValue() + topBNext.getDValue() - 2.0f * e_ab_next;

                    if (g_next > g) {
                        topA = topANext;
                        topB = topBNext;
                        bestIter++;
                    } else {
                        break;
                    }
                }

                if (bestIter == 0) {
                    float e_ab = graph.getWeight(topA.getId(), topB.getId());
                    g = topA.getDValue() + topB.getDValue() - 2.0f * e_ab;
                }

                // remove a and b from further consideration in this pass
                setA.remove(topA);
                setB.remove(topB);

                // add g to gv, a to av, and b to bv
                gv.add(n, g);
                av.add(n, topA);
                bv.add(n, topB);

                // update D values for the elements of A = A \ a and B = B \ b
                topA.updateDValuesOfNeighbors(setA, setB);
                topB.updateDValuesOfNeighbors(setB, setA);
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
            iter++;
            System.out.println("Iteration " + iter + " k=" + k_idx + " gv=" + max_gv);
        } while ((max_gv > 0.0f) && (iter < MAXITER));

        for (Vertex v : av) {
            v.setPartition(0);
        }

        for (Vertex v : bv) {
            v.setPartition(1);
        }
    }
}

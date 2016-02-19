package com.ma;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Solving the graph partitioning problem using the Kernighan–Lin algorithm is a heuristic algorithm.
 * <p>
 * Created by simplay on 16/02/16.
 */
public class GraphPartitioner {

    private Graph graph;
    private final PartitionSet setA = new PartitionSet();
    private final PartitionSet setB = new PartitionSet();
    private final List<Float> gv = new ArrayList<>();
    private final List<Vertex> av = new ArrayList<>();
    private final List<Vertex> bv = new ArrayList<>();
    private ArrayList<Vertex> av_copy;
    private ArrayList<Vertex> bv_copy;
    private final int MAXITER = 10;

    public GraphPartitioner(Graph graph) {

        this.graph = graph;

        // determine a balanced initial partition of the nodes into sets A and B

        int dummyCount = 1150;
        initSetsMod2(dummyCount);
        // initSetsEmptyFull(dummyCount);
        // initSetsSplitLeftRight(dummyCount);

    }

    private void initSetsMod2(int count) {
        int idx = 0;
        for(Vertex v : graph.vertices) {
            if (idx % 2 == 0) {
                setA.add(v);
            } else {
                setB.add(v);
            }
            idx++;
        }
        addDummies(count);
    }

    private void addDummies(int count) {
        for (int k=0; k < count; k++) {
            setA.add(new Vertex(-1, graph.vertexCount(), true));
            setB.add(new Vertex(-1, graph.vertexCount(), true));
        }
    }

    private void initSetsSplitLeftRight(int count) {
        int idx = 0;
        int n = graph.vertexCount();
        int leftHalf = n / 2;
        for(Vertex v : graph.vertices) {
            if (idx <= leftHalf) {
                setA.add(v);
            } else {
                setB.add(v);
            }
            idx++;
        }
        addDummies(count);
    }

    private void initSetsEmptyFull(int count) {
        for(Vertex v : graph.vertices) {
            setA.add(new Vertex(-1, graph.vertexCount(), true));
            setB.add(v);

        }
        addDummies(count);
    }
    
    void dumpDValueHistogram(Graph g){
        if (!Main.DUMP_DATA) return;

        try {
            File f = new File("./temp_debug.m");
            System.out.println("Dumpig debug .m file in: " + f.getAbsolutePath());

            BufferedWriter writer = new BufferedWriter(new FileWriter("./temp_debug.m"));
            writer.write("Vals = [");

            for(Vertex v: graph.vertices){
                writer.write("" + v.getDValue() + "\n");
            }

            writer.write("]; cdfplot(Vals);");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void runKernighanLin() {
        float max_gv = 0.0f;
        int iter = 0;

        do {
            //av_copy = new ArrayList<>(av);
            //bv_copy = new ArrayList<>(bv);

            av.clear();
            bv.clear();
            gv.clear();

            // compute D values for all a in A and b in B
            for (Vertex v : graph.vertices) {
                v.computeD();
            }
            
            dumpDValueHistogram(graph);//ok, i guess.

            // iterate only over n := |V|/2:
            // if n even, then iterate till n/2 => center of the range 1..4 is the value 2
            // if n odd, then iterate till floor(n/2) + 1 => center of the range 1..3 is the value 2
            for (int n = 0; n < ((int)Math.ceil(graph.vertexCount() / 2)) + 1; n++) {
                // find a from A and b from B, such that g = D[a] + D[b] - 2*E(a, b) is maximal

                List<Vertex> sortedByDvalueA = setA.sortedByVertexDvalues();
                List<Vertex> sortedByDvalueB = setB.sortedByVertexDvalues();

                int lastAIndex = setA.size() - 1;
                int lastBIndex = setB.size() - 1;
                if (lastAIndex < 0 || lastBIndex < 0) break;

                Vertex topA = sortedByDvalueA.get(lastAIndex);
                Vertex topB = sortedByDvalueB.get(lastBIndex);

                //could and should be sped up...
                //for now just n^2 double loop.
                float maxgain = graph.gain(topA, topB);
                float candidate_gain;
                for (int idxa = lastAIndex; idxa >= 0; idxa--) {
                    Vertex candidateA = sortedByDvalueA.get(idxa);

                    // if (candidateA.getPartitionSetLabel() == -1) continue;

                    for (Vertex candidateB : candidateA.neighbors) {

                        // only consider valid neighbors
                        if (candidateB.getPartitionSetLabel() == -1) continue;

                        candidate_gain = graph.gain(candidateA, candidateB);

                        //can be sped up with a break here if the candidates are sorted by potential gain
                        if (candidate_gain > maxgain) {
                            maxgain = candidate_gain;
                            topA = candidateA;
                            topB = candidateB;
                        }

                    }
                }
                // update D values for the elements of A = A \ a and B = B \ b
                //topA.updateDValuesOfNeighbors(topB);
                //topB.updateDValuesOfNeighbors(topA);



                HashSet<Vertex> subsetA = new HashSet<>();
                HashSet<Vertex> subsetB = new HashSet<>();

                for (Vertex v : topA.neighbors) {
                    if (v == topB ) continue;
                    if (v.getPartitionSetLabel() == topA.getPartitionSetLabel()) {
                        subsetA.add(v);
                    } else if (v.getPartitionSetLabel() == topB.getPartitionSetLabel()) {
                        subsetB.add(v);
                    }
                }

                for (Vertex v : topB.neighbors) {
                    if (v == topA) continue;
                    if (v.getPartitionSetLabel() == topA.getPartitionSetLabel()) {
                        subsetA.add(v);
                    } else if (v.getPartitionSetLabel() == topB.getPartitionSetLabel()) {
                        subsetB.add(v);
                    }
                }

                for (Vertex v : subsetA ) {
                    if (v.isDummy() ) continue;
                    float tmp = v.getDValue() + 2.0f*topA.getSimValue(v.getId())-2.0f*v.getSimValue(topB.getId());
                    v.setdValue(tmp);
                }

                for (Vertex v : subsetB ) {
                    if (v.isDummy() ) continue;
                    float tmp = v.getDValue() + 2.0f*topB.getSimValue(v.getId())-2.0f*v.getSimValue(topA.getId());
                    v.setdValue(tmp);
                }


                // markInvalid a and b from further consideration in this pass
                setA.markInvalid(topA);
                setB.markInvalid(topB);


                // add g to gv, a to av, and b to bv
                gv.add(n, maxgain);
                av.add(n, topA);
                bv.add(n, topB);


            }

            // find k which maximizes g_max, the sum of gv[1],...,gv[k]
            max_gv = 0.0f;

            // find max partial sum of gv with its corresponding index
            int max_gv_idx = 0;
            int current_gv_idx = 0;
            float current_max_gv = 0.0f;
            for (Float gv_value : gv) {

                if (current_max_gv < gv_value + current_max_gv) {
                    max_gv_idx = current_gv_idx;
                    max_gv = current_max_gv + gv_value;
                }
                current_max_gv += gv_value;
                current_gv_idx++;
            }

            if (max_gv > 0.0f) {
                // Exchange av[1],av[2],...,av[k] with bv[1],bv[2],...,bv[k]
                // TODO: is here an check necessary? k
                for (int k = 0; k <= max_gv_idx; k++) {
                    // perform a vertex swap

                    setA.remove(av.get(k));
                    setB.add(av.get(k));

                    setB.remove(bv.get(k));
                    setA.add(bv.get(k));

                  //  Vertex tmp = av.get(k);
                  //  av.set(k, bv.get(k));
                  //  bv.set(k, tmp);
                }

//                setA.replaceFirstKElementsByCollection(av, max_gv_idx);
  //              setB.replaceFirstKElementsByCollection(bv, max_gv_idx);
            }

            setA.relabelValid();
            setB.relabelValid();

            iter++;
            System.out.println("Iteration " + iter + " k=" + max_gv_idx + " gv=" + max_gv);
        } while ((max_gv > 0.0f) && (iter < MAXITER));

        for (Vertex v : av) {
            v.setPartition(0);
        }

        for (Vertex v : bv) {
            v.setPartition(1);
        }


    }

}

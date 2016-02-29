package com.ma;


import sun.awt.image.ImageWatched;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Solving the graph partitioning problem using the Kernighan–Lin algorithm is a heuristic algorithm.
 * <p>
 * Created by simplay on 16/02/16.
 */
public class GraphPartitioner {

    private Graph graph;
    private final List<Float> gv = new ArrayList<>();
    private final List<Vertex> av = new ArrayList<>();
    private final List<Vertex> bv = new ArrayList<>();
    private final ArrayList<PartitionSet> setList = new ArrayList<>();
    private int clusterCount = 2;


    public PartitionSet getSetA() {
        return getSet(0);
    }

    public PartitionSet getSetB() {
        return getSet(1);
    }

    public PartitionSet getSet(int idx) {
        return setList.get(idx);
    }

    public GraphPartitioner(Graph graph) {

        this.graph = graph;

        for (int k = 0; k < clusterCount; k++) {
            setList.add(new PartitionSet());
        }

        // determine a balanced initial partition of the nodes into sets A and B

        int dummyCount = 0;
        //assignModN(dummyCount);
        // initSetsMod2(dummyCount);
        initSetsEmptyFull(dummyCount);
        // initSetsSplitLeftRight(dummyCount);

    }

    public void assignModN(int dummy_count) {
        int idx = 0;
        for(Vertex v : graph.vertices) {
            setList.get(idx % clusterCount).add(v);
            idx++;
        }
    }

    private void initSetsMod2(int count) {
        int idx = 0;
        for(Vertex v : graph.vertices) {
            if (idx % 2 == 0) {
                setList.get(0).add(v);
            } else {
                setList.get(1).add(v);
            }
            idx++;
        }
        addDummies(count);
    }

    private void addDummies(int count) {
        for (int k = 0; k < count; k++) {
            for (PartitionSet ps : setList) {
                ps.add(new Vertex(-1, graph.vertexCount(), true));
            }
        }
    }

    private void initSetsEmptyFull(int count) {
        for(Vertex v : graph.vertices) {
            setList.get(0).add(new Vertex(-1, graph.vertexCount(), true));
            setList.get(1).add(v);

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

    public void runKernighanLin(int MAXITER) {
        _runKernighanLin(setList.get(0), setList.get(1), MAXITER);
    }

    private void _runKernighanLin(PartitionSet setA, PartitionSet setB, int MAXITER) {
        float max_gv = 0.0f;
        int iter = 0;

        do {

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
            for (int n = 0; n < Math.min(setA.size(), setB.size()); n++) {
                // find a from A and b from B, such that g = D[a] + D[b] - 2*E(a, b) is maximal

                List<Vertex> sortedByDvalueA = setA.sortedByVertexDvalues();
                List<Vertex> sortedByDvalueB = setB.sortedByVertexDvalues();

                if (sortedByDvalueA.size() == 0 || sortedByDvalueB.size() == 0) break;

                Vertex topA = sortedByDvalueA.get(0);
                Vertex topB = sortedByDvalueB.get(0);

                //could and should be sped up...
                //for now just n^2 double loop.
                float maxgain = graph.gain(topA, topB);
                float candidate_gain;
                for (Vertex candidateA : sortedByDvalueA) {
                    if (candidateA.getPartitionSetLabel() == -1) continue;
                    for (Vertex candidateB : sortedByDvalueB) {
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

                if (!setA.contains(topA)) System.out.println("A topA fail");
                if (!setB.contains(topB)) System.out.println("B topB fail");
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
            float current_sum = 0.0f;
            for (Float gv_value : gv) {
            	current_sum += gv_value;
            	//REMOVED BUG: This constraint makes no sense; it will always
            	//be true if gv_value is greater 0, so the found k was wrong.
            	//if (current_sum < gv_value + current_sum) {
            	if (max_gv < current_sum) {
                    max_gv_idx = current_gv_idx + 1;
                    max_gv = current_sum;
                }
            	current_gv_idx++;
                
            }

            if (max_gv > 0.0f) {
                // Exchange av[1],av[2],...,av[k] with bv[1],bv[2],...,bv[k                
            	// TODO: is here an check necessary? k
            	//removedved BUG: loop boundary needs to be < max_g_idx: max_g_idx = 0 must mean NO swaps 
                //for (int k = 0; k <= max_gv_idx; k++) {
            	for (int k = 0; k < max_gv_idx; k++) {
                    setA.remove(av.get(k)) ;
                    setB.add(av.get(k));
                    setB.remove(bv.get(k));
                    setA.add(bv.get(k));
                }
            }

            setA.relabelValid();
            setB.relabelValid();

            iter++;
            System.out.println("Iteration " + iter + " k=" + max_gv_idx + " gv=" + max_gv);
        } while ((max_gv > 0.0f) && (iter < MAXITER));

        for (Vertex v : setA) {
            v.setPartition(setA.getLabel());
        }

        for (Vertex v : setB) {
            v.setPartition(setB.getLabel());
        }


    }

}

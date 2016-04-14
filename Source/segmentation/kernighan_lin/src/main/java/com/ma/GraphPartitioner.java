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
 */
public class GraphPartitioner {

    private Graph graph;
    private final List<Float> gv = new ArrayList<>();
    private final List<Vertex> av = new ArrayList<>();
    private final List<Vertex> bv = new ArrayList<>();
    private final ArrayList<PartitionSet> setList = new ArrayList<>();
    private int clusterCount;
    private int REPS;
    private int vertexCount;


    public PartitionSet getSetA() {
        return getSet(0);
    }

    public PartitionSet getSetB() {
        return getSet(1);
    }

    public PartitionSet getSet(int idx) {
        return setList.get(idx);
    }

    public GraphPartitioner(Graph graph, int clusterCount) {
        this(graph, clusterCount, 500, 1);
    }

    public GraphPartitioner(Graph graph, int clusterCount, int dummyCount, int REPS) {

        this.graph = graph;

        // The total number of vertices is the number of read
        // trajectories plus the virtual vertices (i.e. dummies).
        vertexCount = dummyCount + graph.vertices.size();

        this.clusterCount = clusterCount;

        for (int k = 0; k < clusterCount; k++) {
            setList.add(new PartitionSet());
        }

        // determine a balanced initial partition of the nodes into sets A and B
        initBalancedSets(dummyCount);
        this.REPS = REPS;
        Logger.println("Computing multi-label clustering via alpha-beta expansion");
        Logger.println("=> Solving for " + clusterCount + " clusters by using " + dummyCount + " dummy vertices");
        Logger.println();
    }

    private void initBalancedSets(int dummyCount) {

        switch (ArgParser.getInitialPartitionMode()) {
            case MOD_N:
                assignModN(dummyCount);
                break;
            case MOD_2:
                initSetsMod2(dummyCount);
                break;
            case EMPTY_FULL:
                initSetsEmptyFull(dummyCount);
                break;
            case EMPTY_BUT_ONE:
                initAllEmptyButOne(dummyCount);
                break;
        }
    }

    public void initAllEmptyButOne(int dummyCount) {
        for (Vertex v : graph.vertices) {
            setList.get(0).add(v);
            for (int k = 1; k < setList.size(); k++) {
                setList.get(k).add(new Vertex(-1, graph.vertexCount(), true));
            }
        }
        addDummies(dummyCount);
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
        int fullClusters = clusterCount-1;
        int idx = 0;
        for (Vertex v : graph.vertices) {
            setList.get((idx % fullClusters)+1).add(v);
            idx++;
        }
        int maxVertCount = -1;

        for (PartitionSet ps : setList) {
            if (ps.count() > maxVertCount) {
                maxVertCount = ps.count();
            }
        }

        for (PartitionSet ps : setList) {
            int vertDiffCount = maxVertCount - ps.count();
            for (int k = 0; k < vertDiffCount; k++) {
                ps.add(new Vertex(-1, graph.vertexCount(), true));
            }
        }
        addDummies(count);
    }
    
    void dumpDValueHistogram(Graph g){
        if (!Main.DUMP_DATA) return;

        try {
            File f = new File("./temp_debug.m");
            Logger.println("Dumpig debug .m file in: " + f.getAbsolutePath());

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
        // iterate over all pairs: #cluster low 2
        for (int repet=0; repet < REPS; repet++) {
            Logger.println("Round " + (repet+1) + "/"+REPS);
            for (int m = 0; m < clusterCount; m++) {
                for (int n = m + 1; n < clusterCount; n++) {
                    Logger.println("Computing pair (m,n)="+"("+m+","+n+")...");
                    _runKernighanLin(setList.get(m), setList.get(n), MAXITER);
                }
            }
            Logger.println();
        }
        MaxGainFinder.close();
    }

    private void _runKernighanLin(PartitionSet setA, PartitionSet setB, int MAXITER) {
        float max_gv = 0.0f;
        int iter = 0;
        int[] activeLabels = { setA.getLabel(), setB.getLabel() };
        float diff_gv = 0.0f;
        float prev_max_gv = 0.0f;

        // Iterate until either max_gv become <= 0
        // or we exceeded the max. number of allowed iterations
        //MaxGainFinder.start();
        do {

            av.clear();
            bv.clear();
            gv.clear();

            // compute D values for all a in A and b in B
            for (Vertex v : graph.activeVerticesForLabels(activeLabels)) {
                v.computeD(activeLabels);
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
                // sortedByVertexDvalues returns a (ascending) sorted set of valid vertices
                Vertex topA = sortedByDvalueA.get(0);
                Vertex topB = sortedByDvalueB.get(0);

                //TODO: could and should be sped up...
                //for now just n^2 double loop.
                // Visit all pairs (a,b) and find the (a,b) = max_arg gain(a,b).
                MaxGainContainer container = MaxGainFinder.getMaxGain(graph, sortedByDvalueA, sortedByDvalueB, topA, topB);
                topA = container.getTopA();
                topB = container.getTopB();
                float maxgain = container.getMaxGain();

                // update D values for the elements of A = A \ a and B = B \ b
                HashSet<Vertex> subsetA = new HashSet<>();
                HashSet<Vertex> subsetB = new HashSet<>();

                for (Vertex v : topA.acitveNeighborsForLabels(activeLabels)) {
                    if (v == topB ) continue;
                    if (v.getPartitionSetLabel() == topA.getPartitionSetLabel()) {
                        subsetA.add(v);
                    } else if (v.getPartitionSetLabel() == topB.getPartitionSetLabel()) {
                        subsetB.add(v);
                    }
                }

                for (Vertex v : topB.acitveNeighborsForLabels(activeLabels)) {
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
            diff_gv = prev_max_gv - max_gv;
            Logger.println("+ Iteration " + iter + " k=" + max_gv_idx + " gv=" + max_gv);
            prev_max_gv = max_gv;

            // if there happened the complete permutation or no permutation step,
            // then we are done.
            if (max_gv_idx == vertexCount || max_gv_idx == 0) break;

            // If there was no significant enery change comppared to the previous iteration,
            // then skip
            if (diff_gv == 0.0f) break;

        } while ((max_gv > 0.0f) && (iter < MAXITER));

        // Assign labels to vertices contained in partition set.
        for (Vertex v : setA) {
            v.setPartition(setA.getLabel());
        }

        for (Vertex v : setB) {
            v.setPartition(setB.getLabel());
        }

        //MaxGainFinder.close();
    }

}

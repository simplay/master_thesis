package com.ma;

import java.util.ArrayList;

public class InitialSetPartition {

    private Graph graph;
    private ArrayList<PartitionSet> setList;
    private int clusterCount;

    public InitialSetPartition(Graph graph, ArrayList<PartitionSet> setList, int dummyCount, int clusterCount) {
        this.graph = graph;
        this.setList = setList;
        this.clusterCount = clusterCount;
        initBalancedSets(dummyCount);
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
        for (Vertex v : graph.getVertices()) {
            setList.get(0).add(v);
            for (int k = 1; k < setList.size(); k++) {
                setList.get(k).add(new Vertex(-1, graph.vertexCount(), true));
            }
        }
        addDummies(dummyCount);
    }

    public void assignModN(int dummy_count) {
        int idx = 0;
        for(Vertex v : graph.getVertices()) {
            setList.get(idx % clusterCount).add(v);
            idx++;
        }
    }

    private void initSetsMod2(int count) {
        int idx = 0;
        for(Vertex v : graph.getVertices()) {
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
        for (Vertex v : graph.getVertices()) {
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
}

package com.ma;

import java.util.List;

public class MaxGainTask implements Runnable {

    private Graph graph;
    private MaxGainContainer container;
    private List<Vertex> sortedByDvalueA;
    private List<Vertex> sortedByDvalueB;
    private Vertex topA;
    private Vertex topB;
    private float maxgain;

    public MaxGainTask(Graph graph, MaxGainContainer container, List<Vertex> sortedByDvalueA,
                       List<Vertex> sortedByDvalueB, Vertex topA, Vertex topB, float maxgain) {
        this.graph = graph;
        this.container = container;
        this.sortedByDvalueA = sortedByDvalueA;
        this.sortedByDvalueB = sortedByDvalueB;
        this.topA = topA;
        this.topB = topB;
        this.maxgain = maxgain;
    }

    @Override
    public void run() {
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
        container.updateMaxGainValues(maxgain, topA, topB);
    }
}

package com.ma;

public class MaxGainContainer {

    private Vertex topA;
    private Vertex topB;
    private double maxGain;

    public MaxGainContainer(double maxGain, Vertex topA, Vertex topB) {
        this.maxGain = maxGain;
        this.topA = topA;
        this.topB = topB;
    }

    public synchronized void updateMaxGainValues(double candidateMaxGain, Vertex cTopA, Vertex cTopB) {
        if (candidateMaxGain > maxGain) {
            maxGain = candidateMaxGain;
            topA = cTopA;
            topB = cTopB;
        }
    }

    public Vertex getTopA() {
        return topA;
    }

    public Vertex getTopB() {
        return topB;
    }

    public double getMaxGain() {
        return maxGain;
    }
}

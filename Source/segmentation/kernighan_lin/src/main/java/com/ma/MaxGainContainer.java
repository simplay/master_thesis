package com.ma;

public class MaxGainContainer {

    private Vertex topA;
    private Vertex topB;
    private float maxGain;

    public MaxGainContainer(float maxGain, Vertex topA, Vertex topB) {
        this.maxGain = maxGain;
        this.topA = topA;
        this.topB = topB;
    }

    public synchronized void updateMaxGainValues(float candidateMaxGain, Vertex cTopA, Vertex cTopB) {
        if (maxGain < candidateMaxGain) {
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

    public float getMaxGain() {
        return maxGain;
    }
}

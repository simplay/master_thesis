package com.ma;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by simplay on 15/02/16.
 */
public class Vertex implements Comparable<Vertex> {

    private int id;
    private int partitionLabel;
    private int trajectoryId;

    public final List<Vertex> neighbors = new LinkedList<Vertex>();
    public final float[] similarities;

    // D_a = E_a - I_a
    private float dValue;

    public Vertex(int id, int vertexCount) {
        this.id = id;
        similarities = new float[vertexCount];
        this.partitionLabel = -1;
    }

    public void updateDValuesOfNeighbors(HashSet<Vertex> internalSet, HashSet<Vertex> externalSet) {

        for (Vertex v_i : internalSet) {
            v_i.computeD(internalSet, externalSet);
        }

        for (Vertex v_e : externalSet) {
            v_e.computeD(externalSet, internalSet);
        }
    }

    /**
     * Computes the difference between the external and internal node cost.
     * Lets denote this vertex as a, A is the set of all internal nodes and B
     * the set of all external nodes. Internal means, nodes that are adjacent to
     * this vertex and belong to the same set as this vertex does. External refers to nodes
     * that are also adjacent to this node but belong to another set, called B. Then
     * the internal cost of a is the sum of the costs of edges between a and other nodes in A
     * and the external cost of a is the sum of the costs of edges between a and nodes in B
     *
     * @param setA the set this vertex belongs to, the internal set.
     * @param setB the external vertex set. This vertex does not belong to that set.
     */
    public void computeD(HashSet<Vertex> setA, HashSet<Vertex> setB) {

        // select all adjacent internal vertices.
        List<Vertex> internalNeighbors = new LinkedList<>();
        for (Vertex v_a : setA) {
            if (neighbors.contains(v_a)) {
                internalNeighbors.add(v_a);
            }
        }

        // select all adjacent external vertices.
        List<Vertex> externalNeighbors = new LinkedList<>();
        for (Vertex v_a : setB) {
            if (neighbors.contains(v_a)) {
                externalNeighbors.add(v_a);
            }
        }

        float I_a = 0.0f;
        for (Vertex i_v : internalNeighbors) {
            I_a += similarities[i_v.id];
        }

        float E_a = 0.0f;
        for (Vertex e_v : externalNeighbors) {
            E_a += similarities[e_v.id];
        }

        this.dValue = E_a - I_a;
    }

    public void setPartition(int partitionLabel) {
        this.partitionLabel = partitionLabel;
    }

    public int getPartitionLabel() {
        return partitionLabel;
    }

    public int getId() {
        return id;
    }

    public float getDValue() {
        return dValue;
    }

    /**
     * Appends a vertex to this vertex' nearest neighborhood unless it is null.
     *
     * @param v neighboring vertex.
     */
    public void appendNearestNeighbord(Vertex v) {
        if (v != null) {
            neighbors.add(v);
        }
    }

    public int getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(int trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    public void setSimilarities(String[] readSimilarities) {
        int idx = 0;
        for (String sim : readSimilarities) {
            similarities[idx] = Float.parseFloat(sim);
            idx++;
        }
    }

    //returns -1 if "this" object is less than "that" object
    //returns 0 if they are equal
    //returns 1 if "this" object is greater than "that" object
    public int compareTo(Vertex o) {
        if (dValue < o.dValue) {
            return -1;
        } else if (dValue == o.dValue) {
            return 0;
        } else {
            return 1;
        }
    }

}

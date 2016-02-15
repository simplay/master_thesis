package com.ma;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by simplay on 15/02/16.
 */
public class Vertex {

    private int id;


    private int trajectoryId;

    public final List<Vertex> neighbors = new LinkedList<Vertex>();
    public final float[] similarities;

    public Vertex(int id, int vertexCount) {
        this.id = id;
        similarities = new float[vertexCount];
    }

    public int getId() {
        return id;
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

}

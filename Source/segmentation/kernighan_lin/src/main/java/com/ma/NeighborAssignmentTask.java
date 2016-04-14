package com.ma;

public class NeighborAssignmentTask implements Runnable {

    private Graph g;
    private String[] neighbors;
    private int vertexId;

    public NeighborAssignmentTask(Graph g, String[] neighbors, int vertexId) {
        this.g = g;
        this.neighbors = neighbors;
        this.vertexId = vertexId;
    }

    @Override
    public void run() {
        g.assignNearestNeighborsForVertex(vertexId, neighbors);
    }

}

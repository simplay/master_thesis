package com.ma;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simplay on 15/02/16.
 */
public class Graph {

    public final List<Vertex> vertices = new ArrayList<Vertex>();

    public Graph() {}

    /**
     * Yields the number of contained graph vertices.
     *
     * @return the vertex count.
     */
    public int vertexCount() {
        return vertices.size();
    }

    public void appendVertex(Vertex v) {
        vertices.add(v);
    }

    public void updateSimilaritiesForVertex(int vertex_id, String[] similarities) {
        Vertex v = vertices.get(vertex_id);
        v.setSimilarities(similarities);
    }

    public void assignNearestNeighborsForVertex(int trajectoryId, String[] neighborIndices) {
        Vertex v = findVertexByTrajectoryId(trajectoryId);
        if (v != null) {
            for (String nnIndexIdentifiers : neighborIndices) {
                int nnIdx = Integer.parseInt(nnIndexIdentifiers);
                Vertex neighborVertex = findVertexByTrajectoryId(nnIdx);
                v.appendNearestNeighbord(neighborVertex);
            }
        }
    }

    public void inspectSimilaritiesForVertex(int id) {
        for(Float t : vertices.get(id).similarities) {
            System.out.print(t + " ");
        }
    }

    /**
     * Find a vertex by its trajectory id
     *
     * @param trajectoryId trajectory id globally known.
     * @return the vertex that is associated to the given trajectory id or null.
     */
    public Vertex findVertexByTrajectoryId(int trajectoryId) {
        for (Vertex v : vertices) {
            if (v.getTrajectoryId() == trajectoryId) {
                return v;
            }
        }
        return null;
    }
}

package com.ma;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by simplay on 15/02/16.
 */
public class Graph {

    public final List<Vertex> vertices = new ArrayList<Vertex>();

    public Graph() {
    }

    public float getWeight(int idxa, int idxb) {
        return vertices.get(idxa).similarities[idxb];
    }

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

    /**
     *
     * @param idx vertex idx
     * @param neighborIndices
     */
    public void assignNearestNeighborsForVertex(int idx, String[] neighborIndices) {
        Vertex v = vertices.get(idx);
        if (v != null) {
            for (String nnIndexIdentifiers : neighborIndices) {
                int nnIdx = Integer.parseInt(nnIndexIdentifiers);
                Vertex neighborVertex = findVertexByTrajectoryId(nnIdx);
                v.appendNearestNeighbord(neighborVertex);
                neighborVertex.appendNearestNeighbord(v);
            }
        }
    }

    public void inspectSimilaritiesForVertex(int id) {
        for (Float t : vertices.get(id).similarities) {
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

    /**
     * @param fPathName file path name for this graph's partition file.
     */
    public void savePartitionToFile(String fPathName) {
        try {
            try (PrintWriter out = new PrintWriter(fPathName)) {
                for (Vertex v : vertices) {
                    String line = v.getTrajectoryId() + "," + v.getPartitionLabel();
                    out.println(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

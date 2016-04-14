package com.ma;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Graph {

    public final List<Vertex> vertices = new ArrayList<Vertex>();

    public Graph() {
    }

    public List<Vertex> activeVerticesForLabels(int[] activeLabelList) {
        ArrayList<Vertex> activeVertices = new ArrayList<>();
        for (Vertex v : vertices) {
            for (int activeLabel : activeLabelList) {
                if (v.getPartitionSetLabel() == activeLabel) {
                    activeVertices.add(v);
                }
            }
        }
        return activeVertices;
    }

    //put all loading into constructor, to make sure
    //the similarity matrix is always set to 0 for all non connected edges.
    public Graph(String dataset) {
        new VertexReader(dataset + "_sim.dat", this);
        new TrajectoryLabelReader(dataset + "_labels.txt", this);
        new NeighborhoodReader(dataset + "_spnn.txt", this);
        
        //fill in only the nonzero elements in  a new matrix.
        double[][] tmp_similarity_matrix = new double[vertexCount()][vertexCount()];
        for(Vertex v : vertices){
        	for(Vertex w : v.neighbors){
        		tmp_similarity_matrix[v.getId()][w.getId()] = v.similarities[w.getId()];
        		tmp_similarity_matrix[w.getId()][v.getId()] = w.similarities[v.getId()];
        	}
        }
        //and copy the new similarities back.
        for(Vertex v : vertices){
        	v.setSimilarities(tmp_similarity_matrix[v.getId()]);
        }
	}


	public double gain(Vertex a, Vertex b){
    	return a.getDValue() + b.getDValue() - 2.0d * getWeight(a.getId(), b.getId());
    }
    public double getWeight(int idxa, int idxb) {
        if (idxa < 0 || idxb < 0) return 0.0d;
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
        for (Double t : vertices.get(id).similarities) {
            Logger.print(t + " ");
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
        Logger.println("Writing graph partitioning file to: `" + fPathName + "`");
        try {
            try (PrintWriter out = new PrintWriter(fPathName)) {
                for (Vertex v : vertices) {
                    int labelValue = v.getPartitionLabel();
                    String line = v.getTrajectoryId() + "," + labelValue;
                    out.println(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

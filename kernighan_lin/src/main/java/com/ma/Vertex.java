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
    private int partitionSetLabel;
    private boolean is_dummy;

    public final List<Vertex> neighbors = new LinkedList<Vertex>();
    public float[] similarities;


    // D_a = E_a - I_a
    private float dValue;

    public Vertex(int id, int vertexCount) {
        this.id = id;
        this.similarities = new float[vertexCount];
        this.partitionLabel = -1;
        this.partitionSetLabel = -1;
        this.is_dummy = false;
    }

    public Vertex(int id, int vertexCount, boolean is_dummy) {
        this.id = id;
        this.similarities = new float[vertexCount];
        this.partitionLabel = -1;
        this.partitionSetLabel = -1;
        this.is_dummy = is_dummy;
    }

    public void setPartitionSetLabel(int label) {
        this.partitionSetLabel = label;
    }

    public int getPartitionSetLabel() {
        return partitionSetLabel;
    }

    public LinkedList<Vertex> getNeighborsWithSetLabel(int setLabel) {
        LinkedList<Vertex> labelNeighbors = new LinkedList<>();
        if (is_dummy) return labelNeighbors;
        for (Vertex v : neighbors) {
            if (v.partitionSetLabel == setLabel) {
                labelNeighbors.add(v);
            }
        }
        return labelNeighbors;
    }

    public void updateDValuesOfNeighbors(Vertex other) {
        if (is_dummy) return;



        for (Vertex v : neighbors) {
            if (v.getPartitionSetLabel() == -1) continue;
            float dvalnew = 0.0f;
            if (v.getPartitionSetLabel() == getPartitionSetLabel()) {
                dvalnew = v.getDValue() + 2.0f*similarities[v.getId()]-2.0f*v.similarities[other.getId()];
            } else {
                dvalnew = v.getDValue() - 2.0f*similarities[v.getId()]+2.0f*v.similarities[other.getId()];
            }
            v.setdValue(dvalnew);
        }

    }

    public float getSimValue(int idx) {
        return (isDummy() || idx == -1) ? 0.0f : similarities[idx];
    }

    public void setdValue(float newDVal) {
        this.dValue = newDVal;
    }


    public boolean isDummy() {
        return is_dummy;
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
     */
    public void computeD() {
        if (is_dummy) return;
        // select all adjacent internal vertices.
        float I_a = 0.0f;
        float E_a = 0.0f;

        for(Vertex v : neighbors) {
            // skip vertices with no partition set label
            if (v.getPartitionSetLabel() == -1) continue;

            if (v.getPartitionSetLabel() != getPartitionSetLabel()) {
                E_a += v.similarities[getId()];
            } else {
                I_a += v.similarities[getId()];
            }
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
            if (!neighbors.contains(v)) {
                neighbors.add(v);
            }
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

    public String toString() {
        String neighborsNames = "[ ";
        for (Vertex v : neighbors) {
            neighborsNames += v.getId() + " ";
        }
        neighborsNames += " ]";
        return id+ "=>" + neighborsNames + "\n";
    }

	public void setSimilarities(float[] fs) {
		assert(fs.length ==  similarities.length);
		 int idx = 0;
        for (float sim : fs) {
            similarities[idx] = sim;
            idx++;
        }
	}

}

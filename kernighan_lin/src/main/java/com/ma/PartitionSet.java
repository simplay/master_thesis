package com.ma;

import java.util.*;

/**
 * Created by simplay on 18/02/16.
 */
public class PartitionSet implements Iterable<Vertex> {

    public static final int INVALID_LABEL = -1;
    private static int globalLabelCounter = 0;
    private int label;

    private final ArrayList<Vertex> vertices = new ArrayList<>();
    private int removedCounter = 0;

    public PartitionSet() {
        this.label = globalLabelCounter;
        globalLabelCounter++;
    }

    public void add(Vertex v) {
        if (vertices.contains(v)) return;
        vertices.add(v);
        v.setPartitionSetLabel(label);
        // TODO is this correct?
        v.setPartition(label);
    }

    public boolean remove(Vertex v) {
       // boolean was_successful = vertices.remove(v);
        removedCounter++;
        v.setPartitionSetLabel(INVALID_LABEL);
        return true;
    }

    public int size() {
        int validCounter = 0;
        for (Vertex v : vertices) {
            if (v.getPartitionSetLabel() != INVALID_LABEL) validCounter++;
        }
        return validCounter;
    }

    /**
     * Returns an ascending sorted list of vertices by their d values
     *
     * @return sorted vertices.
     */
    public ArrayList<Vertex> sortedByVertexDvalues() {
        ArrayList<Vertex> sorted_by_d_values = new ArrayList(2000);
        for (Vertex v : vertices) {
            if (v.getPartitionSetLabel() != INVALID_LABEL) {
                sorted_by_d_values.add(v);
            }
        }
        Collections.sort(sorted_by_d_values);
        return sorted_by_d_values;
    }

    public void replaceFirstKElementsByCollection(List<Vertex> permutedVertices) {
        for (int t = 0; t < permutedVertices.size(); t++) {
            Vertex v = permutedVertices.get(t);
            v.setPartitionSetLabel(label);
            vertices.set(t, v);
        }
        flushCounters();
    }

    public void flushCounters() {
        removedCounter = 0;
    }

    @Override
    public Iterator<Vertex> iterator() {
        return vertices.iterator();
    }
}

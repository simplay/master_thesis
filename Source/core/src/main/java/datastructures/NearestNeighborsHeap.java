package datastructures;

import pipeline_components.ArgParser;

import java.util.*;

import static datastructures.NearestNeighborMode.*;

/**
 * This datastructure is used to determine (rather) fast the nearest N eighbors
 * according to their avg spatial distance.
 */
public class NearestNeighborsHeap {

    private ArrayList<TrajectoryNode> data;
    private double min = Double.MAX_VALUE;
    private int count = 0;
    private int size;

    /**
     * Build a priority heap-like data-structure containing the spatial neighbors of a trajectory.
     * @param size heap's size
     */
    public NearestNeighborsHeap(int size) {
        this.size = size;
        data = new ArrayList<>(size);
    }

    /**
     * Has the internal data-structure still some space left?
     *
     * @return true in case there is still space available.
     */
    public boolean hasSpace() {
        return (count < size);
    }

    /**
     * Save addition of an item to the internal datastructure. If there is no more space,
     * replace the worst item with the new one and resort the collection.
     * sorting is performed according to the avg distance value (ascending order).
     *
     * @param label label identifier
     * @param avgSpatialDist avg spatial distance
     */
    public void addItem(int label, double avgSpatialDist) {
        if (hasSpace()) {
            append(count, label, avgSpatialDist);
            count++;
            if (count == size) {
                Collections.sort(data);
            }
        } else {
            if (data.get(size-1).getDistance() > avgSpatialDist) {
                append(size-1, label, avgSpatialDist);
                Collections.sort(data);
            }
        }
    }

    /**
     * Extract label identifiers of spatial neighbors according to a chosen NN mode.
     *
     * @param n number of nearest neighbors that should be extracted
     * @return a collection of the n nearest neighbors according
     *  to their avg spatial distance, where the first index corresponds to the
     *  spatially nearest neighbor.
     */
    public List<Integer> toIntList(int n) {
        switch (ArgParser.getNNMode()) {
            case TOP_N:
                return toTopNInList(n);
            case TOP_AND_WORST_N:
                List<Integer> tops = toTopNInList(n / 2);
                List<Integer> worsts = toWorstNIntList(n/2);
                tops.addAll(worsts);
                return tops;
            default:
                return toTopNInList(n);
        }
    }

    /**
     * Extract label identifiers of top N nearest neighbors
     *
     * @param n number of nearest neighbors that should be extracted
     * @return a collection of the ordered top n nearest neighbors according
     *  to their avg spatial distance, where the first index corresponds to the
     *  spatially nearest neighbor.
     */
    public List<Integer> toTopNInList(int n) {
        List<Integer> nn_labels = new LinkedList<>();
        int counter = 0;
        for (TrajectoryNode node : data) {
            if (counter == n) break;
            nn_labels.add(node.getLabel());
            counter++;
        }
        return nn_labels;
    }

    /**
     * Extract the worst N spatially neighbors
     *
     * @param n number of worst n neighbors.
     * @return List of worst n neighbors.
     */
    public List<Integer> toWorstNIntList(int n) {
        List<Integer> worst_nn_labels = new LinkedList<>();
        int counter = 0;
        int N = data.size();

        for (int k = 1; k <= n; k++) {
            if (counter == n) break;
            int label = data.get(N-k).getLabel();
            worst_nn_labels.add(label);
            counter++;
        }
        return worst_nn_labels;
    }

    /**
     * Append a new item to the data list and update the min avg dist value.
     *
     * @param idx target index in data-structure
     * @param label label value
     * @param dist avg spatial distance value
     */
    private void append(int idx, int label, double dist) {
        data.add(idx, new TrajectoryNode(label, dist));
        if (dist < min) {
            min = dist;
        }
    }
}
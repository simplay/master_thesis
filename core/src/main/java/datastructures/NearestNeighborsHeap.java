package datastructures;

import java.util.*;

/**
 * This datastructure is used to determine (rather) fast the nearest N eighbors
 * according to their avg spatial distance.
 */
public class NearestNeighborsHeap {

    private ArrayList<TrajectoryNode> data;
    private double min = Double.MAX_VALUE;
    private int count = 0;
    private int size;

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
     * Extract label identifiers of top N nearest neighbors
     *
     * @param n number of nearest neighbors that should be extracted
     * @return a collection of the ordered top n nearest neighbors according
     *  to their avg spatial distance, where the first index corresponds to the
     *  spatially nearest neighbor.
     */
    public List<Integer> toIntList(int n) {
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
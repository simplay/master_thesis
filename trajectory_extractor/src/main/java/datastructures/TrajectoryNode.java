package datastructures;

/**
 * Sortable structure combining a label identifier and a avg spatial distance value.
 * items are comparable by their spatial distance value.
 */
public class TrajectoryNode implements Comparable<TrajectoryNode>{

    private Double dist;
    private Integer id;

    public TrajectoryNode(Integer id, Double dist) {
        this.id = id;
        this.dist = dist;
    }

    public double getDistance() {
        return dist;
    }

    public int getLabel() {
        return id;
    }

    @Override
    public int compareTo(TrajectoryNode o) {
        if (dist < o.getDistance()) {
            return -1;
        } else if (dist == o.getDistance()) {
            return 0;
        } else {
            return 1;
        }
    }
}

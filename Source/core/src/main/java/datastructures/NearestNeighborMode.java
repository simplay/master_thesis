package datastructures;

/**
 * Models an indicator which represents which nearest neighbors should be returned.
 */
public enum NearestNeighborMode {
    TOP_N("top"),
    TOP_AND_WORST_N("both"),
    ALL("all");

    // Unique identifier of a mode
    private String id;

    /**
     * Create a new nearest neighbor mode
     *
     * @param id unique identifier.
     */
    private NearestNeighborMode(String id) {
        this.id = id;
    }

    /**
     * Get a mode's unique identifier.
     *
     * @return mode id name
     */
    public String getId() {
        return id;
    }

    /**
     * Find a Mode by its id.
     *
     * By default all nearest neighbors are returned.
     *
     * @param id mode identifier
     * @return the mode that maps to the given id.
     */
    public static NearestNeighborMode getModeById(String id) {
        for (NearestNeighborMode t : values()) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return ALL;
    }
}

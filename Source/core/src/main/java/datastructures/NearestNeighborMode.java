package datastructures;

/**
 * Models an indicator which represents which nearest neighbors should be returned.
 */
public enum NearestNeighborMode {
    TOP_N("top", false),
    TOP_AND_WORST_N("both", false),
    ALL("all", true);

    // Unique identifier of a mode
    private String id;

    // Should we ignore the given nn count
    private boolean shouldIgnoreNNCount;

    /**
     * Create a new nearest neighbor mode
     *
     * @param id unique identifier.
     */
    private NearestNeighborMode(String id, boolean shouldIgnoreNNCount) {
        this.id = id;
        this.shouldIgnoreNNCount = shouldIgnoreNNCount;
    }

    /**
     * Should the given nearest neighbor count be ignored.
     *
     * @return true if it should be ignored, false if it is relevant.
     */
    public boolean getShouldIgnoreNNCount() {
        return shouldIgnoreNNCount;
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

package datastructures;

/**
 * The Activity class is a boolean matrix that allows us to check whether we may start a new trajectory.
 *
 * It is Used to determine whether a new trajectory may be started at a tracking candidate position
 * according to the sampling rule.
 *
 * If and only if there has no trajectory been started/continued
 * within a square window of pixel size sampling_rate/2, we are allowed to start a new trajectory
 * within this window at a given tracking candidate.
 *
 * To have 'activity' means that there is a trajectory, either one that is started or is continued
 * within certain region, close to a query position.
 *
 * Checking for activity and only tracking a point in
 * regions that have no activity yet, saves us therefore from oversampling tracking points (i.e. introduce
 * a sampling density - only every k-th pixel is tracked).
 *
 * Each dataset frame is supposed to have its own activity matrix.
 *
 */
public class Activity {

    // The activity state of a frame.
    private boolean[][] states;

    // Track every samplingRate-th pixel in a frame, determines the density.
    private int samplingRate;

    // image dimensions: m rows, n columns
    private int m;
    private int n;

    /**
     * Generate a new activity frame
     *
     * @param m number of rows in frame
     * @param n number of columns in frame
     * @param samplingRate pixel sampling density.
     */
    public Activity(int m, int n, int samplingRate) {
        states = new boolean[m][n];
        this.m = m;
        this.n = n;
        this.samplingRate = samplingRate;
    }

    /**
     * Marks a certain frame location as active
     *
     * @param row_idx row index in frame that is active
     * @param col_idx column index in frame that is active
     */
    public void markActiveAt(int row_idx, int col_idx) {
        states[row_idx][col_idx] = true;
    }

    /**
     * Iterate over neighborhood of samplingRate/2 and check whether or not
     * there exists an active neighbor.
     *
     * @param p position for checking for the activity. The first component is the row index in the frame,
     *          the 2nd the column index.
     * @return true, if there is some activity present within the given neighborhood,
     *  false otherwise.
     */
    public boolean hasActivityAt(Point2d p) {

        // Obtain the rounded lookup position, since we have a discrete grid.
        int row_idx = p.rounded().iU();
        int col_idx = p.rounded().iV();

        // upper is the starting row index of the neighbor window
        int upper = row_idx - samplingRate/2;
        if (upper < 0) upper = 0;

        // lower is the ending row index of the neighbor window
        int lower = row_idx + samplingRate/2;
        if (lower >= m) lower = m-1;

        // left is the starting column index of the neighbor window
        int left = col_idx - samplingRate/2;
        if (left < 0) left = 0;

        // right is the ending column index of the neighbor window
        int right = col_idx + samplingRate/2;
        if (right >= n) right = n-1;

        // Scan the (sampling_rate/2 x sampling_rate/2) window centered a p
        // within the activity matrix and check for its states.
        for (int k1 = upper; k1 <= lower; k1++) {
            for (int k2 = left; k2 <= right; k2++) {
                if (states[k1][k2]) {
                    return true;
                }
            }
        }

        // No activiy has been found within the window, thus, report no activity found.
        return false;
    }

    /**
     * Reset all internal states to false
     * i.e. they are not active
     */
    public void flushStates() {
        states = new boolean[m][n];
    }

    /**
     * Retrieves the internal activity state of this activity matrix.
     *
     * @return activity matrix states.
     */
    public boolean[][] getStates() {
        return states;
    }

    /**
     * Copies the sate of another activity matrix into this activity matrix.
     *
     * @param other states we use to overwrite the states of this activity.
     */
    public void copyStates(Activity other) {
        boolean[][] states_other = other.getStates();
        for (int k1 = 0; k1 < m; k1++) {
            for (int k2 = 0; k2 < n; k2++) {
                states[k1][k2] = states_other[k1][k2];
            }
        }
    }

}

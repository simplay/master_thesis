package datastructures;

/**
 * Flag all tracked point from the previous frame
 */
public class Activity {

    private boolean[][] states;
    private int samplingRate;
    private int m;
    private int n;

    /**
     *
     * @param m
     * @param n
     * @param samplingRate
     */
    public Activity(int m, int n, int samplingRate) {
        states = new boolean[m][n];
        this.m = m;
        this.n = n;
        this.samplingRate = samplingRate;
    }

    public void markActiveAt(int row_idx, int col_idx) {
        states[row_idx][col_idx] = true;
    }

    /**
     * Iterate over neighborhood of samplingRate/2 and check whether or not
     * there exists an active neighbor.
     * @param p
     * @return
     */
    public boolean hasActivityAt(Point2d p) {
        int row_idx = p.rounded().iU();
        int col_idx = p.rounded().iV();
        int upper = row_idx - samplingRate/2;
        if (upper < 0) upper = 0;
        int lower = row_idx + samplingRate/2;
        if (lower >= m) lower = m-1;

        int left = col_idx - samplingRate/2;
        if (left < 0) left = 0;
        int right = col_idx + samplingRate/2;
        if (right >= n) right = n-1;

        boolean hasActiveNeighbor = false;
        for (int k1 = upper; k1 <= lower; k1++) {
            for (int k2 = left; k2 <= right; k2++) {
                hasActiveNeighbor = hasActiveNeighbor || states[k1][k2];
            }
        }

        return hasActiveNeighbor;
    }

    /**
     * Reset all internal states to false
     * i.e. they are not active
     */
    public void flushStates() {
        states = new boolean[m][n];
    }

    public boolean[][] getStates() {
        return states;
    }

    public void copyStates(Activity other) {
        boolean[][] states_other = other.getStates();
        for (int k1 = 0; k1 < m; k1++) {
            for (int k2 = 0; k2 < n; k2++) {
                states[k1][k2] = states_other[k1][k2];
            }
        }
    }

}

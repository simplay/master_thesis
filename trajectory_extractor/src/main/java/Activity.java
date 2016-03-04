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
        this.samplingRate = samplingRate;
    }

    public void markActiveAt(int row_idx, int col_idx) {
        states[m][n] = true;
    }

    /**
     * Iterate over neighborhood of samplingRate/2 and check whether or not
     * there exists an active neighbor.
     * @param row_idx
     * @param col_idx
     * @return
     */
    public boolean hasActivityAt(int row_idx, int col_idx) {
        int upper = row_idx - samplingRate/2;
        if (upper < 0) upper = 0;
        int lower = row_idx + samplingRate/2;
        if (lower > m) lower = m;

        int left = col_idx - samplingRate/2;
        if (left < 0) left = 0;
        int right = col_idx + samplingRate/2;
        if (right > n) right = n;

        boolean hasActiveNeighbor = false;
        for (int k1 = upper; k1 <= lower; k1++) {
            for (int k2 = left; k2 <= right; k2++) {
                hasActiveNeighbor = hasActiveNeighbor || states[k1][k2];
            }
        }

        return hasActiveNeighbor;
    }
}

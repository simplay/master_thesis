package datastructures;

/**
 * InvalidRegionsMask is used to decide whether or not we have to
 * stop the tracking of a trajectory (i.e. mark it as 'closed')
 *
 * It purpose is to wrap the so called consistency matrix, read by the InvalidRegionReader.
 *
 * A location is supposed to be invalid if:
 *  => it is occluded: using the computed tracked to position via the forward flow and going back via the backward flow
 *  does not yield the initial position. Thus, the queried position is occluded.
 *
 *  => the brightness varies too much: too bright locations may cause wrong pixel tracings and thus have to be skipped.
 *
 * Note that each frame in the dataset is supposed to have an own InvalidRegionsMask.
 *
 */
public class InvalidRegionsMask extends Interpolator {

    // The validity state of a frame
    // It only contains the values 0 (false) and 1 (true).
    private double[][] state;

    /**
     * Construct a new InvalidRegionMask
     *
     * @param m row count
     * @param n column count
     */
    public InvalidRegionsMask(int m, int n) {
        state = new double[m][n];
    }

    /**
     * Set the value of a complete row in the InvalidRegionMask
     *
     * @param row_idx row index we want to set.
     * @param row the row values.
     */
    public void setRow(int row_idx, double[] row) {
        state[row_idx] = row;
    }

    /**
     * Set a value at a given mask position.
     *
     * @param i row index
     * @param j column index
     * @param value mask state to be set at a given location.
     */
    public synchronized void setAt(int i, int j, double value) {
        state[i][j] = value;
    }

    /**
     * Checks whether a given tracked position is within an invalid region.
     * Such invalid regions cause to end a trajectory (i.e. it will be discontinued).
     *
     * Since we only have discrete invalid region sampling points but tracking points
     * are continuous valued (i.e. are in between grid locations), we have to apply
     * some mechanism to decide which value a in-between grid point belongs to.
     *
     * We make use of bilinear interpolation. We interpret the value 'true'
     * as equal '1' and the value 'false' equal to the value '0'. Then we
     * compute the bilinear interpolated value in the grid, given a query location.
     * This value acts as a probability for being true (i.e. what's the probability
     * for being true-valued). We then further apply a thresholding: if the probability
     * is at least 0.5, then, we assume it is true (similar like transistor state interpretation works).
     *
     * Alternative approach:
     *
     * Point2d rp = p.rounded();
     * double invalidPorb = interpolatedValueAt(state, rp.x(), rp.y());
     * return invalidPorb == 1.0;
     *
     * @param p grid lookup position
     * @return true, if the given lookup location is invalid, false otherwise.
     */
    public boolean isInvalidAt(Point2d p) {
        double invalidPorb = interpolatedValueAt(state, p.x(), p.y());
        return invalidPorb >= 0.5d;
    }
}

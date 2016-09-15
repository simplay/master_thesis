package datastructures;

/**
 * DepthField contains the depth values of a color image it is associated with.
 *
 * We assume that depth values are in meter units.
 *
 * Note that the value '0' indicates an invalid measured depth value.
 * Therefore, image locations that map to a depth value equals 0 is supposed to be an
 * invalid measurement.
 *
 * It is possible to query for the validity of a image location
 * even though there are only discrete measurements available (at pixel precession).
 * We simply consider the nearest neighbors and say, that a pixel is valid iff
 * it has all valid neighbors.
 *
 */
public class DepthField extends Interpolator {

    // Invalid depth measurement value
    private final double INVALID_DEPTH_VALUE = 0.0d;

    // Measured depth values
    private double[][] data;

    // Image dimensions
    private int m;
    private int n;

    /**
     * Construct a new depth field.
     *
     * @param m number of rows
     * @param n number of columns
     */
    public DepthField(int m, int n) {
        this.m = m;
        this.n = n;
        data = new double[m][n];
    }

    /**
     * Sets a row in the depth field at a given row index.
     *
     * @param row_idx target row index.
     * @param row the row elements at a given row index.
     */
    public void setRow(int row_idx, double[] row) {
        data[row_idx] = row;
    }

    /**
     * Get the interpolated value at a given depth field location.
     *
     * @param row_idx target row location
     * @param col_idx target column location
     * @return bilinear interpolated value at the queried position.
     */
    public double valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(data, row_idx, col_idx);
    }

    /**
     * Get the number of rows
     *
     * @return row count
     */
    public int m() {
        return m;
    }

    /**
     * Get the number of columns
     *
     * @return column count.
     */
    public int n() {
        return n;
    }

    /**
     * Set a value at a given depth field position.
     *
     * This method is used when computing the warped depth field.
     * In case the color and depth camera do not align.
     *
     * @param i row index
     * @param j column index
     * @param value depth value to be set at given depth field location.
     */
    public synchronized void setAt(int i, int j, double value) {
        data[i][j] = value;
    }

    /**
     * Get the depth values of this depth field.
     *
     * @return the measured ddepth values.
     */
    public double[][] getData() {
        return data;
    }

    /**
     * Checks whether a given location has a valid depth value.
     *
     * For deciding whether a query position has a correct depth value
     * associated with, we consider its
     * floored position, and then its right, bottom and bottom-right neighbor.
     * Iff all of these values are valid, i.e. not equal to 0, then we say, that
     * the query position has a valid depth measurement.
     *
     * This is a strict, conservative approach, resulting in never sampling accidently invalid
     * depth regions (since we use interpolated values all over the place, when looking up
     * depth values in in-between sampling points).
     *
     * @param row_idx row index
     * @param col_idx column index
     * @return true if the query position is a valid depth measurement, false otherwise.
     */
    public boolean validRegionAt(double row_idx, double col_idx) {
        return !interpolAtEqVal(data, row_idx, col_idx, INVALID_DEPTH_VALUE);
    }

}

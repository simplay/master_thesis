package datastructures;

/**
 * The depth images are scaled by a factor of 5000,
 * i.e., a pixel value of 5000 in the depth image corresponds
 * to a distance of 1 meter from the camera, 10000 to 2 meter distance,
 * etc. A pixel value of 0 means missing value/no data.
 */
public class DepthField extends Interpolator {

    private final double INVALID_DEPTH_VALUE = 0.0d;

    private double[][] data;

    public DepthField(int m, int n) {
        data = new double[m][n];
    }

    public void setRow(int row_idx, double[] row) {
        data[row_idx] = row;
    }

    public double valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(data, row_idx, col_idx);
    }

    /**
     * Checks whether a given location has a valid depth value.
     *
     * @param row_idx
     * @param col_idx
     * @return
     */
    public boolean validRegionAt(double row_idx, double col_idx) {
        return !interpolAtEqVal(data, row_idx, col_idx, INVALID_DEPTH_VALUE);
    }

}

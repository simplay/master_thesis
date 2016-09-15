package datastructures;

/**
 * DepthVarField contains the variances of a depth field.
 *
 * It is used to normalize depth values when computing 3d motion variances,
 * used when using depth cues and running either the 'SAED' or the 'PAED' Similarity task.
 *
 * It prevents us from sampling 3d motion distance outliers when noisy depth cues/data is used.
 *
 * Its values are in meter scale (i.e. metric units) and computed by applying a 2-pass bilateral filter.
 * The actual variance values are computed by using the filter's weights.
 *
 * See '../initialize_data/src/coputeLocalDepthVar.m' for further information.
 *
 */
public class DepthVarField extends Interpolator {

    // Depth variance values in meters^2
    private double[][] matrix;

    /**
     * Construct a DepthVarField
     * @param m number of rows.
     * @param n number of columns.
     */
    public DepthVarField(int m, int n) {
        matrix = new double[m][n];
    }

    /**
     * Get the depth variance values of this depth field.
     *
     * @return the computed depth variance values.
     */
    public double[][] getData() {
        return matrix;
    }

    /**
     * Sets a row in the depth variance matrix.
     *
     * @param row_idx target row index.
     * @param row row elements to be set.
     */
    public void setRow(int row_idx, double[] row) {
        matrix[row_idx] = row;
    }

    /**
     * Set a value at a given depth variance field position.
     *
     * This method is used when computing the warped depth var field.
     * In case the color and depth camera do not align.
     *
     * @param i row index
     * @param j column index
     * @param value depth variance value to be set at given depth field location.
     */
    public synchronized void setAt(int i, int j, double value) {
        matrix[i][j] = value;
    }

    /**
     * Get the bilinear interpolated depth variance value at a given location.
     *
     * @param row_idx row lookup position.
     * @param col_idx column lookup position.
     * @return bilinear interpolated value.
     */
    public double valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(matrix, row_idx, col_idx);
    }

}

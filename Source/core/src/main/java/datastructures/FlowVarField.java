package datastructures;

/**
 * FlowVarField contains the flow variance values of an optical flow.
 * The variance values were computed by computing the filter weights of a bilateral filter
 * applied on the selected dataset.
 *
 * For further information, please refer to the Matlab script 'init_data.m'.
 *
 * Variance values are in pixel units and only computed for 2d motions.
 * 3d motions are computed on-the-fly when using depth cues and running a corresponding SimilarityTask.
 *
 * These variance values are used to normalize the motion distances. The absolute flow difference between
 * two overlapping trajectory points is divided by this variance value.
 *
 * This helps to reduce noise at locations, that have a huge motion distance but also a large variance value at that position.
 *
 * Such noisy positions indicate a unreliable flow value in general (resulting due to an inaccurate flow computation).
 *
 * Those variance values are used within all SimilarityTask instances.
 */
public class FlowVarField extends Interpolator {

    // Flow variance values in pixel units
    private double[][] matrix;

    /**
     * Construct a new FlowVarField instance.
     *
     * @param m number of rows.
     * @param n number of columns.
     */
    public FlowVarField(int m, int n) {
        this.matrix = new double[m][n];
    }

    /**
     * Get the bilinear interpolated flow variance value at a given location.
     *
     * @param p lookup position.
     * @return bilinear interpolated value.
     */
    public double valueAt(Point2d p) {
        return interpolatedValueAt(matrix, p.x(), p.y());
    }

    /**
     * Sets a row in the flow variance matrix.
     *
     * @param row_idx target row index.
     * @param row row elements to be set.
     */
    public void setRow(int row_idx, double[] row) {
        matrix[row_idx] = row;
    }
}

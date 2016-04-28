package datastructures;

import java.util.ArrayList;

/**
 * A 3 x 4 double matrix, defining the extrinsic parameters for aligning the color to the depth camera.
 */
public class Mat3x4 {

    // matrix elements
    private double[][] data;

    // matrix cols
    private final ArrayList<Point3d> cols = new ArrayList<>(4);

    /**
     * Create a new matrix by its rows
     * @param r1 row 1
     * @param r2 row 2
     * @param r3 row 3
     */
    public Mat3x4(double[] r1, double[] r2, double[] r3) {
        data = new double[3][4];
        data[0] = r1;
        data[1] = r2;
        data[2] = r3;

        cols.add(new Point3d(r1[0], r2[0], r3[0]));
        cols.add(new Point3d(r1[1], r2[1], r3[1]));
        cols.add(new Point3d(r1[2], r2[2], r3[2]));
        cols.add(new Point3d(r1[3], r2[3], r3[3]));
    }

    /**
     * Retrieves the matrix
     *
     *  1 0 0 0
     *  0 1 0 0
     *  0 0 1 0
     *
     * @return the one 3x4 matrix
     */
    public static Mat3x4 one() {
        double[] r1 = {1, 0, 0, 0};
        double[] r2 = {0, 1, 0, 0};
        double[] r3 = {0, 0, 1, 0};
        return new Mat3x4(r1, r2, r3);
    }

    /**
     * Returns the i-th col.
     * Not that indices are enumerated starting at index 0.
     *
     * @param col_idx target col index of the matrix
     * @return a col encoded as a point3d instance.
     */
    public Point3d getCol(int col_idx) {
        return cols.get(col_idx);
    }
}

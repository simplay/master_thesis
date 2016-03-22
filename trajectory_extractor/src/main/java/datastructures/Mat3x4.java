package datastructures;

import java.util.ArrayList;

/**
 * A 3 x 4 double matrix, defining the extrinsic parameters for aligning the color to the depth camera.
 */
public class Mat3x4 {

    // matrix elements
    private double[][] data;

    // matrix rows
    private final ArrayList<Point3d> rows = new ArrayList<>(3);

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

        rows.add(new Point3d(r1[0], r1[1], r1[2]));
        rows.add(new Point3d(r2[0], r2[1], r2[2]));
        rows.add(new Point3d(r3[0], r3[1], r3[2]));
    }

    /**
     * Returns the i-th row.
     * Not that indices are enumerated starting at index 0.
     *
     * @param row_idx target row index of the matrix
     * @return a row encoded as a point3d instance.
     */
    public Point3d getRow(int row_idx) {
        return rows.get(row_idx);
    }
}

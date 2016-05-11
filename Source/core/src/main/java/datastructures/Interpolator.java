package datastructures;

/**
 * This class implements bilinear interpolation methods for 3d points and scalars.
 */
public class Interpolator {

    /**
     * Compute bilinear interpolation of a given flow position
     *
     * @param data target flow direction values
     * @param x corresponds to row index
     * @param y corresponds to column index
     * @return interpolated flow value
     */
    public double interpolatedValueAt(double[][] data, double x, double y) {
        int m = data.length;
        int n = data[0].length;
        if (x > m-1 || y > n-1) {
            throw new ArrayIndexOutOfBoundsException("Dimensions (m,n)=("+m+","+n+") but accessing (x,y)=("+x+","+y+")" );
        }

        int px_i = (int) Math.floor(x);
        int py_i = (int) Math.floor(y);

        int px_i2 = px_i + 1;
        int py_i2 = py_i + 1;

        double dx = x - px_i;
        double dy = y - py_i;

        double f_00 = data[px_i][py_i];
        double f_01 = saveGetAt(data, px_i, py_i2); // data[px_i][py_i2];
        double f_10 = saveGetAt(data, px_i2, py_i); // data[px_i2][py_i];
        double f_11 = saveGetAt(data, px_i2, py_i2); // data[px_i2][py_i2];

        double sum = 0d;
        sum += f_00*(1.0d-dx)*(1.0d-dy);
        sum += f_01*(1.0d-dx)*dy;
        sum += f_10*dx*(1.0d-dy);
        sum += f_11*dx*dy;

        return sum;
    }

    /**
     * Compute bilinear interpolated 3d point given a 2d array of 3d points for a given location.
     *
     * @param data matrix containing 3d points
     * @param x row idx
     * @param y column idx
     * @return bilinear interpolated 3d point
     */
    public Point3d interpolatedValueAt(Point3d[][] data, double x, double y) {
        int m = data.length;
        int n = data[0].length;
        if (x > m-1 || y > n-1) {
            throw new ArrayIndexOutOfBoundsException("Dimensions (m,n)=("+m+","+n+") but accessing (x,y)=("+x+","+y+")" );
        }

        int px_i = (int) Math.floor(x);
        int py_i = (int) Math.floor(y);

        int px_i2 = px_i + 1;
        int py_i2 = py_i + 1;

        double dx = x - px_i;
        double dy = y - py_i;

        Point3d f_00 = data[px_i][py_i];
        Point3d f_01 = saveGetAt(data, px_i, py_i2); // data[px_i][py_i2];
        Point3d f_10 = saveGetAt(data, px_i2, py_i); // data[px_i2][py_i];
        Point3d f_11 = saveGetAt(data, px_i2, py_i2); // data[px_i2][py_i2];

        double i_x = bilinearInterpolatedValue(f_00.x(), f_01.x(), f_10.x(), f_11.x(), dx, dy);
        double i_y = bilinearInterpolatedValue(f_00.y(), f_01.y(), f_10.y(), f_11.y(), dx, dy);
        double i_z = bilinearInterpolatedValue(f_00.z(), f_01.z(), f_10.z(), f_11.z(), dx, dy);
        return new Point3d(i_x, i_y, i_z);
    }

    /**
     * Apply the bilinear combination scheme.
     *
     * @param f_00 lookup value left top
     * @param f_01 lookup value right top
     * @param f_10 lookup value left bottom
     * @param f_11 lookup value right bottom
     * @param dx weight width
     * @param dy weight height
     * @return interpolated value
     */
    private double bilinearInterpolatedValue(double f_00, double f_01, double f_10, double f_11, double dx, double dy) {
        double sum = 0d;
        sum += f_00*(1.0d-dx)*(1.0d-dy);
        sum += f_01*(1.0d-dx)*dy;
        sum += f_10*dx*(1.0d-dy);
        sum += f_11*dx*dy;
        return sum;
    }

    /**
     * Get the exact value at a given location (not interpolated).
     * In case we query for an in-existent location, then the value 0 is returned.
     *
     * @param data 2d array
     * @param idx row idx
     * @param idy column idx
     * @return value at given lookup positions.
     */
    protected double saveGetAt(double[][] data, int idx, int idy) {
        int m = data.length;
        int n = data[0].length;
        if (idx == m || idy == n) {
            return 0d;
        }
        return data[idx][idy];
    }

    /**
     * Get the exact 3d point at a given location (not interpolated).
     * In case we query for an in-existent location, then the zero point is returned.
     *
     * @param data 2d array
     * @param idx row idx
     * @param idy column idx
     * @return 3d point at given lookup positions.
     */
    protected Point3d saveGetAt(Point3d[][] data, int idx, int idy) {
        int m = data.length;
        int n = data[0].length;
        if (idx == m || idy == n) {
            return new Point3d(0,0,0);
        }
        return data[idx][idy];
    }

    /**
     * Checks whether a given interpolation point is equal to a given reference value.
     *
     * This method is used to check depth fields whether the exhibit invalid depth information
     * at a certain location (i.e. are equal the value 0).
     *
     * @param data 2d array
     * @param x row idx
     * @param y column idx
     * @param refValue reference value we want to check for.
     * @return true if any interpolation point is equal to the given reference value,
     *  false otherwise.
     */
    public boolean interpolAtEqVal(double[][] data, double x, double y, double refValue) {
        int px_i = (int) Math.floor(x);
        int py_i = (int) Math.floor(y);

        int px_i2 = px_i + 1;
        int py_i2 = py_i + 1;

        double f_00 = data[px_i][py_i];
        double f_01 = saveGetAt(data, px_i, py_i2); // data[px_i][py_i2];
        double f_10 = saveGetAt(data, px_i2, py_i); // data[px_i2][py_i];
        double f_11 = saveGetAt(data, px_i2, py_i2); // data[px_i2][py_i2];

        if (f_00 == refValue || f_01 == refValue || f_10 == refValue || f_11 == refValue) {
            return true;
        }
        return false;
    }
}

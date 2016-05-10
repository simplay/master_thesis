package datastructures;

import managers.CalibrationManager;
import managers.DepthManager;

/**
 * Defines a two dimensional Point offering basic vector operations.
 * Used for modeling tracked trajectory points.
 *
 * Note that the x component denotes a row index within an image of a
 * tracked feature point and y its column.
 */
public class Point2d {

    // The row index of a tracked position within an image.
    private double x;

    // The column index of a tracked position within an image.
    private double y;

    // Is this tracked point valid.
    private boolean isValid = true;

    // Euclidian position is space.
    private Point3d depthPos;

    /**
     * Get the constant zero as a point
     *
     * @return a point defining the zero constant.
     */
    public static Point2d zero() {
        return new Point2d(0d, 0d);
    }

    /**
     * Get the constant one as a point
     *
     * @return a point defining the one constant.
     */
    public static Point2d one() {
        return new Point2d(1d, 1d);
    }

    /**
     * Construct a new point by two components
     *
     * @param x row image index of tracked feature
     * @param y column image index of tracked feature
     */
    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a new point by a list of strings.
     * containing two elements.
     *
     * @param xy list of point components.
     */
    public Point2d(String[] xy) {
        this.x = Double.parseDouble(xy[0]);
        this.y = Double.parseDouble(xy[1]);
    }

    /**
     * Swaps the value of the x and the y component.
     */
    public void swapComponents() {
        double tmp = x;
        x = y;
        y = tmp;
    }

    /**
     * Checks whether this is a valid point.
     * By default every point is valid.
     *
     * @return is this a valid point.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Marks this point as invalid.
     */
    public void markAsInvalid() {
        this.isValid = false;
    }

    /**
     * Creates a shallow copy of this point.
     *
     * @return new point having the same components as this point.
     */
    public Point2d copy() {
        return new Point2d(x,y);
    }

    /**
     * Subtracts another point from this one.
     * This modifies the components of this point.
     *
     * @param other the point we subtract from this point.
     * @return this point after being modified.
     */
    public Point2d sub(Point2d other) {
        x -= other.x();
        y -= other.y();
        return this;
    }


    /**
     * Divides this point by a given scale.
     * This modifies the components of this point.
     *
     * @param f the divisor we use to divide the components of this point..
     * @return this point after being modified.
     */
    public Point2d div_by(double f) {
        x /= f;
        y /= f;
        return this;
    }

    /**
     * Transforms this point to the 3d Euclidean space by making use of depth cues and camera calibration data.
     * The value will be in meters. The result is stored in depthPos
     *
     * Please notice that x denotes the column image index and y the row image index
     * of tracked features. Hence, the calibration data also has to be assigned accordingly.
     * Usually, such calibration data components are given in a cartesian coordinate system.
     *
     * @param frame_idx the frame this point belongs to.
     *
     * For performing the transformation we apply the following transformation:
     *  P = [(u-p_x^d)/f_x^d *z, (v-p_y^d)/f_y^d *z), z]
     *  E*P = [xx, yy, zz]
     *  (x/z * f_x^c + p_x^c, y/z * f_y^c + p_y^c)
     */
    public void compute3dTrackedPosition(int frame_idx) {
        double depth = DepthManager.getInstance().get(frame_idx).valueAt(x,y);
        double _x = depth*((x - CalibrationManager.depth_principal_point().x()) / CalibrationManager.depth_focal_len().x());
        double _y = depth*((y - CalibrationManager.depth_principal_point().y()) / CalibrationManager.depth_focal_len().y());
        this.depthPos = new Point3d(_x, _y, depth);
    }

    /**
     * Depth position in euclidian space after applying the depth/color camera calibrations.
     *
     * @return position of this coordinate in the Euclidian space.
     */
    public Point3d getEuclidPos() {
        return depthPos;
    }

    /**
     * Get the column image index of this tracked feature point.
     *
     * @return column index
     */
    public double x() {
        return u();
    }

    /**
     * Get the row image index of this tracked feature point.
     *
     * @return row index
     */
    public double y() {
        return v();
    }

    /**
     * Truncated column index
     *
     * @return integer lookup column index
     */
    public int iU() {
        return (int) x;
    }

    /**
     * Truncated row index.
     *
     * @return integer lookup row index
     */
    public int iV() {
        return (int) y;
    }


    /**
     * Creates a new point containing rounded x,y components from this point.
     *
     * @return a point containing the rounded components of this point.
     */
    public Point2d rounded() {
        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        return new Point2d(px, py);
    }

    /**
     * Computes the l2 distance of this point
     *
     * @return l2 distance
     */
    public double length() {
        return Math.sqrt(length_squared());
    }

    /**
     * Computes the squared l2 distance of this point.
     *
     * @return squared l2 distance.
     */
    public double length_squared() {
        return x*x + y*y;
    }

    /**
     * Get the column index of this point.
     *
     * @return column index.
     */
    public double u() {
        return x;
    }

    /**
     * Get the row index of this point.
     *
     * @return row index
     */
    public double v() {
        return y;
    }

    /**
     * Debugging string representation of this point.
     *
     * @return debugging representation
     */
    public String toString() {
        return "(x,y)=("+ x +","+y+")";
    }

    /**
     * Get the pretty string representation of this point
     * used by the file writers.
     *
     * @return serialized point 2 representation.
     */
    public String toOutputString() {
        return x + " " + y;
    }
}

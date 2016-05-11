package datastructures;

/**
 * Models a 3d dimensional point in the Euclidean space.
 * Supports basic vector operations.
 */
public class Point3d {

    // The components of this 3d point
    private double x;
    private double y;
    private double z;

    // A Point is invalid if it has a depth value equal to 0.0d
    private boolean isValid = true;

    /**
     * Construct a new Point by providing the 3 component values.
     *
     * @param x 1st component
     * @param y 2nd component
     * @param z 3rd component
     */
    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Make a shallow copy of this Point3d instance.
     *
     * @return shallow copy
     */
    public Point3d copy() {
        return new Point3d(x, y, z);
    }

    /**
     * Subtract another 3d from this point.
     *
     * Note that the component of this points are modified when calling this method.
     *
     * @param other reference point we subtract from this point.
     * @return updated version of this point
     */
    public Point3d sub(Point3d other) {
        this.x -= other.x();
        this.y -= other.y();
        this.z -= other.z();

        return this;
    }

    /**
     * Checks whether this point is marked as invalid.
     *
     * This is-valid-flag is used to filter invalid 3d points (e.g. points,
     * that do not exhibit a valid depth value).
     *
     * @return true if this point is invalid, false otherwise (by default).
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Marks the state of this point as invalid.
     * This state is not copied when crating a copy of this instance.
     */
    public void markInvalid() {
        this.isValid = false;
    }

    /**
     * Apply a (3 x 4) matrix to this 3d point
     * Herby, we interpret the 3d point as a 4d homogeneous point.
     *
     * Thus, a scale and shift is applied to this 3d point.
     *
     * The matrix we usually apply to a 3d point is the so called
     * extrinsic camera matrix.
     *
     * Note that the internal state of this point is modified
     * by applying this transformation.
     *
     * @param mat 3x4 (extrinsic) matrix
     */
    public Point3d transformBy(Mat3x4 mat) {
        Point3d col1 = mat.getCol(0);
        Point3d col2 = mat.getCol(1);
        Point3d col3 = mat.getCol(2);
        Point3d col4 = mat.getCol(3);

        this.x = col1.x()*x + col2.x()*y + col3.x()*z + col4.x();
        this.y = col1.y()*x + col2.y()*y + col3.y*z + col4.y();
        this.z = col1.z()*x + col2.z()*y + col3.z()*z + col4.z();

        return this;
    }

    /**
     * Divides this point by a given scale.
     * This modifies the components of this point.
     *
     * @param f the divisor we use to divide the components of this point.
     * @return this point after being modified.
     */
    public Point3d div_by(double f) {
        x /= f;
        y /= f;
        z /= f;
        return this;
    }

    /**
     * Scales the components of this point by a given scale.
     * This modifies the components of this point.
     *
     * @param f the factor we use to divide the components of this point..
     * @return this point after being modified.
     */
    public Point3d scaleBy(double f) {
        x *= f;
        y *= f;
        z *= f;
        return this;
    }

    /**
     * Computes the squared l2 distance of this point.
     *
     * @return squared l2 distance.
     */
    public double length_squared() {
        return x*x + y*y + z*z;
    }

    /**
     * Computed the l2 distance to the origin.
     *
     * @return l2 distance
     */
    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * Get the x component of this 3d point
     *
     * @return x component
     */
    public double x() {
        return x;
    }

    /**
     * Get the y component of this 3d point
     *
     * @return y component
     */
    public double y() {
        return y;
    }

    /**
     * Get the z component of this 3d point
     *
     * @return z component
     */
    public double z() {
        return z;
    }

    /**
     * Get the pretty string representation used for dubbing.
     *
     * @return debuggable representation of this 3d point.
     */
    public String toString() {
        return "(x,y,z)=("+ x + "," + y + "," + z + ")";
    }
}

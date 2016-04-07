package datastructures;

import managers.DepthManager;

public class Point3d {
    private double x;
    private double y;
    private double z;

    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3d copy() {
        return new Point3d(x,y,z);
    }

    public Point3d sub(Point3d other) {
        this.x -= other.x();
        this.y -= other.y();
        this.z -= other.z();

        return this;
    }

    /**
     * Scales this point3 by an extrinsic camera 3x4 double matrix.
     *
     * @param mat 3x4 matrix
     */
    public void scaleByMat(Mat3x4 mat) {
        Point3d col1 = mat.getCol(0);
        Point3d col2 = mat.getCol(1);
        Point3d col3 = mat.getCol(2);
        Point3d col4 = mat.getCol(3);

        this.x = col1.x()*x + col2.x()*y + col3.x()*z + col4.x();
        this.y = col1.y()*x + col2.y()*y + col3.y*z + col4.y();
        this.z = col1.z()*x + col2.z()*y + col3.z()*z + col4.z();
    }

    public double length_squared() {
        return x*x + y*y + z*z;
    }

    /**
     * Computed the l2 distance to the origin
     * @return
     */
    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public String toString() {
        return "(x,y,z)=("+ x + "," + y + "," + z + ")";
    }
}

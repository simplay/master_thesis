package datastructures;

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

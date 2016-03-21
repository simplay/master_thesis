package datastructures;

import managers.DepthManager;

public class Point2d {

    private double x;
    private double y;

    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2d copy() {
        return new Point2d(x,y);
    }

    public Point2d sub(Point2d other) {
        x -= other.x();
        y -= other.y();
        return this;
    }

    public Point2d div_by(double f) {
        x /= f;
        y /= f;
        return this;
    }

    public Point2d euclidianTransformed(int frame_idx) {
        /**
        depth = z*depth_scale(true)
        _x = ((p.x-MetaInfo.build.p_d.x) / MetaInfo.build.f_d.x)*depth
        _y = ((p.y-MetaInfo.build.p_d.y) / MetaInfo.build.f_d.y)*depth
        p3 = Point3f.new([_x, _y, depth])
        pp3 = MetaInfo.build.extrinsic_camera_mat.mult(p3)
        return pp3 if cameras_overlapping
                depth = pp3.z
        x = (pp3.x*MetaInfo.build.f_c.x)/depth + MetaInfo.build.p_c.x
        y = (pp3.y*MetaInfo.build.f_c.y)/depth + MetaInfo.build.p_c.y
        Point.new([x, y])
        **/

        double depth = DepthManager.getInstance().get(frame_idx).valueAt(x,y);

        return new Point2d(0,0);
    }

    public double x() {
        return u();
    }

    public double y() {
        return v();
    }

    public int iU() {
        return (int) x;
    }

    public int iV() {
        return (int) y;
    }

    public Point2d rounded() {
        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        return new Point2d(px, py);
    }

    public double length() {
        return Math.sqrt(length_squared());
    }

    public double length_squared() {
        return x*x + y*y;
    }

    public double u() {
        return x;
    }

    public double v() {
        return y;
    }

    public String toString() {
        return "(x,y)=("+ x +","+y+")";
    }

    public String toOutputString() {
        return x + " " + y;
    }
}

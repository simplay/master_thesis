package datastructures;

import managers.CalibrationManager;
import managers.DepthManager;
import readers.CandidateFileReader;

public class Point2d {

    private double x;
    private double y;

    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2d(String[] xy) {
        this.x = Double.parseDouble(xy[0]);
        this.y = Double.parseDouble(xy[1]);
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

    /**
     * Transforms to Euclidian space and back to camera space
     *
     * P = [(u-p_x^d)/f_x^d *z, (v-p_y^d)/f_y^d *z), z]
     * E*P = [xx, yy, zz]
     * (x/z * f_x^c + p_x^c, y/z * f_y^c + p_y^c)
     */
    public Point2d transformToOveralappingDepthColorCamPixelCoors(int frame_idx) {
        double depth = DepthManager.getInstance().get(frame_idx).valueAt(x,y);
        double _x = depth*((x - CalibrationManager.depth_principal_point().x()) / CalibrationManager.depth_focal_len().x());
        double _y = depth*((y - CalibrationManager.depth_principal_point().y()) / CalibrationManager.depth_focal_len().y());

        // 3d euclidian space point
        Point3d p3 = new Point3d(_x, _y, depth);
        p3.scaleByMat(CalibrationManager.extrinsicMat());

        return new Point2d(
                (p3.x()*CalibrationManager.rgb_focal_len().x() / depth) + CalibrationManager.rgb_principal_point().x(),
                (p3.y()*CalibrationManager.rgb_focal_len().y() / depth) + CalibrationManager.rgb_principal_point().y()
        );
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

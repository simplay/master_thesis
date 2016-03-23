package managers;

import datastructures.Mat3x4;
import datastructures.Point2d;

import java.util.ArrayList;

public class CalibrationManager {

    private static CalibrationManager instance = null;
    private Mat3x4 extrinsicMat;
    private Point2d rgb_focal_len;
    private Point2d rgb_principal_point;
    private Point2d depth_focal_len;
    private Point2d depth_principal_point;

    public static CalibrationManager getInstance(ArrayList<String> data) {
        if (instance == null && data != null) {
            instance = new CalibrationManager(data);
        }
        return instance;
    }

    public static CalibrationManager getInstance() {
        return getInstance(null);
    }

    public static Point2d rgb_focal_len() {
        return getInstance().getRgbFocalLen();
    }

    public static Point2d rgb_principal_point() {
        return getInstance().getRgbPrincipalPoint();
    }

    public static Point2d depth_focal_len() {
        return getInstance().getDepthFocalLen();
    }

    public static Point2d depth_principal_point() {
        return getInstance().getDepthPrincipalPoint();
    }

    public static Mat3x4 extrinsicMat() {
        return getInstance().getExtrinsicMat();
    }

    /**
     * Calibration data format:
     * The rgb intrinsics dimension
     *  line1: focal length x y
     *  line2: principal point x y
     * The depth intrinsics dimension:
     *  line4: focal length
     *  line5: principal point
     *
     * line 6-8: The Extrinsic parameters 4x3 matrix describing
     * the transformation from color to depth.
     * @param calibData
     */
    public CalibrationManager(ArrayList<String> calibData) {

        // depth intrinsics dimension
        // focal length
        String[] rgb_focalLen = calibData.get(1).split(" ");
        this.rgb_focal_len = new Point2d(rgb_focalLen);

        // principal point
        String[] rgb_principalPoint = calibData.get(2).split(" ");
        this.rgb_principal_point = new Point2d(rgb_principalPoint);

        // depth intrinsics dimension

        // focal length
        String[] depth_focalLen = calibData.get(4).split(" ");
        this.depth_focal_len = new Point2d(depth_focalLen);

        // principal point
        String[] depth_principalPoint = calibData.get(5).split(" ");
        this.depth_principal_point = new Point2d(depth_principalPoint);

        // The Extrinsic parameters 4x3 matrix describing:
        // the transformation from color to depth, empty line
        String[] extrRow1 = calibData.get(6).split(" ");
        String[] extrRow2 = calibData.get(7).split(" ");
        String[] extrRow3 = calibData.get(8).split(" ");

        extrinsicMat = new Mat3x4(
                toDoubleArray(extrRow1),
                toDoubleArray(extrRow2),
                toDoubleArray(extrRow3)
        );
    }

    public Point2d getRgbFocalLen() {
        return rgb_focal_len;
    }

    public Point2d getRgbPrincipalPoint() {
        return rgb_principal_point;
    }

    public Point2d getDepthFocalLen() {
        return depth_focal_len;
    }

    public Point2d getDepthPrincipalPoint() {
        return depth_principal_point;
    }

    public Mat3x4 getExtrinsicMat() {
        return extrinsicMat;
    }

    private static double[] toDoubleArray(String[] a) {
        double[] data = new double[a.length];

        for (int idx = 0; idx < a.length; idx++) {
            data[idx] = Double.parseDouble(a[idx]);
        }

        return data;
    }
}

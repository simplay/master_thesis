package managers;

import datastructures.LabeledFile;
import datastructures.Mat3x4;
import datastructures.Point2d;

public class CalibrationManager {

    private static CalibrationManager instance = null;

    // The extrinsic camera calibration matrix
    private Mat3x4 extrinsicMat;

    // intrinsic param: focal length of the rgb camera
    private Point2d rgb_focal_len;

    // intrinsic param: principal point of the rgb camera
    private Point2d rgb_principal_point;

    // intrinsic param: focal length of the depth camera
    private Point2d depth_focal_len;

    // intrinsic param: principal point of the depth camera
    private Point2d depth_principal_point;

    // Indicates whether any rgb camera intrinsic data loaded
    private boolean hasColorIntrinsicDataLoaded;

    // Indicates whether any depth camera intrinsic data loaded
    private boolean hasDepthIntrinsicDataLoaded;

    public static CalibrationManager getInstance(LabeledFile file) {
        if (instance == null && file != null) {
            instance = new CalibrationManager(file);
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
     * @param calibrationFile
     */
    public CalibrationManager(LabeledFile calibrationFile) {

        // Initialize intrinsic parameters with default values:
        // f = (1,1) and p = (0,0)
        // since f acts as a scale factor and p as a shift parameter,
        // thus, these default values won't have an effect when being applied
        this.rgb_focal_len = Point2d.one();
        this.rgb_principal_point = Point2d.zero();

        this.depth_focal_len = Point2d.one();
        this.depth_principal_point = Point2d.zero();

        // Initialize the default 'one' valued extrinsic matrix.
        this.extrinsicMat = Mat3x4.one();

        if (calibrationFile.hasDepth()) {
            String[] depth_focalLen = calibrationFile.getLineByLabel("f_d").getContent().split(" ");
            this.depth_focal_len = new Point2d(depth_focalLen);

            String[] depth_principalPoint = calibrationFile.getLineByLabel("p_d").getContent().split(" ");
            this.depth_principal_point = new Point2d(depth_principalPoint);
        }

        if (calibrationFile.hasRGB()) {
            String[] rgb_focalLen = calibrationFile.getLineByLabel("f_rgb").getContent().split(" ");
            this.rgb_focal_len = new Point2d(rgb_focalLen);

            String[] rgb_principalPoint = calibrationFile.getLineByLabel("p_rgb").getContent().split(" ");
            this.rgb_principal_point = new Point2d(rgb_principalPoint);
        }

        if (calibrationFile.hasHasExtrinsicMat()) {
            String[] extrRow1 = calibrationFile.getLineByLabel("e_1").getContent().split(" ");
            String[] extrRow2 = calibrationFile.getLineByLabel("e_2").getContent().split(" ");
            String[] extrRow3 = calibrationFile.getLineByLabel("e_3").getContent().split(" ");

            extrinsicMat = new Mat3x4(
                    toDoubleArray(extrRow1),
                    toDoubleArray(extrRow2),
                    toDoubleArray(extrRow3)
            );
        }

        // swap x,y compontents since we work with row,col coordinates instead of regular x,y components
        rgb_focal_len.swapComponents();
        rgb_principal_point.swapComponents();
        depth_focal_len.swapComponents();
        depth_principal_point.swapComponents();

        this.hasColorIntrinsicDataLoaded = calibrationFile.hasRGB();
        this.hasDepthIntrinsicDataLoaded = calibrationFile.hasDepth();
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

    public static boolean shouldWarpDepthFields() {
        if (instance == null) return false;
        return (instance.hasColorIntrinsicDataLoaded() && instance.isHasDepthIntrinsicDataLoaded());
    }

    public boolean hasColorIntrinsicDataLoaded() {
        return hasColorIntrinsicDataLoaded;
    }

    public boolean isHasDepthIntrinsicDataLoaded() {
        return hasDepthIntrinsicDataLoaded;
    }

    public static boolean hasNoDepthIntrinsics() {
        if (instance == null) return true;
        return !instance.isHasDepthIntrinsicDataLoaded();
    }
}

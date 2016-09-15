package managers;

import datastructures.LabeledFile;
import datastructures.Mat3x4;
import datastructures.Point2d;

/**
 * CalibrationManager singleton that allow to globally access
 * the intrinsic and extrinsic camera calibration data, such as
 * the focal length and the principal point of the
 * color- and the depth camera.
 *
 * These values are used to compute Euclidean distances between tracking points
 * and to align compute the warped depth field.
 *
 */
public class CalibrationManager {

    // The singleton instance
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

    /**
     * Assign the singleton with a calibration file.
     *
     * @param file the content of a labeled file.
     *
     * @return singleton instance.
     */
    public static CalibrationManager getInstance(LabeledFile file) {
        if (instance == null && file != null) {
            instance = new CalibrationManager(file);
        }
        return instance;
    }

    /**
     * Get the calibration singleton carrying the initially assigned
     * calibration file.
     *
     * @return the singleton.
     */
    public static CalibrationManager getInstance() {
        return getInstance(null);
    }

    /**
     * Get the focal length of the RGB camera.
     *
     * @return focal length of rgb the camera.
     */
    public static Point2d rgb_focal_len() {
        return getInstance().getRgbFocalLen();
    }

    /**
     * Get the principal point of the RGB camera.
     *
     * @return principal point of the rgb camera.
     */
    public static Point2d rgb_principal_point() {
        return getInstance().getRgbPrincipalPoint();
    }

    /**
     * Get the focal length of the depth camera.
     *
     * @return focal length of depth the camera.
     */
    public static Point2d depth_focal_len() {
        return getInstance().getDepthFocalLen();
    }

    /**
     * Get the principal point of the depth camera.
     *
     * @return principal point of the depth camera.
     */
    public static Point2d depth_principal_point() {
        return getInstance().getDepthPrincipalPoint();
    }

    /**
     * Get the extrinsic transformation matrix used to
     * align the depth and color camera.
     *
     * @return extrinsic transformation matrix.
     */
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

    /**
     * Get the focal length of the RGB camera.
     *
     * @return focal length of rgb the camera.
     */
    public Point2d getRgbFocalLen() {
        return rgb_focal_len;
    }

    /**
     * Get the principal point of the RGB camera.
     *
     * @return principal point of the rgb camera.
     */
    public Point2d getRgbPrincipalPoint() {
        return rgb_principal_point;
    }

    /**
     * Get the focal length of the depth camera.
     *
     * @return focal length of depth the camera.
     */
    public Point2d getDepthFocalLen() {
        return depth_focal_len;
    }

    /**
     * Get the principal point of the depth camera.
     *
     * @return principal point of the depth camera.
     */
    public Point2d getDepthPrincipalPoint() {
        return depth_principal_point;
    }

    /**
     * Get the extrinsic transformation matrix used to
     * align the depth and color camera.
     *
     * @return extrinsic transformation matrix.
     */
    public Mat3x4 getExtrinsicMat() {
        return extrinsicMat;
    }

    /**
     * Creates a double array from a series of string items.
     *
     * @param a items that should be converted to double values.
     * @return array that contains the to-double converted string items.
     */
    private static double[] toDoubleArray(String[] a) {
        double[] data = new double[a.length];
        for (int idx = 0; idx < a.length; idx++) {
            data[idx] = Double.parseDouble(a[idx]);
        }
        return data;
    }

    /**
     * Releases the singleton instance.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Checks whether we should warp the loaded depth fields.
     *
     * @return true, if we should warp the depth fields, otherwise false.
     */
    public static boolean shouldWarpDepthFields() {
        if (instance == null) return false;
        return (instance.hasColorIntrinsicDataLoaded() && instance.isHasDepthIntrinsicDataLoaded());
    }

    /**
     * Checks whether intrinsic data for the rgb camera was loaded.
     *
     * @return true if intrinsic rgb camera data was loaded, false otherwise.
     */
    public boolean hasColorIntrinsicDataLoaded() {
        return hasColorIntrinsicDataLoaded;
    }

    /**
     * Checks whether intrinsic data for the depth camera was loaded.
     *
     * @return true if intrinsic depth camera data was loaded, false otherwise.
     */
    public boolean isHasDepthIntrinsicDataLoaded() {
        return hasDepthIntrinsicDataLoaded;
    }

    /**
     * Checks whether there ware any depth intrinsics loaded.
     *
     * @return true if depth intrinsics were loaded, false otherwise.
     */
    public static boolean hasNoDepthIntrinsics() {
        if (instance == null) return true;
        return !instance.isHasDepthIntrinsicDataLoaded();
    }
}

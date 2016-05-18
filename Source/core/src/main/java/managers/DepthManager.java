package managers;

import datastructures.DepthField;
import datastructures.Mat3x4;
import datastructures.Point3d;
import java.util.ArrayList;

public class DepthManager {

    // The depeth field singleton instance.
    private static DepthManager instance = null;

    // An ordered list of all depth field.
    private ArrayList<DepthField> depthFields;

    /**
     * Get the depth field singleton instance.
     *
     * @return singleton
     */
    public static DepthManager getInstance() {
        if (instance == null) {
            instance = new DepthManager();
        }
        return instance;
    }

    /**
     * Releases the singleton and its references.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Creates a new singleton.
     */
    private DepthManager() {
        this.depthFields = new ArrayList<>();
    }

    /**
     * Append a depth field to the list of loaded depth field.
     *
     * Assumption: The position in the depth field list corresponds to
     * the frame index the depth field is associated with.
     *
     * @param depthField depth field to append.
     */
    public void add(DepthField depthField) {
        depthFields.add(depthField);
    }

    /**
     * Sets a depth field for a given frame index.
     *
     * @param depthField depth field to be set.
     * @param frame_index target frame index we want to overwrite.
     */
    public void setAt(DepthField depthField, int frame_index) {
        depthFields.set(frame_index, depthField);
    }

    /**
     * Get the number of stored depth fields.
     *
     * @return depth field count.
     */
    public int length() {
        return depthFields.size();
    }

    /**
     * Get a depth field by its frame index.
     *
     * Remember that frame indices start counting at 0.
     *
     * @param frame_idx target frame index the depth field of interest belongs to.
     * @return depth field that is associated with the given frame index.
     */
    public DepthField get(int frame_idx) {
        return depthFields.get(frame_idx);
    }

    /**
     * Warps the existing depth fields in in case the
     * color and depth cameras do not overlap.
     *
     * Lookup intrinsic and extrinsic camera calibration data
     *
     * for each dm : depth map
     *  for each d : dm
     *
     *    Perform index lookup
     *    i,j = indices(d)
     *
     *    Compute depth projected onto the color camera space:
     *    p = E * [(i - p_x) / f_x, (j - p_y) / f_y, 1]
     *    z = p.z
     *
     *    Compute its lookup coordinates
     *    i = (p.x * f_x_rgb / z) + p_x_rgb
     *    j = (p.y * f_y_rgb / z) + p_y_rgb
     *
     *    Assing warped depth field with front-most warped depth.
     *    if z > w_d[i][j] then w_d[i][j] = z;
     */
    public static void warpDepthFields() {
        Mat3x4 E = CalibrationManager.extrinsicMat();

        // Get the intrinsic camera calibration
        double p_x = CalibrationManager.depth_principal_point().x();
        double p_y = CalibrationManager.depth_principal_point().y();
        double f_x = CalibrationManager.depth_focal_len().x();
        double f_y = CalibrationManager.depth_focal_len().y();

        double p_x_rgb = CalibrationManager.rgb_principal_point().x();
        double p_y_rgb = CalibrationManager.rgb_principal_point().y();
        double f_x_rgb = CalibrationManager.rgb_focal_len().x();
        double f_y_rgb = CalibrationManager.rgb_focal_len().y();

        // Iterate over all loaded depth maps
        for(int k = 0; k < instance.length(); k++) {
            DepthField df = instance.get(k);

            // initialized the warped depth map in which initially each location
            // is marked as invalid (i.e. each lookup position is equal to zero).
            DepthField warpedDf = new DepthField(df.m(), df.n());

            // foreach element in the depth field, compute its warped depth
            // and store it at the appropriate warped depth field position.
            for (int i = 0; i < df.m(); i++) {
                for (int j = 0; j < df.n(); j++) {
                    double d = df.valueAt(i, j);

                    // depths that are zero can be skipped since their warped
                    // value is also zero and the warped depth map is by default
                    // initialized by the value zero.
                    if (d == 0d) continue;

                    Point3d p = new Point3d((i-p_x)/f_x, (j-p_y)/f_y, 1d);
                    p.scaleBy(d).transformBy(E);
                    double d_tilde = p.z();

                    // If the projected depth is invalid
                    if (d_tilde <= 0) continue;

                    // Compute lookup coordinates in warped depth map
                    double i_tilde_prime = ((p.x()*f_x_rgb) / d_tilde) + p_x_rgb;
                    double j_tilde_prime = ((p.y()*f_y_rgb) / d_tilde) + p_y_rgb;

                    int i_tilde = (int) Math.round(i_tilde_prime);
                    int j_tilde = (int) Math.round(j_tilde_prime);

                    // if i_tilde, j_tilde are within a valid depth field index range.
                    if (i_tilde >= 0 && i_tilde < df.m() && j_tilde >= 0 && j_tilde < df.n()) {

                        // takes the front most depth values of the warped depth field
                        double frontmostDepth = warpedDf.valueAt(i_tilde, j_tilde);

                        // If there is already a valid front most depth value set
                        // then overwrite it only if and only if the new depth
                        // value is smaller than the previously assigned depth.
                        // This corresponds to having the closest object front most.
                        if (frontmostDepth > 0 && d_tilde < frontmostDepth) {
                            warpedDf.setAt(i_tilde, j_tilde, d_tilde);

                        // if there has no valid depth value set yet, set the currently
                        // computed depth as the new front most depth
                        } else if(frontmostDepth == 0) {
                            warpedDf.setAt(i_tilde, j_tilde, d_tilde);
                        }

                    }
                }
            }

            // overwrite previous depth field with warped depth field
            getInstance().setAt(warpedDf, k);
        }
    }

}

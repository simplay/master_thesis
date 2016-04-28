package managers;

import datastructures.DepthField;
import datastructures.Mat3x4;
import datastructures.Point3d;

import java.util.ArrayList;

public class DepthManager {

    private static DepthManager instance = null;
    private ArrayList<DepthField> depthFields;

    public static DepthManager getInstance() {
        if (instance == null) {
            instance = new DepthManager();
        }
        return instance;
    }

    private DepthManager() {
        this.depthFields = new ArrayList<>();
    }

    public void add(DepthField depthField) {
        depthFields.add(depthField);
    }

    public int length() {
        return depthFields.size();
    }

    public DepthField get(int frame_idx) {
        return depthFields.get(frame_idx);
    }

    /**
     * Warps the existing depth fields in in case the
     * color and depth cameras do not overlap.
     *
     * TODO: describe warping mechanism
     * 
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

            for (int i = 0; i < df.m(); i++) {
                for (int j = 0; j < df.n(); j++) {
                    double d = df.valueAt(i, j);
                    Point3d p = new Point3d((i-p_x)/f_x, (j-p_y)/f_y, 1d);
                    p.scaleBy(d).transformBy(E);
                    double d_tilde = p.z();

                    // Compute lookup coordinates in warped depth map
                    int i_tilde = (int)((p.x()*f_x_rgb) / d_tilde + p_x_rgb);
                    int j_tilde = (int)((p.y()*f_y_rgb) / d_tilde + p_y_rgb);

                    // if i_tilde, j_tilde are within a valid depth field index range.
                    if (i_tilde >= 0 && i_tilde < df.m() && j_tilde >= 0 && j_tilde < df.n()) {

                        // takes the front most depth values of the warped depth field
                        if (warpedDf.valueAt(i_tilde, j_tilde) > d_tilde && d_tilde > 0) {
                            warpedDf.setAt(i_tilde, j_tilde, d_tilde);
                        }
                    }
                }
            }
        }
    }

}

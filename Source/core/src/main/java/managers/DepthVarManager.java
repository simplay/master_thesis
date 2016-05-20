package managers;

import datastructures.DepthVarField;
import java.util.ArrayList;

/**
 * DepthVarManager allows to query depth variances that belong to a dataset frame.
 *
 * Depth variances are used to normalize 3d motion distances.
 *
 * Depth variance values are in meter^2 units.
 *
 * For further information see {@link DepthVarField}.
 *
 * Assumption: The index in the internal list of depth variances corresponds to the frame index
 * the image belongs to.
 */
public class DepthVarManager {

    // The depth variance singleton
    private static DepthVarManager instance = null;

    // Internal collection of all depth variances.
    private ArrayList<DepthVarField> depthVarFields;

    /**
     * Get the depth variance field singleton
     *
     * @return singleton
     */
    public static DepthVarManager getInstance() {
        if (instance == null) {
            instance = new DepthVarManager();
        }
        return instance;
    }

    /**
     * Releases the singleton and its held references.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Construct a new singleton.
     */
    private DepthVarManager() {
        this.depthVarFields = new ArrayList<>();
    }

    /**
     * Append an depth variance field to the internal local variances list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param depthVarField depth variance field  to be added
     */
    public void add(DepthVarField depthVarField) {
        depthVarFields.add(depthVarField);
    }

    /**
     * Get the depth variance field that maps to a given dataset frame.
     *
     * Assumption: The index in the internal list of flow variances corresponds to the frame index
     * the image belongs to.
     *
     * @param frame_idx target dataset frame index.
     * @return the depth variance field that belongs to the given frame index.
     */
    public DepthVarField get(int frame_idx) {
        return depthVarFields.get(frame_idx);
    }
}

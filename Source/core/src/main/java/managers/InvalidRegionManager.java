package managers;

import datastructures.InvalidRegionsMask;
import java.util.ArrayList;

/**
 * InvalidRegionManager allows to access a mask which indicates invalid traceable frame location.
 *
 * We make use of this manager within the trajectory tracking stage in order to avoid invalid tracking
 * position. Invalid refers to the fact that a tracking point was either occluded or too uncertain (w.r.t.
 * to the precession, too bright, too large flow deviation).
 *
 * The mask is computed in the `init_data.m` script by comparing the forward and the backward flow, applied
 * on the set of all available indices. If the tracked to position, computed by using the forward flow on
 * a particular index value, does not map back, using the backward flow applied to the tracked to position, to
 * its original tracking from position, we know, that the tracking candidate position is invalid.
 *
 * Assumption: The index in the internal list of invalid traceable regions corresponds to the frame index
 * the image belongs to.
 */
public class InvalidRegionManager {

    // Internal collection of all invalid (occluded) regions.
    private ArrayList<InvalidRegionsMask> invalidRegions;

    // The invalid regions singleton
    private static InvalidRegionManager instance = null;

    /**
     * Get the invalid regions singleton
     *
     * @return singleton
     */
    public static InvalidRegionManager getInstance() {
        if (instance == null) {
            instance = new InvalidRegionManager();
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
    private InvalidRegionManager() {
        invalidRegions = new ArrayList<InvalidRegionsMask>();
    }

    /**
     * Append an invalid regions mask to the internal local variances list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param invalidRegion invalid regions mask to be added
     */
    public void addInvalidRegion(InvalidRegionsMask invalidRegion) {
        invalidRegions.add(invalidRegion);
    }

    /**
     * Get the invalid regions mask that maps to a given dataset frame.
     *
     * Assumption: The index in the internal list of flow variances corresponds to the frame index
     * the image belongs to.
     *
     * @param frame_idx target dataset frame index.
     * @return the invalid regions mask that belongs to the given frame index.
     */
    public InvalidRegionsMask getInvalidRegionAt(int frame_idx) {
        return invalidRegions.get(frame_idx);
    }

}


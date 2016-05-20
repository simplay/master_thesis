package managers;

import datastructures.FlowField;
import pipeline_components.Tracker;

import java.util.ArrayList;

/**
 * FlowFieldManager allows to access the forward-and backward flow fields that belong to
 * a dataset frame.
 *
 * The flow fields are used for tracing tracking points in {@link Tracker} or
 * to compute motion distances in all similarity tasks.
 *
 * Flow fields are in pixel units and indicate how much a pixel moves
 * from a given frame location to the location in its successor frame.
 *
 * Note that the internal collection of the forward-and backward flows have the same size,
 * and a given index in one flow collection belongs directly to the the value on the other flow
 * collection with the same index value.
 *
 * Assumption: The index in the internal list of flow fields corresponds to the frame index
 * the image belongs to.
 */
public class FlowFieldManager {

    // Internal collection of all forward flows
    private ArrayList<FlowField> forwardFlows;

    // Internal collection of all backward flows
    private ArrayList<FlowField> backwardFlows;

    // The flow field singleton
    private static FlowFieldManager instance = null;

    /**
     * Get the flow field singleton
     *
     * @return singleton
     */
    public static FlowFieldManager getInstance() {
        if (instance == null) {
            instance = new FlowFieldManager();
        }
        return instance;
    }

    /**
     * Get the number of rows in / height of a flow field.
     *
     * @return row count
     */
    public int m() {
        return forwardFlows.get(0).m();
    }

    /**
     * Get the number of columns in / width of a flow field.
     *
     * @return column count
     */
    public int n() {
        return forwardFlows.get(0).n();
    }

    /**
     * Construct a new singleton.
     */
    private FlowFieldManager() {
        forwardFlows = new ArrayList<FlowField>();
        backwardFlows = new ArrayList<FlowField>();
    }

    /**
     * Append a forward flow field to the internal local variances list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param flow forward flow field to be added
     */
    public void addForwardFlow(FlowField flow) {
        forwardFlows.add(flow);
    }

    /**
     * Get the forward flow field that maps to a given dataset frame.
     *
     * Assumption: The index in the internal list of flow variances corresponds to the frame index
     * the image belongs to.
     *
     * @param frame_idx target dataset frame index.
     * @return the forward flow field that belongs to the given frame index.
     */
    public FlowField getForwardFlow(int frame_idx) {
        return forwardFlows.get(frame_idx);
    }

    /**
     * Append a backward flow field to the internal local variances list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param flow backward flow field to be added
     */
    public void addBackwardFlow(FlowField flow) {
        backwardFlows.add(flow);
    }

    /**
     * Get the backward flow field that maps to a given dataset frame.
     *
     * Assumption: The index in the internal list of flow variances corresponds to the frame index
     * the image belongs to.
     *
     * @param frame_idx target dataset frame index.
     * @return the backward flow field that belongs to the given frame index.
     */
    public FlowField getBackwardFlow(int frame_idx) {
        return backwardFlows.get(frame_idx);
    }

    /**
     * Realses the flow field collection references.
     */
    public void releaseFlows() {
        forwardFlows = null;
        backwardFlows = null;
    }

    /**
     * Releases the singleton and its held references.
     */
    public static void release() {
        if (instance != null) instance.releaseFlows();
        instance = null;
        System.gc();
    }
}

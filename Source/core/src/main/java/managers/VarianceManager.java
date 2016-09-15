package managers;

import datastructures.FlowVarField;
import java.util.ArrayList;

/**
 * VarianceManager allows us to access the flow variance field that belong to the dataset frames.
 *
 * In fact, this manager contains the local flow variance fields and the global variance values.
 * Each local variance field is the same size as the resolution of a dataset image. In contrary,
 * for every dataset frame, there is only one global variance value present.
 *
 * It is used to normalize the motion distances, used within all similarity tasks to compute the
 * affinity matrix of interest. It handles the noise that is present in the flow fields.
 *
 * Large motion distances that have a large motion variance are penalized.
 *
 * These variances are computed by applying a bilateral filter on the forward flow field.
 *
 * Assumption: The index in the internal list of flow variances corresponds to the frame index
 * the image belongs to.
 */
public class VarianceManager {

    // The flow variance singleton
    private static VarianceManager instance = null;

    // Internal collection of all local flow variance fields
    private ArrayList<FlowVarField> localVariances;

    // Internal collection of all global variance values.
    private ArrayList<Double> globalVariances;

    /**
     * Get the flow variance singleton
     *
     * @return singleton
     */
    public static VarianceManager getInstance() {
        if (instance == null) {
            instance = new VarianceManager();
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
    private VarianceManager() {
        localVariances = new ArrayList<>();
        globalVariances = new ArrayList<>();
    }

    /**
     * Get the local flow variance field that maps to a given dataset frame.
     *
     * Assumption: The index in the internal list of flow variances corresponds to the frame index
     * the image belongs to.
     *
     * @param frame_idx target dataset frame index.
     * @return the local flow variance field that belongs to the given frame index.
     */
    public FlowVarField getVariance(int frame_idx) {
        return localVariances.get(frame_idx);
    }

    /**
     * Append a local flow variance field to the internal local variances list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param flowVarField local variance field to be added
     */
    public void add(FlowVarField flowVarField) {
        localVariances.add(flowVarField);
    }

    /**
     * Append a global variance value to the internal global variances list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param value local variance field to be added
     */
    public void addGlobalVariance(double value) {
        globalVariances.add(value);
    }

    /**
     * Get the global flow variance field that maps to a given dataset frame.
     *
     * Assumption: The index in the internal list of flow variances corresponds to the frame index
     * the image belongs to.
     *
     * @param frame_idx target dataset frame index.
     * @return the global flow variance field that belongs to the given frame index.
     */
    public double getGlobalVarianceValue(int frame_idx) {
        return globalVariances.get(frame_idx);
    }
}

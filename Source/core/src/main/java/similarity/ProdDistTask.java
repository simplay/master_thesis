package similarity;

import datastructures.Point2d;
import datastructures.Trajectory;
import pipeline_components.ArgParser;

import java.util.Collection;

/**
 * ProdDistTask is a SimilarityTask to makes use of the motion- and spatial distances between trajectories.
 * Only overlapping trajectory parts (their associated tracking points) are considered.
 *
 * The actual affinity value between two trajectories is computed by
 * calculating the product of the two distance values and then taking a negative weighted exponential
 * of this resulting product.
 *
 * The motion distance is the max. motion distance between the overlapping trajectory parts, whereas
 * the motion distance is the difference of the two flow forward differences.
 *
 * The spatial distance is the average spatial distance between the overlapping trajectory parts.
 *
 * Given trajectories a, b
 * Foreach frame : overlapping_frames(a, b)
 *  pa = a.getPointAt(frame.index)
 *  pb = b.getPointAt(frame.index)
 *  d_sp += sp_dist(pa, pb)
 *  d_motion = max d(1/sigma_t(k) * ||d_t(pa) - d_t(pb)||^2)
 * exp(-LAMBDA * d_sp * d_motion)
 */
public class ProdDistTask extends SimilarityTask {

    // Affinity scaling factor:
    // The higher lambda gets, the smaller the affinities become (and vice-versa)
    // Acts as a threshold-ing affinity value equalizer.
    protected double lambda_scale;

    /**
     * Construct a new product distance similarity task.
     *
     * @param a reference trajectory
     * @param trajectories collection of all extracted trajectories.
     */
    public ProdDistTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
        this.lambda_scale = ArgParser.getLambda();
    }

    /**
     * Compute the similarity value between two trajectories using product distances.
     *
     * Info: Eigen-similarities yield a zero affinity.
     *
     * @param a trajectory
     * @param b trajectory
     * @return computed similarity value
     */
    @Override
    protected double similarityBetween(Trajectory a, Trajectory b) {

        // handle eigen-similarity case: similarity value between itself is 0
        if (a == b) {
            return EIGENSIMILARITY_VALUE;
        }

        // Compute overlapping frame indices
        int from_idx = getLowerFrameIndexBetween(a, b);
        int to_idx = getUpperFrameIndexBetween(a, b);

        return spatialTemporalDistances(a, b, from_idx, to_idx);
    }

    /**
     * Computes the similarity between two given trajectories.
     *
     * The similarity value is computed by taking a weighted,
     * negative exponential (using the lambda value) of the trajectory distance.
     *
     * The trajectory distance is equal to the product of
     * the avg. spatial distance and the max. motion distance
     * between the overlapping tracking points of the two given trajectories.
     *
     * In case the the from_idx and to_index do not overlap,
     * or too few points overlap, then the similarity value zero is returned.
     *
     * Note that the step size influences the actual number of used overlapping parts.
     * Given a step size of dt and an upper index u, then, we only iterate till and with
     * the index value (u-dt). The reason for this is because we otherwise exceed the
     * upper possible index while computing the forward difference (because this method
     * makes use of the assigned step size).
     *
     * This method also assigns the spatial avg dist. value to the involved trajectories.
     *
     * @param a trajectory
     * @param b trajectory
     * @param from_idx lower overlapping index
     * @param to_idx upper overlapping index
     * @return computed affinity value between the two given trajectories.
     */
    protected double spatialTemporalDistances(Trajectory a, Trajectory b, int from_idx, int to_idx) {

        // the number of overlapping frames.
        int commonFrameCount = overlappingFrameCount(from_idx, to_idx);

        // Checks if the number of the overlapping segments is long enough:
        // This is true if we either have more common frames than the assigned
        // min. expected overlapping frames count or the trajectories are only
        // overlapping due to a continuation (can only occur when setting ct = 1).
        if (isTooShortOverlapping(a, b, commonFrameCount)) {
            return 0;
        }

        // Compute the time step that will be for computing the forward differences between tracking points.
        // Info: The time step value is at most equal to MIN_TIMESTEP_SIZE
        int timestep = timestepSize(commonFrameCount);

        // The allowed upper frame index is affected by the time-step dt size,
        // The reason for this is that we compute forward differences, and therefore need
        // to access the frame t and t+dt. Therefore such a index shift, by dt, is necessary.
        int maxAllowedUpperFrameIdx = to_idx - timestep;

        // guard: in case there is no overlapping segment, skip computations
        if (maxAllowedUpperFrameIdx < from_idx || timestep == 0) {
            return 0;
        }

        double maxMotionDistance = 0;
        double avgSpatialDist = 0;
        double len = maxAllowedUpperFrameIdx - from_idx + 1;

        // Iterate over all overlapping frames
        for (int frameIdx = from_idx; frameIdx <= maxAllowedUpperFrameIdx; frameIdx++) {

            // Fetch the tracking point living in the current frame.
            Point2d pa = a.getPointAtFrame(frameIdx);
            Point2d pb = b.getPointAtFrame(frameIdx);

            // Points are invalid if they have not valid depth data associated
            // This can only happen, when making use of depth cues, such as in
            // the PED or the PEAD task.
            if (trajectoryPointsInvalid(pa, pb)) {
                len--;
                continue;
            }

            // update spatial distance
            avgSpatialDist += spatialDistBetween(a, b, frameIdx);

            // update max. motion distance
            double dist = d_motion(a, b, timestep, frameIdx);
            if (dist > maxMotionDistance) {
                maxMotionDistance = dist;
            }
        }

        // this check is required when using depth cues:
        // the points of overlapping trajectories may be all invalid,
        // resulting in len == 0, which would result in a NaN similarity assignment.
        if (len == 0) return 0d;

        // Normalize distance by length and assign the value to the trajectories.
        avgSpatialDist = avgSpatialDist / len;
        appendAvgSpatialDistances(a, b, avgSpatialDist);

        // Compute the affinity value
        double dist_st_a_b = avgSpatialDist * maxMotionDistance;
        double w_ab = Math.exp(-lambda_scale * dist_st_a_b);

        // Return the zero similarity, in case the spatial tol. has been reached.
        if (exceededSpatialTol(avgSpatialDist)) return 0d;

        // If the similarity is sufficiently small, return zero.
        return (w_ab < ZERO_THRESHOLD) ? 0d : w_ab;
    }

    /**
     * Check to determine whether the computed distance value can be considered as invalid
     *
     *  @param dist calculated avg spatial distance
     * @return true if invalid, false otherwise.
     */
    protected boolean exceededSpatialTol(double dist) {
        return false;
    }

    /**
     * Compute the normalized motion distance between two trajectory points having a certain frame distance.
     *
     * Computes the motion difference between to given trajectories at a given frame.
     * This difference is then normalized by the motion variance.
     *
     * @param a trajectory
     * @param b trajectory
     * @param timestep sum with current frame yields the to-frame.
     * @param frame_idx starting frame index
     * @return the motion distance normalized by the flow variance.
     */
    protected double d_motion(Trajectory a, Trajectory b, int timestep, int frame_idx) {
        Point2d dt_a = forward_difference(a, timestep, frame_idx);
        Point2d dt_b = forward_difference(b, timestep, frame_idx);

        double dt_ab = dt_a.sub(dt_b).length_squared();
        double sigma_t = EPS_FLOW + getVariance(frame_idx, a, b);

        return dt_ab / sigma_t;
    }

}

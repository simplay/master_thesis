package similarity;

import datastructures.Point2d;
import datastructures.Point3d;
import datastructures.Trajectory;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.VarianceManager;
import pipeline_components.ArgParser;
import java.util.Collection;

public abstract class SimilarityTask implements Runnable {

    // minimal expected timestep size
    // trajectory overlaps shorter than that value will be ignored
    protected int MIN_TIMESTEP_SIZE = 4;

    // The similarity value when comparing the trajectory with itself.
    // Setting this to 0 makes cluster consisting of 1 pixel vanish.
    protected final double EIGENSIMILARITY_VALUE = 0d;

    // minimal expected trajectory length
    protected static int MIN_EXPECTED_TRAJ_LEN = 3;

    // Minimal assigned flow variance value used within
    // the normalization step,
    protected final double EPS_FLOW = 1d;

    // Thresholds the zero value to truncate too small affinity towards zero.
    protected final double ZERO_THRESHOLD = 1e-12;

    // A list of all existing trajectories
    private final Collection<Trajectory> trajectories;

    // the trajectory that belongs to this current task
    protected Trajectory a;

    /**
     *
     * @param a
     * @param trajectories
     */
    public SimilarityTask(Trajectory a, Collection<Trajectory> trajectories) {
        this.a = a;
        this.trajectories = trajectories;
    }

    public static int minExpectedTrajectoryLength() {
        return MIN_EXPECTED_TRAJ_LEN;
    }

    protected abstract double similarityBetween(Trajectory a, Trajectory b);

    /**
     * Get index of the first frame where the tracking points of two trajectories start overlapping,
     *
     * i.e. Get the upper frame index, where a trajectory starts, between two given trajectories.
     * This gives the start frame index of a potential overlapping pair.
     *
     * @param a trajectory
     * @param b trajectory
     * @return frame index the two given trajectory could overlap for the first time.
     */
    protected int getLowerFrameIndexBetween(Trajectory a, Trajectory b) {
        if (!ArgParser.shouldContinueTrajectories()) {
            return Math.max(a.getStartFrame(), b.getStartFrame());
        }
        return a.getOverlappingStartFrameIndexBetween(b);
    }

    /**
     * Get the end frame index of a trajectory pair,
     * i.e. the lower frame index, where a trajectory ends, between two given trajectories.
     * This gives the end frame index of a potential overlapping pair.
     *
     * @param a
     * @param b
     * @return
     */
    protected int getUpperFrameIndexBetween(Trajectory a, Trajectory b) {
        if (!ArgParser.shouldContinueTrajectories()) {
            return Math.min(a.getEndFrame(), b.getEndFrame());
        }
        return a.getOverlappingEndFrameIndexBetween(b);
    }

    /**
     * Computes the number of overlapping frames, given two frame indices.
     *
     * @param from_idx start frame index
     * @param to_idx end frame index
     * @return number of overalpping frames.
     */
    protected int overlappingFrameCount(int from_idx, int to_idx) {
        return to_idx - from_idx + 1;
    }

    /**
     * Returns the timestep size that can be used for the forward difference scheme.
     *
     * @param common_frame_count number of overlapping frames of two given trajectories.
     * @return the timestep size (number of frames) that can be used for the forward difference method.
     */
    protected int timestepSize(int common_frame_count) {
        return Math.min(common_frame_count, MIN_TIMESTEP_SIZE);
    }

    /**
     * Checks whether the overlapping trajectory parts, given their overlapping frame count,
     * is too short according to the minimum expected trajectory length.
     *
     * This method is used to skip the affinity computation for too short overlapping segments.
     *
     * Note: The number of frames - 1 gives us the length of a trajectory
     *
     * Never use this method directly within a similarity task
     *
     * @param overlappingFrameCount the number of overlapping trajectory frames.
     * @return true, if the overlap length is too short, false otherwise.
     */
    protected boolean isTooShortOverlapping(int overlappingFrameCount) {
        return (overlappingFrameCount - 1) < MIN_EXPECTED_TRAJ_LEN;
    }

    /**
     * Checks whether the overlapping trajectory parts are too short.
     *
     * An overlap is either too short if it has no overlap at all,
     * is overlapping (without continuation), but the the overlap length
     * or is smaller than the expected minimum overlap length.
     *
     * In case we we consider a overlapping which only exists because of a
     * continuation of the trajectories (i.e. the trajectories would not have an
     * overlap, if they were not continued), then we consider them never as being too short.
     *
     * Too short trajectory overlaps result in assigning a similarity value equals zero
     * between their involved trajectories.
     *
     * Note that the given overlapping frame count refers to
     * the number of overlapping frames without considering a continuation (if ct = 0)
     * the number of overlapping frames, including continued tracking points (if ct = 1).
     *
     * In the following a list of all possible use-cases:
     *
     * ct = 0, no overlap
     * then overlappingFrameCount == 0 && hasOnlyOverlapInCont == false, hence
     * only the check whether the min expected overlap length is fulfilled is performed.
     * Since no overlap given, isTooShortOverlapping(overlappingFrameCount) yields true
     *
     * ct = 0, has overlap
     * then hasOverlapWithoutContinuation is true, hence hasOnlyOverlapInCont is false, hence
     * only the check isTooShortOverlapping(overlappingFrameCount() is performed
     *
     * ct = 1, no overlap (with cont)
     * then overlappingFrameCount > 0 is false and since no overlap in cont, we also have
     * hasOverlapWithoutContinuation(a, b) equals false (if not in cont. overlap, then also
     * no overlap without cont) => !hasOverlapWithoutContinuation(a, b) is true. This results
     * in hasOnlyOverlapInCont is false. Since !hasOnlyOverlapInCont is therefore true,
     * we only consider isTooShortOverlapping(overlappingFrameCount)
     * which is true (since no overlap, thus too short)
     *
     * ct = 1, overlap (with cont)
     * overlappingFrameCount > 0 => true
     * if hasOverlapWithoutContinuation(a, b) == true
     * then hasOnlyOverlapInCont == false
     * therefore we perform the isTooShortOverlapping(overlappingFrameCount) check
     *
     * if hasOverlapWithoutContinuation(a, b) == false
     * (there is only an overlap in the cont. case),
     * then !hasOverlapWithoutContinuation(a, b) == true and
     * hence hasOnlyOverlapInCont is true.
     * Since we use !hasOnlyOverlapInCont and perform an AND operation,
     * the check is too short overlapping returns false.
     * (Desired behaviour, when there is only an overlap due to expanding, then skip the
     * the too short check).
     *
     * @param a trajectory
     * @param b trajectory
     * @param overlappingFrameCount the overlapping frame count.
     * @return true if the given trajectory overlap is too short, false otherwise.
     */
    protected boolean isTooShortOverlapping(Trajectory a, Trajectory b, int overlappingFrameCount) {
        boolean hasOnlyOverlapInCont = !hasOverlapWithoutContinuation(a, b) && overlappingFrameCount > 0;
        return isTooShortOverlapping(overlappingFrameCount) && !hasOnlyOverlapInCont;
    }

    /**
     * Checks whether a given pair of trajectories overlap,
     * when not using their continuation tracking points.
     *
     * When not setting `ct = 1` then, this behaves like an ordinary
     * overlapping check. Otherwise, it checks if there already was an
     * overlap between two trajectories without considering their continuation.
     *
     * This is used to filter overlaps, that only exists due to trajectory expansions.
     * In this case we do not want to apply the isTooShortOverlapping check.
     *
     * Do not attempt to directly use this method within a similarity task,
     * rather use the isTooShortOverlapping check, which actually makes use of this method.
     *
     * @param a trajectory
     * @param b trajectory
     * @return true, if trajectory does not overlap, given the tracking points
     *  without their continuations.
     */
    protected boolean hasOverlapWithoutContinuation(Trajectory a, Trajectory b) {
        int noContStartA = a.startFrameWithoutLeftAdditions();
        int noContStartB = b.startFrameWithoutLeftAdditions();

        int noContEndA = a.endFrameWithoutRightAdditions();
        int noContEndB = b.endFrameWithoutRightAdditions();

        int noContStartIdx = Math.max(noContStartA, noContStartB);
        int noContEndIdx = Math.min(noContEndA, noContEndB);

        int commonFrameCountWithoutAdd = (noContEndIdx - noContStartIdx) + 1;
        if (commonFrameCountWithoutAdd <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Computes the tangent at a given trajectory point, using a certain time-step.
     *
     * To compute the tangent, we make use of a forward difference scheme,
     * allowing arbitrary time-steps. The
     *
     * Given a time-step h, then any function f can be approximated by the Taylor series
     * f(x+h) = f(x) + h*f'(x) + O(h^2)
     * => [f(x+h) - f(x)] / h = f'(x)
     *
     * This method is used to compute motion distances between trajectories.
     *
     * @param tra trajectory
     * @param dt time step size
     * @param frame_idx index of tracking point the tangent is orthogonal to.
     * @return forward difference of the trajectory active at the target
     *  frame when using the given step size.
     */
    protected Point2d forward_difference(Trajectory tra, int dt, int frame_idx) {
        Point2d p_i = tra.getPointAtFrame(frame_idx);
        Point2d p_i_pl_t = tra.getPointAtFrame(frame_idx + dt);
        Point2d p = p_i_pl_t.copy().sub(p_i);
        return p.div_by(dt);
    }

    /**
     * Computes the tangent in the Euclidean space on a trajectory using a certain time step.
     *
     * The tangent is computed by calculating the forward difference between two 3D points,
     * being a certain step-size apart from each other.
     *
     * Is used to compute 3D motion distance, used by `PAED` or `SAED` similarity tasks.
     *
     * @param tra trajectory
     * @param dt time step size
     * @param frame_idx index of tracking point the tangent is orthogonal to.
     * @return 3D tangent on a trajectory.
     */
    protected Point3d forward_difference3d(Trajectory tra, int dt, int frame_idx) {
        Point3d p1 = tra.getEuclidPositionAtFrame(frame_idx);
        Point3d p2 = tra.getEuclidPositionAtFrame(frame_idx + dt);
        Point3d p = p2.copy().sub(p1);

        // check for invalid depth values
        if (p1.z() == 0d || p2.z() == 0d) {
            p.markInvalid();
        }
        return p.div_by(dt);
    }

    /**
     * The the motion field variance value for two given trajectories at a given frame.
     * Can either be the local or the global variance value. The actual selection depends on
     * the provided user arguments.
     * The flow variance is used for normalizing the motion difference value used for
     * computing the motion distance between a trajectory pair.
     * For further details, pleasse refer to the paper:
     * `Segmentation of moving objects by long term video analysis` - T. Brox et al.
     *
     * @param frame_idx a frame index the two trajectories are overlapping.
     * @param a first trajectory.
     * @param b second trajectory
     * @return flow variance value.
     */
    protected double getVariance(int frame_idx, Trajectory a, Trajectory b) {
        return (ArgParser.useLocalVariance())
                ? localVarAt(frame_idx, a, b)
                : VarianceManager.getInstance().getGlobalVarianceValue(frame_idx);
    }

    /**
     * The local flow variance value at a given frame. These variance values are computed
     * by running the script `../initialize_data/init_data.m`. This script makes use of
     * a bilateral filter applied to the motion field. For further details read the corresponding
     * REDME.md file and the code documentation.
     *
     * @param frame_idx a frame index the two trajectories are overlapping.
     * @param a first trajectory
     * @param b second trajectory
     * @return local flow variance value between two trajectories at a given frame.
     */
    private double localVarAt(int frame_idx, Trajectory a, Trajectory b) {
        Point2d pa = a.getPointAtFrame(frame_idx);
        Point2d pb = b.getPointAtFrame(frame_idx);
        double var_a = VarianceManager.getInstance().getVariance(frame_idx).valueAt(pa);
        double var_b = VarianceManager.getInstance().getVariance(frame_idx).valueAt(pb);
        return Math.min(var_a, var_b);
    }

    /**
     * Appends the average spatial distance between two trajectories. This is used to
     * find and determine the nearest neighbors of a trajectory.
     *
     * @param a first trajectory
     * @param b second trajectory
     * @param sp_dist spatial distance value (can be either in pixel or meter units).
     */
    protected void appendAvgSpatialDistances(Trajectory a, Trajectory b, double sp_dist) {
        a.appendAvgSpatialDist(b.getLabel(), sp_dist);
        b.appendAvgSpatialDist(a.getLabel(), sp_dist);
    }

    /**
     * Compute the spatial distance between two trajectory points overlapping at a certain given frame.
     *
     * This measures gives a value for the distance between two tracking points living in the same frame.
     *
     * By default, the spatial distance is in pixel units.
     *
     * @param a Trajectory
     * @param b Trajectory
     * @param frame_idx frame index where trajectory frames are overlapping.
     * @return spatial distance of overlapping trajectory points.
     */
    protected double spatialDistBetween(Trajectory a, Trajectory b, int frame_idx) {
        Point2d pa = a.getPointAtFrame(frame_idx);
        Point2d pb = b.getPointAtFrame(frame_idx);
        return pa.copy().sub(pb).length();
    }

    /**
     * Checks whether a given pair of comparison points is valid.
     * Valid means that they have valid depth information assigned.
     *
     * @param pa tracking point living in trajectory a
     * @param pb tracking point living in trajectory b
     * @return true if they are invalid, false otherwise.
     */
    protected boolean trajectoryPointsInvalid(Point2d pa, Point2d pb) {
        return (!pa.isValid() || !pb.isValid());
    }

    @Override
    public void run() {
        for (Trajectory b : trajectories) {
            double similarityValue = similarityBetween(a, b);
            a.assignSimilarityValueTo(b.getLabel(), similarityValue);
            b.assignSimilarityValueTo(a.getLabel(), similarityValue);
        }
        ProgressBar.reportStatus();
    }
}

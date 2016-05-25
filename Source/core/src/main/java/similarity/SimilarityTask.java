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
    protected final int MIN_TIMESTEP_SIZE = 4;

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
     * The number of frames - 1 gives us the length of a trajectory
     *
     * @param overlappingFrameCount
     * @return
     */
    protected boolean isTooShortOverlapping(int overlappingFrameCount) {
        return (overlappingFrameCount - 1) < MIN_EXPECTED_TRAJ_LEN;
    }

    /**
     * Compute the value of the tangent of a given trajectory at a given location.
     * Given a time-step h, then any function f can be approximated by the Taylor series
     * f(x+h) = f(x) + h*f'(x) + O(h^2)
     * => [f(x+h) - f(x)] / h = f'(x)
     *
     * @param tra trajectory
     * @param dt time step size
     * @param frame_idx active frame
     * @return forward difference value of the trajectory active at the target
     *  frame when using the given step size.
     */
    protected Point2d forward_difference(Trajectory tra, int dt, int frame_idx) {
        Point2d p_i = tra.getPointAtFrame(frame_idx);
        Point2d p_i_pl_t = tra.getPointAtFrame(frame_idx+dt);
        Point2d p = p_i_pl_t.copy().sub(p_i);
        return p.div_by(dt);
    }

    /**
     * Compute 3d flow in metric space.
     *
     * @param tra
     * @param dt
     * @param frame_idx
     * @return
     */
    protected Point3d forward_difference3d(Trajectory tra, int dt, int frame_idx) {
        Point2d p_i = tra.getPointAtFrame(frame_idx);
        Point2d p_i_pl_t = tra.getPointAtFrame(frame_idx+dt);
        double d_i = DepthManager.getInstance().get(frame_idx).valueAt(p_i.x(), p_i.y());
        double d_i_pl_t = DepthManager.getInstance().get(frame_idx+dt).valueAt(p_i_pl_t.x(), p_i_pl_t.y());

        double _x1 = d_i*((p_i.x() - CalibrationManager.depth_principal_point().x()) / CalibrationManager.depth_focal_len().x());
        double _y1 = d_i*((p_i.y() - CalibrationManager.depth_principal_point().y()) / CalibrationManager.depth_focal_len().y());

        double _x2 = d_i_pl_t*((p_i_pl_t.x() - CalibrationManager.depth_principal_point().x()) / CalibrationManager.depth_focal_len().x());
        double _y2 = d_i_pl_t*((p_i_pl_t.y() - CalibrationManager.depth_principal_point().y()) / CalibrationManager.depth_focal_len().y());

        Point3d p1 = new Point3d(_x1, _y1, d_i);
        Point3d p2 = new Point3d(_x2, _y2, d_i_pl_t);

        Point3d p = p1.sub(p2);

        // check for invalid depth values
        if (d_i == 0d || d_i_pl_t == 0d) {
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
     * @param pa
     * @param pb
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

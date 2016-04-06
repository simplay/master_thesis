package similarity;

import datastructures.Point2d;
import datastructures.Trajectory;
import managers.VarianceManager;
import pipeline_components.ArgParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class SimilarityTask implements Runnable {

    // Affinity scaling factor, acts as a sensivity parameter
    protected final double LAMBDA = 0.1d;

    // minimal expected timestep size
    // trajectory overlaps shorter than that value will be ignored
    protected final int MIN_TIMESTEP_SIZE = 4;

    // The similarity value when comparing the trajectory with itself.
    // Setting this to 0 makes cluster consisting of 1 pixel vanish.
    protected final double EIGENSIMILARITY_VALUE = 0d;

    // minimal expected trajectory length
    protected final int MIN_EXPECTED_TRAJ_LEN = 3;

    // Minimal assigned flow variance value used within
    // the normalization step,
    protected final double EPS_FLOW = 1d;

    // Thresholds the zero value to truncate too small affinity towards zero.
    protected final double ZERO_THRESHOLD = 1e-12;


    public enum Types {
        SD(1, SumDistTask.class, true),
        PD(2, ProdDistTask.class, false);

        private int value;
        private Class targetClass;
        private boolean usesColorCues;

        private Types(int value, Class targetClass, boolean usesColorCues) {
            this.value = value;
            this.targetClass = targetClass;
            this.usesColorCues = usesColorCues;
        }

        public Class getTaskClass() {
            return targetClass;
        }

        public static Types TypeById(int id) {
            for (Types t : values() ) {
                if (t.value == id) {
                    return t;
                }
            }
            return null;
        }

        /***
         * Is the corresponding task required to use color cues?
         *
         * @return true if the tasks required color cues otherwise false.
         */
        public boolean usesColorCues() {
            return usesColorCues;
        }

    }

    public static SimilarityTask buildTask(Types taskType, Trajectory a, Collection<Trajectory> trajectories) {
        SimilarityTask task = null;
        try {
            task = (SimilarityTask) taskType.getTaskClass().getConstructor(Trajectory.class, Collection.class).newInstance(a, trajectories);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return task;
    }

    private final Collection<Trajectory> trajectories;
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

    protected abstract double similarityBetween(Trajectory a, Trajectory b);

    /**
     * Get the start frame index of a trajectory pair,
     * i.e. Get the upper frame index, where a trajectory starts, between two given trajectories.
     * This gives the start frame index of a potential overlapping pair.
     *
     * @param a
     * @param b
     * @return
     */
    protected int getLowerFrameIndexBetween(Trajectory a, Trajectory b) {
        return Math.max(a.getStartFrame(), b.getStartFrame());
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
        return Math.min(a.getEndFrame(), b.getEndFrame());
    }

    protected int overlappingFrameCount(int from_idx, int to_idx) {
        return to_idx - from_idx + 1;
    }

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
        return overlappingFrameCount-1 < MIN_EXPECTED_TRAJ_LEN;
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

    protected double getVariance(int frame_idx, Trajectory a, Trajectory b) {
        return (ArgParser.useLocalVariance())
                ? localVarAt(frame_idx, a, b)
                : VarianceManager.getInstance().getGlobalVarianceValue(frame_idx);
    }

    private double localVarAt(int frame_idx, Trajectory a, Trajectory b) {
        Point2d pa = a.getPointAtFrame(frame_idx);
        Point2d pb = b.getPointAtFrame(frame_idx);
        double var_a = VarianceManager.getInstance().getVariance(frame_idx).valueAt(pa);
        double var_b = VarianceManager.getInstance().getVariance(frame_idx).valueAt(pb);
        return Math.min(var_a, var_b);
    }

    protected void appendAvgSpatialDistances(Trajectory a, Trajectory b, double sp_dist) {
        a.appendAvgSpatialDist(b.getLabel(), sp_dist);
        b.appendAvgSpatialDist(a.getLabel(), sp_dist);
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
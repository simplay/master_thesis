package similarity;

import datastructures.Point2d;
import datastructures.Point3d;
import datastructures.Trajectory;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.VarianceManager;
import pipeline_components.ArgParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class SimilarityTask implements Runnable {

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
        SD(1, SumDistTask.class, true, false, "Sum of Distances", SumDistEuclidTask.class),
        PD(2, ProdDistTask.class, false, false, "Product of Distances", ProdDistEuclidTask.class),
        PED(3, ProdDistEuclidTask.class, false, false, "Product of Euclidian Distances"),
        SED(4, SumDistEuclidTask.class, true, false, "Sum of Euclidian Distances"),
        PAED(5, ProdDistAllEuclidTask.class, false, true, "Product of Distances all 3d"),
        PAENDD(6, ProdDistAllEuclidNoDepthVarTask.class, false, false, "Product of Distances all 3d without depth variance");

        private int value;
        private Class targetClass;
        private boolean usesColorCues;
        private boolean usesDepthVar;
        private String taskName;
        private Class alternativeTaskClass;

        private Types(int value, Class targetClass, boolean usesColorCues, boolean usesDepthVar, String taskName, Class alternativTaskClass) {
            this.value = value;
            this.targetClass = targetClass;
            this.usesColorCues = usesColorCues;
            this.usesDepthVar = usesDepthVar;
            this.taskName = taskName;
            this.alternativeTaskClass = alternativTaskClass;
        }

        private Types(int value, Class targetClass, boolean usesColorCues, boolean usesDepthVar, String taskName) {
            this(value, targetClass, usesColorCues, usesDepthVar, taskName, null);
        }

        public String getName() {
            return taskName;
        }

        /**
         * Checks whether the alternative task should be used.
         * This is the case whenever depth cues should be used but the color and depth camera are overlapping
         * and we are not already an alternative task.
         *
         * @return true if the alternative task should be used otherwise false.
         */
        public boolean shouldUseAlternativeTask() {
            if (alternativeTaskClass == null) return false;
            return CalibrationManager.hasNoIntrinsicDepthProjection() && ArgParser.useDepthCues();
        }

        public Class getAlternativeTaskClass() {
            return alternativeTaskClass;
        }

        public boolean usesDepthVariance() {
            return usesDepthVar;
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
            Class type = taskType.getTaskClass();
            if (taskType.shouldUseAlternativeTask()) {
                type = taskType.getAlternativeTaskClass();
            }
            task = (SimilarityTask) type.getConstructor(Trajectory.class, Collection.class).newInstance(a, trajectories);
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
     * Compute the spatial distance between two trajectory points overlapping at a certain given frame.
     *
     * @param a Trajectory
     * @param b Trajectory
     * @param frame_idx frame index where trajectory frames are overlapping.
     * @return spatial distance of overlapping trajectory points.
     */
    protected double spatialDistBetween(Trajectory a, Trajectory b, int frame_idx) {
        Point2d pa = a.getSpatialPointAtFrame(frame_idx);
        Point2d pb = b.getSpatialPointAtFrame(frame_idx);
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

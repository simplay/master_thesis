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


    public enum Types {
        SD(1, SumDistTask.class),
        MD(2, SumDistTask.class);

        private int value;
        private Class targetClass;

        private Types(int value, Class targetClass) {
            this.value = value;
            this.targetClass = targetClass;
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

    }

    static SimilarityTask buildTask(Types taskType, Trajectory a, Collection<Trajectory> trajectories) {
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
        return Math.min(common_frame_count, MIN_TIMESTEP_SIZE) - 1;
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
     *
     * @param tra
     * @param dt
     * @param frame_idx
     * @return
     */
    protected Point2f forward_difference(Trajectory tra, int dt, int frame_idx) {
        Point2f p_i = tra.getPointAtFrame(frame_idx);
        Point2f p_i_pl_t = tra.getPointAtFrame(frame_idx+dt);
        Point2f p = p_i_pl_t.copy().sub(p_i);
        return p.div_by(dt + 1);
    }

    @Override
    public void run() {
        for (Trajectory b : trajectories) {
            double similarityValue = similarityBetween(a, b);
            a.assignSimilarityValueTo(b.getLabel(), similarityValue);
            b.assignSimilarityValueTo(a.getLabel(), similarityValue);
        }
    }
}

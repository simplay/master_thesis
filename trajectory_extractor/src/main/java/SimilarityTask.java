import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class SimilarityTask implements Runnable {

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

    @Override
    public void run() {
        for (Trajectory b : trajectories) {
            double similarityValue = similarityBetween(a, b);
            a.assignSimilarityValueTo(b.getLabel(), similarityValue);
            b.assignSimilarityValueTo(a.getLabel(), similarityValue);
        }
    }
}

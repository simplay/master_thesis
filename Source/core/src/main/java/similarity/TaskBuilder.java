package similarity;

import datastructures.Trajectory;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class TaskBuilder {

    private static TaskBuilder instance = null;

    public static TaskBuilder getInstance() {
        if (instance == null) {
            instance = new TaskBuilder();
        }
        return instance;
    }

    public static SimilarityTask buildTask(SimilarityTaskType taskType, Trajectory a, Collection<Trajectory> trajectories) {
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
}

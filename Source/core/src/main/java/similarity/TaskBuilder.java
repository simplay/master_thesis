package similarity;

import datastructures.Trajectory;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * TaskBuilder is responsible for building an similarity task, consumed by the the similarity task
 * worker threads. It makes use of the provided user input and the extracted trajectories and builds
 * an appropriate similarity task.
 */
public class TaskBuilder {

    /**
     * Build a similarity task.
     *
     * @param taskType type of similarity task we want to run.
     * @param a reference trajectory
     * @param trajectories all valid trajectories
     * @return an appropriate similarity task that can be processed by the worker threads.
     */
    public static SimilarityTask buildTask(SimilarityTaskType taskType, Trajectory a, Collection<Trajectory> trajectories) {
        SimilarityTask task = null;
        try {
            Class type = taskType.getTaskClass();
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

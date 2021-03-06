package pipeline_components;

import datastructures.Trajectory;
import managers.TrajectoryManager;
import similarity.SimilarityTask;
import similarity.SimilarityTaskType;
import similarity.TaskBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * AffinityCalculator prepares the similarity workers and their tasks and then runs them
 * which results in computing the affinity matrix.
 *
 * Note that the whole pipeline is blocking, until the last job has been
 * successfully processed by a worker.
 */
public class AffinityCalculator {

    /**
     * Generate the affinity matrix. Is invoked in the Main class.
     */
    public AffinityCalculator() {

        // performance improvement: pre-allocate all involved datastructures.
        TrajectoryManager.prepareForSimilarityCompuation();

        // determine which task should be used for the computation
        SimilarityTaskType taskType = ArgParser.getSimTask();
        ArrayList<SimilarityTask> tasks = new ArrayList<>();

        String taskName = taskType.name();
        Logger.println("Running similarity task: " + taskName);

        int from_idx = 0;
        // assign upper triangular matrix
        // fetch all triangular matrix rows and build a similarity task
        for (Trajectory a : TrajectoryManager.getTrajectories()) {
            Collection<Trajectory> trajectories = TrajectoryManager.getTrajectorySubset(from_idx);
            SimilarityTask task = TaskBuilder.buildTask(taskType, a, trajectories);
            tasks.add(task);
            from_idx++;
        }

        int numberOfAvailableThreads = Runtime.getRuntime().availableProcessors();
        Logger.println("=> Using " + numberOfAvailableThreads + " threads.");
        ExecutorService executor = Executors.newFixedThreadPool(numberOfAvailableThreads);
        for (SimilarityTask task : tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}

        Logger.println("Computed trajectory affinities...");
    }
}
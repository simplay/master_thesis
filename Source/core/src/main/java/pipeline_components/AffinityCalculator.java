package pipeline_components;

import datastructures.Trajectory;
import managers.CalibrationManager;
import managers.TrajectoryManager;
import similarity.SimilarityTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AffinityCalculator {

    public AffinityCalculator() {

        // performance improvement: pre-allocate all involved datastructures.
        TrajectoryManager.prepareForSimilarityCompuation();

        // determine which task should be used for the computation
        SimilarityTask.Types taskType = ArgParser.getSimTask();
        ArrayList<SimilarityTask> tasks = new ArrayList<>();

        String taskName = taskType.name();
        if (taskType.shouldUseAlternativeTask()) {
            System.err.println("No extrinsic transformation provided. Changed similarity task to its alternative...");
            taskName = taskType.getAlternativeTaskClass().getName();
        }

        System.out.println("Running similarity task: " + taskName);

        int from_idx = 0;
        // assign upper triangular matrix
        for (Trajectory a : TrajectoryManager.getTrajectories()) {
            Collection<Trajectory> trajectories = TrajectoryManager.getTrajectorySubset(from_idx);
            SimilarityTask task = SimilarityTask.buildTask(taskType, a, trajectories);
            tasks.add(task);
            from_idx++;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (SimilarityTask task : tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}

        System.out.println("Computed trajectory affinities...");
    }
}
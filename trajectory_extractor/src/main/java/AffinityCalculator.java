import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AffinityCalculator {

    public AffinityCalculator() {

        SimilarityTask.Types taskType = ArgParser.getSimTask();
        ArrayList<SimilarityTask> tasks = new ArrayList<>();

        for (Trajectory a : TrajectoryManager.getTrajectories()) {
            SimilarityTask task = SimilarityTask.buildTask(taskType, a);
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (SimilarityTask task : tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}

        System.out.println("foobar");

    }
}

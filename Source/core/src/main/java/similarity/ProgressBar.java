package similarity;

import managers.TrajectoryManager;
import pipeline_components.Logger;

/**
 * ProgressBar is a shared resource among similarity threads used to update their progress
 * regarding their tasks progress.
 *
 * A progress bar basically keeps track of the number of finished tasks and the number of
 * total tasks. Every 200th time of progress update, the progress bar reports
 * the fraction of finished and total tasks by invoking the logger.
 */
public class ProgressBar {

    // The progress bar singleton
    private static ProgressBar instance = null;

    // The total number of extracted trajectories
    private double totalTaskCount;

    // The current number of finished tasks
    private int finishedTaskCount = 0;

    /**
     * Obtain the progress bar singleton
     *
     * @return singleton
     */
    public static ProgressBar getInstance() {
        if (instance == null) {
            instance = new ProgressBar();
        }
        return instance;
    }

    /**
     * Releases the internal singleton instance.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Creates a new progress bar singleton and assigns the total number of tasks.
     */
    private ProgressBar() {
        this.totalTaskCount = TrajectoryManager.getTrajectories().size();
    }

    /**
     * Increments the internal count of finished tasks.
     *
     * Every 200th call, the current progress percentage is reported.
     */
    public synchronized void incState() {
        finishedTaskCount++;
        if (finishedTaskCount % 200 == 0) {
            double progressPercentage = (finishedTaskCount / totalTaskCount) * 100d;
            Logger.println("+ Progress: " + progressPercentage + "%");
        }
    }

    /**
     * Tries to report the current progress.
     * Only every 200th attempt of status reporting will print the current progress.
     * This saves the console from being flooded.
     */
    public static void reportStatus() {
        getInstance().incState();
    }

}

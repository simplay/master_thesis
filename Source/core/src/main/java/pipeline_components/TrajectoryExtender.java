package pipeline_components;

import datastructures.Trajectory;
import managers.TrajectoryManager;

/**
 * TrajectoryExtender appends and prepends estimated tracking points
 * to all trajectories.
 */
public class TrajectoryExtender {

    /**
     *
     */
    public TrajectoryExtender() {
       for (Trajectory tra : TrajectoryManager.getTrajectories()) {

       }
    }
}

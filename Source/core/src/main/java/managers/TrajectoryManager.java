package managers;

import datastructures.Point2d;
import datastructures.Trajectory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Singleton containing and managing all existing trajectories.
 */
public class TrajectoryManager implements Iterable<Trajectory>{

    // A hash containing all the trajectories, with the trajectory label as key.
    private HashMap<Integer, Trajectory> trajectories;

    // Singleton state
    private static TrajectoryManager instance = null;

    public static TrajectoryManager getInstance() {
        if (instance == null) {
            instance = new TrajectoryManager();
        }
        return instance;
    }

    private TrajectoryManager() {
        trajectories = new HashMap<Integer, Trajectory>();
    }

    public static Collection<Trajectory> getTrajectories() {
        return instance.trajectories.values();
    }

    public static List<Trajectory> getTrajectorySubset(int from_idx) {
        int n = getTrajectories().size();
        ArrayList<Trajectory> collection = new ArrayList<>(getTrajectories());
        return collection.subList(from_idx, n-1);
    }

    /**
     * Number of trajectories this Manager contains.
     *
     * @return
     */
    public int trajectoryCount() {
        return trajectories.size();
    }

    /**
     * Start a new trajectory with a given starting point at a given starting frame.
     *
     * @param p starting point the new trajectory is supposed to start
     * @param startingFrame frame index where the new trajectory will start at.
     */
    public void startNewTrajectoryAt(Point2d p, int startingFrame) {
        Trajectory tra = new Trajectory(startingFrame);
        tra.addPoint(p);
        trajectories.put(tra.getLabel(), tra);
    }

    /**
     * Append a new tracking point to target trajectory.
     *
     * @param trajectory_label trajectory identifier referencing the target trajectory.
     * @param p point to be added to the target trajectory
     */
    public void appendPointTo(int trajectory_label, Point2d p) {
        Trajectory tra = trajectories.get(trajectory_label);
        tra.addPoint(p);
    }

    public Iterator<Trajectory> iterator() {
        return trajectories.values().iterator();
    }

    /**
     * Select all Trajectories that are currently active in a given frame.
     *
     * @param frame_idx active frame index
     * @return selected active trajectories.
     */
    public LinkedList<Trajectory> getActivesForFrame(int frame_idx) {
        LinkedList<Trajectory> actives = new LinkedList<Trajectory>();
        for (Trajectory tra : trajectories.values()) {
            if ((tra.currentActiveFrame() == frame_idx) && tra.notClosed() ) {
                actives.add(tra);
            }
        }
        return actives;
    }

    /**
     * All in advance required steps to start the similarity computation.
     */
    public static void prepareForSimilarityCompuation() {
        for (Trajectory tra : getTrajectories()) {
            tra.initSimilarityDatastructures();
        }
    }

    /**
     * Select all trajectories that have a given length value.
     *
     * @param length expected trajectory length value.
     * @return Collection of trajectories all having an expected length value.
     */
    public LinkedList<Trajectory> allTrajectoryWithLength(int length) {
        LinkedList<Trajectory> actives = new LinkedList<Trajectory>();
        for (Trajectory tra : trajectories.values()) {
            if (tra.length() == length) {
                actives.add(tra);
            }
        }
        return actives;
    }

    /**
     * Select all trajectories that start at a given frame.
     *
     * @param frame_idx index of frame the trajectories should start.
     * @return a collection of trajectories all starting at a given frame.
     */
    public LinkedList<Trajectory> allTrajectoriesStartingAt(int frame_idx) {
        LinkedList<Trajectory> actives = new LinkedList<Trajectory>();
        for (Trajectory tra : trajectories.values()) {
            if (tra.getStartFrame() == frame_idx) {
                actives.add(tra);
            }
        }
        return actives;
    }
    /**
     * Select all trajectories that start at a given frame.
     *
     * @param frame_idx index of frame the trajectories should start.
     * @return a collection of trajectories all starting at a given frame.
     */
    public LinkedList<Trajectory> allTrajectoriesActiveInGivenFrame(int frame_idx) {
        LinkedList<Trajectory> actives = new LinkedList<Trajectory>();
        for (Trajectory tra : trajectories.values()) {
            if (tra.livesInFrame(frame_idx)) {
                actives.add(tra);
            }
        }
        return actives;
    }

    /**
     * Filter all trajectories from the internal list of trajectories,
     * that have length zero, i.e. that consists of just one point.
     */
    public void filterOnePointedTrajectories() {
        for (Trajectory traj : allTrajectoryWithLength(0)) {
            trajectories.remove(traj.getLabel());
        }
    }

    /**
     * Filter all trajectories shorter than a given min length.
     *
     * @param len expected minimum length
     */
    public void filterTrajectoriesShorterThanMinLen(int len) {
        for (int k = 0; k < len; k++) {
            for (Trajectory traj : allTrajectoryWithLength(k)) {
                trajectories.remove(traj.getLabel());
            }
        }
    }

    /**
     * Filters every trajectory from the set of all extracted trajectory
     * that do not exhibit any similarity values, referring to those trajectories,
     * that either only include zeros or no similarity entries at all.
     */
    public void filterNoSimilarityTrajectories() {
        for (Trajectory traj : getTrajectories()) {
            if (!traj.hasSimilarityValues()) {
                for (Trajectory other : getTrajectories()) {
                    other.filterSimilarityOfTrajectory(traj.getLabel());
                }
                traj.markAsDeletable();
            }
        }
        filterDeletableTrajectories();
    }

    /**
     * Filter all deletable trajectories:
     * Note that we are forced to make a copy of the old trajectories
     * since we otherwise would end up having a runtime error since the hash map
     * references to null entries during iteration since we
     * perform deletion operations of trajectories on-the-fly.
     */
    public void filterDeletableTrajectories() {
        LinkedList<Trajectory> c_tras = new LinkedList<>(trajectories.values());
        for (Trajectory tra : c_tras) {
            if (tra.isDeletable()) {
                trajectories.remove(tra.getLabel());
            }
        }
    }

    public String toFramewiseOutputString(int frame_idx) {
        String content = "";
        LinkedList<Trajectory> trajectoriesAtGivenFrame = allTrajectoriesActiveInGivenFrame(frame_idx);
        Collections.sort(allTrajectoriesStartingAt(frame_idx));

        for (Trajectory tra : trajectoriesAtGivenFrame) {
            content = content + tra.toFramewiseString(frame_idx) + "\n";
        }
        return content.trim();
    }

    /**
     * Transforms all remaining trajectories to the Euclidian space using
     * extrinsic camera the calibration matrices and depth cues.
     */
    public void transformTrajectoryPointsToEuclidianSpace() {
        for (Trajectory tra : trajectories.values()) {

            // in case a trajectory has no valid depth information associated with its tracked points
            // mark it as deletable and proceed the next trajectory.
            boolean allTrackedPointsInvalid = tra.markInvalidPoints();
            if (allTrackedPointsInvalid) {
                tra.markAsDeletable();
                continue;
            }
            tra.transformTrackedPoints();
        }
    }
}

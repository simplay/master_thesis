package managers;

import datastructures.Point2d;
import datastructures.Trajectory;
import java.util.*;

/**
 * TrajectoryManager is a singleton used for accessing, creating/continuing and filtering/transforming trajectories.
 *
 * It keeps internally a collection of all existing trajectories, sorted by the trajectory label.
 *
 */
public class TrajectoryManager implements Iterable<Trajectory>{

    // A hash containing all the trajectories, with the trajectory label as key.
    // Note that a hash map automatically sorts its values by its keys (ascending).
    // Thus, trajectories are always sorted by their label id.
    private HashMap<Integer, Trajectory> trajectories;

    // Singleton state
    private static TrajectoryManager instance = null;

    /**
     * Get the trajectory manager singleton
     *
     * @return singleton instance
     */
    public static TrajectoryManager getInstance() {
        if (instance == null) {
            instance = new TrajectoryManager();
        }
        return instance;
    }

    /**
     * Create a new trajectory manager singleton
     */
    private TrajectoryManager() {
        trajectories = new HashMap<Integer, Trajectory>();
    }

    public static Collection<Trajectory> getTrajectories() {
        return instance.trajectories.values();
    }

    /**
     * Get k last trajectories.
     *
     * That are all ordered trajectories (by their label value)
     * in the collection of trajectories from the k-th till the last trajectory.
     *
     * The first k value is n, the last is n-1, where n is the number of trajectories.
     *
     * example: given trajectories [t1, t2, t3, t4], then
     *  getTrajectorySubset(0) #=> [t1, t2, t3, t4]
     *  getTrajectorySubset(1) #=> [t2, t3, t4]
     *  getTrajectorySubset(2) #=> [t3, t4]
     *  getTrajectorySubset(3) #=> [t4]
     *
     * @param k start index from which we collect all trajectories.
     * @return subset of trajectories containing the k last trajectories.
     */
    public static List<Trajectory> getTrajectorySubset(int k) {
        int n = getTrajectories().size();
        ArrayList<Trajectory> collection = new ArrayList<>(getTrajectories());
        return collection.subList(k, n);
    }

    /**
     * Deletes state of singleton.
     * Accessing this singleton once again, after calling this method,
     * will result in re-initializing its internal state.
     *
     * The garbage collector will be able to delete the internally held trajectory references.
     */
    public static void release() {
        instance = null;
        Trajectory.resetLabelCounter();
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
     * Note that the first frame index corresponds to the value 0.
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
     * Note that the length of a trajectory corresponds to
     * its number of tracking points minus one.
     *
     * I.e. trajectories of length 0 correspond to 'one-pointed' trajectories.
     *
     * @param length expected trajectory length value.
     * @return Collection of trajectories all having the expected length value.
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
     * Note that the collected trajectories are sorted by their label value (ascending).
     * Note that the first frame starts at index 0.
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
     * Select all trajectories that live in a series of frames starting at a given frame index.
     *
     * Note that the collected trajectories are sorted by their label value (ascending).
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
     * Filters all trajectories shorter than a given min length.
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

    /**
     * Generate the output string of the frame-wise active trajectories.
     *
     * Format:
     * /(L_ID ROW_POS COL_POS(\n))*(L_ID ROW_POS COL_POS)/
     *
     * where L_ID is the label id value of the trajectory currently considered
     * and active in the queried frame, ROW_POS and COL_POS are the row and coloumn
     * position of tracking point in the trajectory with the given label, active in the
     * queried frame.
     *
     * Example
     * "3 5.0 6.0\n5 9.0 10.0\n6 11.0 12.0"
     *
     * Please note that trajectories are separated by new lines
     * and ordered according to their label value (ascending).
     *
     * Note that the first frame has the index value 0.
     *
     * @param frame_idx current active frame.
     * @return dump of trajectories active in a given frame.
     */
    public String toFramewiseOutputString(int frame_idx) {
        String content = "";
        LinkedList<Trajectory> trajectoriesAtGivenFrame = allTrajectoriesActiveInGivenFrame(frame_idx);

        for (Trajectory tra : trajectoriesAtGivenFrame) {

            // skip if current frame point is an auxiliary point
            if (tra.getPointAtFrame(frame_idx).isAddition()) continue;
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

    /**
     * Extends trajectory tracking by trying to append and prepend
     * additional points. The points are computed by applying the average trajectory directory
     * to the boundary tracking points.
     *
     * Only call this method after running the filtering of too short trajectories.
     */
    public void continueTrajectories() {
        for (Trajectory tra : getTrajectories()) {
            tra.extendPointTracking();
        }
    }
}

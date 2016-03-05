import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

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
    public void startNewTrajectoryAt(Point2f p, int startingFrame) {
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
    public void appendPointTo(int trajectory_label, Point2f p) {
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
            if (tra.currentActiveFrame() == frame_idx) {
                actives.add(tra);
            }
        }
        return actives;
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
     * Filter all trajectories from the internal list of trajectories,
     * that have length zero, i.e. that consists of just one point.
     */
    public void filterOnePointedTrajectories() {
        for (Trajectory traj : allTrajectoryWithLength(0)) {
            trajectories.remove(traj.getLabel());
        }
    }

    /**
     * String representation of all trajectories in the format the
     * similarity computation script expects its input to be.
     *
     * @return
     */
    public String toOutputString() {
        String content = "";
        LinkedList<Trajectory> sortedByLabel = new LinkedList<>(trajectories.values());
        Collections.sort(sortedByLabel);

        for (Trajectory tra : sortedByLabel) {
            content = content + tra.toOutputString();
        }
        return content.trim();
    }

    public String toFramewiseOutputString(int frame_idx) {
        String content = "";
        LinkedList<Trajectory> trajectoriesAtGivenFrame = allTrajectoriesStartingAt(frame_idx);
        Collections.sort(allTrajectoriesStartingAt(frame_idx));

        for (Trajectory tra : trajectoriesAtGivenFrame) {
            content = content + tra.toFramewiseString(frame_idx) + "\n";
        }
        return content.trim();
    }


    /**
     * @param fPathName file path name for this graph's partition file.
     */
    public void saveTrajectoriesToFile(String fPathName) {
        try {
            try (PrintWriter out = new PrintWriter(fPathName)) {
                out.println(toOutputString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveFramewiseTrajectoryDataToFile(String baseOutPath, int till_idx) {
        String fname = "";
        for (int idx = 0; idx < till_idx; idx++) {
            fname = baseOutPath + "active_tra_f_"+idx+".txt";
            try {
                try (PrintWriter out = new PrintWriter(fname)) {
                    out.print(toFramewiseOutputString(idx));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

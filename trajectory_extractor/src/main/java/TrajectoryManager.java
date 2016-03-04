import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class TrajectoryManager implements Iterable<Trajectory>{

    private HashMap<Integer, Trajectory> trajectories;
    private static TrajectoryManager instance = null;

    public static TrajectoryManager getInstance() {
        if (instance == null) {
            instance = new TrajectoryManager();
        }
        return instance;
    }

    public int trajectoryCount() {
        return trajectories.size();
    }
    private TrajectoryManager() {
        trajectories = new HashMap<Integer, Trajectory>();
    }

    public void startNewTrajectoryAt(Point2f p, int startingFrame) {
        Trajectory tra = new Trajectory(startingFrame);
        tra.addPoint(p);
        trajectories.put(tra.getLabel(), tra);
    }

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

    public LinkedList<Trajectory> allTrajectoryWithLength(int length) {
        LinkedList<Trajectory> actives = new LinkedList<Trajectory>();
        for (Trajectory tra : trajectories.values()) {
            if (tra.length() == length) {
                actives.add(tra);
            }
        }
        return actives;
    }

    public void filterOnePointedTrajectories() {
        for (Trajectory traj : allTrajectoryWithLength(0)) {
            trajectories.remove(traj.getLabel());
        }
    }

    public String toOutputString() {
        String content = "";
        LinkedList<Trajectory> sortedByLabel = new LinkedList<>(trajectories.values());
        Collections.sort(sortedByLabel);

        for (Trajectory tra : sortedByLabel) {
            content = content + tra.toOutputString();
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
}

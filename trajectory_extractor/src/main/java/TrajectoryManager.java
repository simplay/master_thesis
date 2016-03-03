import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by simplay on 03/03/16.
 */
public class TrajectoryManager implements Iterable<Trajectory>{

    private HashMap<Integer, Trajectory> trajectories;
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

}

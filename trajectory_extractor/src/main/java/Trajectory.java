import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by simplay on 03/03/16.
 */
public class Trajectory implements Iterable<Point2f>{

    // points that span the trajectory
    private ArrayList<Point2f> points;

    // unique identifier of a trajectory
    private int label;

    // shared resource among trajectories
    private static int label_counter = 1;

    // assumption: first frame is equal 0
    private int startFrame;

    public Trajectory(int startFrame) {
        this.label = label_counter++;
        this.startFrame = startFrame;
    }

    public void addPoint(Point2f p) {
        points.add(p);
    }

    public int getLabel() {
        return label;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public Iterator<Point2f> iterator() {
        return points.iterator();
    }
}

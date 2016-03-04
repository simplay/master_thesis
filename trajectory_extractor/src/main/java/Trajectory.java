import java.util.ArrayList;
import java.util.Iterator;

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
        points = new ArrayList<Point2f>();
    }

    public int length() {
        return points.size()-1;
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

    public Point2f getPointAtFrame(int frame_idx) {
        return points.get(frame_idx-startFrame);
    }

    /**
     * Get the currently last frame in which this trajectory is active
     *
     * @example
     *  if trajectory starts at frame 0 and only has one point (the starting point)
     *  its current active frame is frame 0 (0 + 1 - 1).
     * @return
     */
    public int currentActiveFrame() {
        return startFrame + points.size() - 1;
    }

    public Iterator<Point2f> iterator() {
        return points.iterator();
    }

    public String toString() {
        String header = "l="+label+" s="+startFrame;
        String content = "";
        for (Point2f p : points) {
            content += p.toString() + " ";
        }
        return header+ " " +content;
    }

    public String toOutputString() {
        String header = "### L:" + getLabel() + " S:" + startFrame+1 + " C:" + length();
        String content = "";
        for (Point2f p : points) {
            content = content + p.toOutputString() + "\n";
        }
        return header + "\n" + content;
    }
}

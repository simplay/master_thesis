import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Trajectory implements Iterable<Point2f>, Comparable<Trajectory>{

    // points that span the trajectory
    private ArrayList<Point2f> points;
    private boolean isClosed = false;

    private HashMap<Integer, Double> similarities;

    // unique identifier of a trajectory
    private int label;

    // shared resource among trajectories
    private static int label_counter = 1;

    // assumption: first frame is equal 0
    private int startFrame;
    private int endFrame;

    public Trajectory(int startFrame) {
        this.label = label_counter++;
        this.startFrame = startFrame;
        points = new ArrayList<Point2f>();
        this.similarities = new HashMap<>();
        this.endFrame = -1;
    }

    public synchronized void assignSimilarityValueTo(int trajectoryLabel, double value) {
        similarities.put(trajectoryLabel, value);
    }

    public boolean livesInFrame(int frame_idx) {
        int till = startFrame+points.size()-1;

        if (frame_idx <= till && frame_idx >= startFrame) {
            return true;
        }
        return false;
    }

    public int getEndFrame() {
        return endFrame;
    }

    /**
     * A closed trajectory cannot be continued and is thus not selected
     * by the tracker anymore. In addition, the trajectories end frame is
     * assigned, when closed, which is the current active frame at that time.
     */
    public void markClosed() {
        this.isClosed = true;
        endFrame = currentActiveFrame();
    }

    public boolean notClosed() {
        return !isClosed;
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
        int out_start_frame = startFrame+1;
        String header = "### L:" + getLabel() + " S:" + out_start_frame + " C:" + length();
        String content = "";
        for (Point2f p : points) {
            content = content + p.toOutputString() + "\n";
        }
        return header + "\n" + content;
    }

    public String toFramewiseString(int frame_idx) {
        int idx = frame_idx - startFrame;
        Point2f p = points.get(idx);
        return label + " " + p.u() + " " + p.v();
    }

    @Override
    public int compareTo(Trajectory o) {
        if (label < o.getLabel()) {
            return -1;
        } else if (label == o.getLabel()) {
            return 0;
        } else {
            return 1;
        }
    }

}

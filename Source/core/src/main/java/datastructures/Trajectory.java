package datastructures;

import managers.DepthManager;
import managers.TrajectoryManager;
import pipeline_components.ArgParser;

import java.util.*;

/**
 * A Trajectory is a set of coherently tracked points of a particular feature over a set of frames.
 */
public class Trajectory implements Iterable<Point2d>, Comparable<Trajectory>{

    // points that span the trajectory
    private ArrayList<Point2d> points;

    //private ArrayList<Point2d> transformedPoints;
    private ArrayList<Point3d> euclidianPoints;

    // Marks whether this trajectory can be continued during the tracking step
    private boolean isClosed = false;

    // indicates whether this trajectory should be filtered
    private boolean isDeletable = false;

    // all similarities between this and any other trajectory
    private TreeMap<Integer, Double> similarities;

    // all avg spatial pixel distances between this and any other trajectory
    private HashMap<Integer, Double> avgSpatialDistToNeighbors;

    // unique identifier of a trajectory
    private int label;

    // shared resource among trajectories
    private static int label_counter = 1;

    // frame index of first the first tracking point of this trajectory.
    // assumption: first frame is equal 0
    private int startFrame;

    // Frame index the last tracked point of this trajectory belongs to.
    private int endFrame;

    // Indicates whether any similarity value were computed for this trajectory
    private boolean hasSimilarityValues = false;

    private String traOutRepresentation = null;

    /**
     * Start a ne wtrajectory at a given frame.
     *
     * @param startFrame frame of first tracking point of this trajectory.
     */
    public Trajectory(int startFrame) {
        this.label = label_counter++;
        this.startFrame = startFrame;
        points = new ArrayList<Point2d>();
        this.similarities = new TreeMap<>();
        this.endFrame = -1;
        this.avgSpatialDistToNeighbors = new HashMap<>();
    }

    /**
     * Remove all spatial neighbors that do not map to a existing similarity value.
     * Since some trajectory similarities were filtered due to inconsistency checks,
     * but they were added as a spatial neighbor, we also have to filter them from
     * the neighbor list.
     *
     * @return reports the number of filtered neighbors
     */
    public int filterInvalidSpatialNeighbors() {
        int filteredCount = 0;
        for (int key : avgSpatialDistToNeighbors.keySet()) {
            if (!similarities.containsKey(key)) {
                avgSpatialDistToNeighbors.remove(key);
                filteredCount++;
            }
        }
        return filteredCount;
    }

    /**
     * Checks whether this trajectory closed.
     * note that closed means, that its end frame is correctly assigned.
     *
     * @return
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Pre-allocate memory for data-structures used during
     * the computation of similarity values during computation.
     */
    public void initSimilarityDatastructures() {
        int n = TrajectoryManager.getTrajectories().size();
        this.avgSpatialDistToNeighbors = new HashMap<>(n);
        this.similarities = new TreeMap<>();
    }

    /**
     * Flags this trajectory as deletable.
     * A trajectory is marked as deletable if and only if
     * it has either no similarity values to any other trajectory assigned to
     * or it exhibits no similarity value greater than zero. Therefore, such
     * trajectories do not carry any useful information and thus can be deleted.
     */
    public void markAsDeletable() {
        this.isDeletable = true;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public boolean hasSimilarityValues() {
        return hasSimilarityValues;
    }

    /**
     * Fast fetching of nearest N avg spatial neighbors
     *
     * Returns an ordered list of the nearest n neighbors according to their spatial
     * avg distance to this trajectory.
     *
     * @param numberOfNeighbors number of top n neighbors that should be returned.
     * @return n top neighbors according to their average spatial distance to this trajectory
     *  (regarding their overlapping trackings).
     */
    public List<Integer> nearestAvgSpatialNeighbors(int numberOfNeighbors) {
        Iterator<Map.Entry<Integer, Double>> it = avgSpatialDistToNeighbors.entrySet().iterator();
        NearestNeighborsHeap avgSpDist = new NearestNeighborsHeap(avgSpatialDistToNeighbors.size());
        while (it.hasNext()) {
            Map.Entry<Integer, Double> el = it.next();
            int label = el.getKey();
            double dist = el.getValue();
            avgSpDist.addItem(label, dist);
        }
        return avgSpDist.toIntList(numberOfNeighbors);
    }

    /**
     * Filter the similarity and spatial avg distance value of
     * an now invalid neighboring trajectory.
     *
     * @param label unique identifier of an invalid neighborhood trajectory
     *  for which similarity and avg spatial values were computed.
     */
    public void filterSimilarityOfTrajectory(int label) {
        similarities.remove(label);

        // only deletes neighbors that have a zero similarity to this trajectory
        // but are still in the list of neighbors.
        avgSpatialDistToNeighbors.remove(label);
    }

    /**
     * Assign the similarity value between this trajectory and another trajectory given
     * by its label value.
     *
     * @param trajectoryLabel identifier of other trajectory
     * @param value the similarity value
     */
    public void assignSimilarityValueTo(int trajectoryLabel, double value) {
        synchronized (similarities) {

            // If there is a meaningful similarity value
            if (value > 0d || value < 0d) {
                hasSimilarityValues = true;
            }
            similarities.put(trajectoryLabel, value);
        }
    }

    /**
     * Assign the average spatial distance of the overlapping parts between this and
     * another trajectory given by its trajectory label.
     *
     * @param trajectoryLabel identifier of the other trajectory
     * @param value the avg spatial distance (in pixel units) of the overlapping segments.
     */
    public void appendAvgSpatialDist(int trajectoryLabel, double value) {
        synchronized (avgSpatialDistToNeighbors) {
            avgSpatialDistToNeighbors.put(trajectoryLabel, value);
        }
    }

    /**
     * Checks whether this trajectory was tracked in a certain frame.
     *
     * @param frame_idx unique number of frame
     * @return true if this trajectory has a tracking point in the given frame.
     */
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
        traOutRepresentation = toOutputString();
    }

    public String getOutputString() {
        return traOutRepresentation;
    }

    public boolean notClosed() {
        return !isClosed;
    }

    public int length() {
        return points.size()-1;
    }

    public void addPoint(Point2d p) {
        points.add(p);
    }

    public int getLabel() {
        return label;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public Point2d getPointAtFrame(int frame_idx) {
        return points.get(frame_idx-startFrame);
    }

    public Point2d getSpatialPointAtFrame(int frame_idx) {
        return points.get(frame_idx-startFrame);
    }

    public Point3d getEuclidPositionAtFrame(int frame_idx) {
        return euclidianPoints.get(frame_idx-startFrame);
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

    public Iterator<Point2d> iterator() {
        return points.iterator();
    }

    public String toString() {
        String header = "l="+label+" s="+startFrame;
        String content = "";
        for (Point2d p : points) {
            content += p.toString() + " ";
        }
        return header+ " " +content;
    }

    public String toOutputString() {
        int out_start_frame = startFrame+1;
        String header = "### L:" + getLabel() + " S:" + out_start_frame + " C:" + length();
        String content = "";
        for (Point2d p : points) {
            content = content + p.toOutputString() + "\n";
        }
        return header + "\n" + content;
    }

    public String toFramewiseString(int frame_idx) {
        int idx = frame_idx - startFrame;
        Point2d p = points.get(idx);
        return label + " " + p.u() + " " + p.v();
    }

    public String toSimilarityString() {
        return similarities.values().toString();
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

    /**
     * Transforms tracked trajectory points into the Euclidian space
     * by making use of depth cues.
     *
     * Z = depth_image[v,u];
     * X = (u - cx) * Z / fx;
     * Y = (v - cy) * Z / fy;
     */
    public void transformTrackedPoints() {
        ArrayList<Point3d> depthPoints = new ArrayList<>();
        int idx = startFrame;
        for (Point2d p : points) {
            p.compute3dTrackedPosition(idx);
            depthPoints.add(p.getEuclidPos());
            idx++;
        }
        this.euclidianPoints = depthPoints;
    }

    /**
     * Marks every tracked trajectory point that does not have
     * a valid depth field position.
     *
     * @return true if all tracked points are labeled as invalid.
     */
    public boolean markInvalidPoints() {
        int idx = startFrame;
        int count = points.size();
        int invCounter = 0;
        for (Point2d p : points) {
            DepthField depthField = DepthManager.getInstance().get(idx);
            if (!depthField.validRegionAt(p.x(), p.y())) {
                p.markAsInvalid();
                invCounter++;
            }
            idx++;
        }
        return count == invCounter;
    }

}

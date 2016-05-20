package datastructures;

import managers.DepthManager;
import java.util.*;

/**
 * A Trajectory is a set of coherently tracked points of a particular feature over a set of frames.
 * Each trajectory is uniquely identified by its label value.
 * A trajectory knows their similarity value to other existing trajectories as well as
 * the avg spatial distance towards other trajectories./get
 */
public class Trajectory implements Iterable<Point2d>, Comparable<Trajectory>{

    // Points that span the trajectory
    private ArrayList<Point2d> points;

    // Private ArrayList<Point2d> transformedPoints;
    private ArrayList<Point3d> euclidianPoints;

    // Indicates if this trajectory can be continued during the tracking step
    // Only trajectories that are not yet closed can be continued.
    private boolean isClosed = false;

    // indicates whether this trajectory should be filtered
    // Trajectories are deletable if they either do not have any similarity value assigned to
    // or are too short or out of
    private boolean isDeletable = false;

    // all similarities between this and any other trajectory
    // A TreeMap sorts its hash-items by their key (i.e. the trajectory label)
    //  key = label of other trajectory,
    //  value = similarity value between this and other trajectory
    private TreeMap<Integer, Double> similarities;

    // All avg spatial pixel distances between this and any other trajectory
    // A TreeMap sorts its hash-items by their key (i.e. the trajectory label)
    //  key = label of other trajectory,
    //  value = avg spatial distance between this and other trajectory
    private TreeMap<Integer, Double> avgSpatialDistToNeighbors;

    // Unique identifier of a trajectory
    private int label;

    // Shared resource among trajectories
    // Assumption: The first trajectory label is equal to 1.
    private static int label_counter = 1;

    // frame index of first the first tracking point of this trajectory.
    // assumption: first frame is equal 0
    private int startFrame;

    // Frame index the last tracked point of this trajectory belongs to.
    private int endFrame;

    // Indicates whether any similarity value were computed for this trajectory
    private boolean hasSimilarityValues = false;

    // Value used by the TrajectoryWriter for generating its output.
    // Corresponds to Trajectory#toString()
    private String traOutRepresentation = null;

    /**
     * Start a new trajectory at a given frame.
     * Note that each trajectory gets a unique number assigned called the
     * trajectory label.
     *
     * @param startFrame frame of first tracking point of this trajectory.
     */
    public Trajectory(int startFrame) {
        this.label = label_counter++;
        this.startFrame = startFrame;
        points = new ArrayList<Point2d>();
        this.similarities = new TreeMap<>();
        this.endFrame = -1;
        this.avgSpatialDistToNeighbors = new TreeMap<>();
    }

    /**
     * Resets the trajectory label counter
     */
    public static void resetLabelCounter() {
        label_counter = 1;
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
     * Note that closed means, that its end frame is correctly assigned.
     *
     * @return true if we marked the trajectory as closed, false otherwise.
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Pre-allocate memory for data-structures used during
     * the computation of similarity values during computation.
     */
    public void initSimilarityDatastructures() {
        this.avgSpatialDistToNeighbors = new TreeMap<>();
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

    /**
     * Checks whether this trajectory is marked as deletable.
     * A trajectory is deletable if either
     *  + it has no affinity values associated with or
     *  + if all of its tracking points are invalid
     *
     * Note that trajectories marked as deletable are selected by
     * the post filtering process.
     *
     * @return is this trajectory deletable.
     */
    public boolean isDeletable() {
        return isDeletable;
    }

    /**
     * Checks whether this trajectory exhibits valid similarity values.
     *
     * @return true if it has valid similarities associated with, otherwise false.
     */
    public boolean hasSimilarityValues() {
        return hasSimilarityValues;
    }

    /**
     * Fast fetching of nearest N avg spatial neighbors
     *
     * Returns an ordered list of the nearest n neighbors (ordered from the top- to the worst neighbors)
     * according to their spatial avg distance to this trajectory.
     *
     * The first element in the returned list corresponds to the best nearest neighbors,
     * the last to the worst (when running the NN mode top).
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
     * Get all nearest neighbors for this trajectory
     *
     * @return a list of nearest neighbors.
     */
    public List<Integer> allNearestNeighbors() {
        return new LinkedList<>(avgSpatialDistToNeighbors.keySet());
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

    /**
     * Returns the index of the last frame in which this trajectory
     * has an active tracked point.
     *
     * Note that this end frame index is only set if we mark this trajectory as closed.
     *
     * @return frame index of the last tracking point.
     */
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

    /**
     * Get the representation used by the TrajectoryWriter.
     *
     * Requires the trajectories marked as closed in order to have a valid output representation.
     *
     * @return the to string representation of the TrajectoryWriter.
     */
    public String getOutputString() {
        return traOutRepresentation;
    }

    /**
     * Is this trajectory not marked as closed?
     *
     * @return true if not closed, otherwise false.
     */
    public boolean notClosed() {
        return !isClosed;
    }

    /**
     * The number of tracking points this trajectory carries.
     *
     * @return number of tracking points.
     */
    public int length() {
        return points.size()-1;
    }

    /**
     * Append a new tracking point to this trajectory.
     * Assumption: a new added tracking point belongs to the
     * frame index startFrame + tra.length()
     *
     * @param p new point we add to this trajectory.
     */
    public void addPoint(Point2d p) {
        points.add(p);
    }

    /**
     * Get the unique label identifier of this trajectory,
     * i.e. the frame (index) in which its last tracked point lives in.
     *
     * @return trajectory label.
     */
    public int getLabel() {
        return label;
    }

    /**
     * Get the star frame of this trajectory.
     * I.e. the frame in which its first tracked point lives in.
     * @return
     */
    public int getStartFrame() {
        return startFrame;
    }

    /**
     * Get a tracked trajectory point by its frame index.
     *
     * @param frame_idx a frame index in which one of this
     *                  trajectory's tracked points is active.
     * @return
     */
    public Point2d getPointAtFrame(int frame_idx) {
        return points.get(frame_idx-startFrame);
    }

    /**
     * Get the 3D point representation of a tracking point by its frame index.
     * Note that making use of this accessor only makes sense if and only if
     * we are using depth cues.
     *
     * @param frame_idx the frame index of a tracked point.
     *
     * @return the 3d point active in a given frame.
     */
    public Point3d getEuclidPositionAtFrame(int frame_idx) {
        return euclidianPoints.get(frame_idx-startFrame);
    }

    /**
     * Get the currently last frame in which this trajectory is active
     *
     * @example
     *  if trajectory starts at frame 0 and only has one point (the starting point)
     *  its current active frame is frame 0 (0 + 1 - 1).
     *
     *
     * @return the current active frame which corresponds to the frame index to which
     *  the last tracked point of this trajectory belongs to.
     */
    public int currentActiveFrame() {
        return startFrame + points.size() - 1;
    }

    /**
     * Point iterator
     *
     * @return Iterator of tracked points.
     */
    public Iterator<Point2d> iterator() {
        return points.iterator();
    }

    /**
     * Obtain the debugging representation of this trajectory.
     *
     * @return debuggable representation of this trajectory.
     */
    public String toString() {
        String header = "l=" + label + " s=" + startFrame;
        String content = "";
        for (Point2d p : points) {
            content += p.toString() + " ";
        }
        return header + " " + content;
    }

    /**
     * Get representation used for dumping the extracted trajectories stored (optionally)
     * in `output/trajectories/`.
     *
     * @return serialized trajectory represntation listing all of its points and meta information.
     */
    public String toOutputString() {
        int out_start_frame = startFrame+1;
        String header = "### L:" + getLabel() + " S:" + out_start_frame + " C:" + length();
        String content = "";
        for (Point2d p : points) {
            content = content + p.toOutputString() + "\n";
        }
        return header + "\n" + content;
    }

    /**
     * Get representation used for dumping all active trajectory points in a frame.
     * The generated file is stored (optionally) at `output/trajectory_label_frame/<DATASET>/`.
     *
     * @param frame_idx index of frame where the trajectory point is supposed to live.
     * @return
     */
    public String toFramewiseString(int frame_idx) {
        int idx = frame_idx - startFrame;
        Point2d p = points.get(idx);
        return label + " " + p.u() + " " + p.v();
    }

    /**
     * Returns all similarity values from this trajectory to any other trajectory.
     * This representation encodes a line of the affinity matrix.
     * Note that the similarities are sorted according to their label identifier,
     * therefore no additional sorting step is required.
     *
     * @return a row in the affinity matrix.
     */
    public String toSimilarityString() {
        return similarities.values().toString();
    }

    /**
     * Compare two trajectories by their label to define an order among trajectories.
     * Used for spanning the affinity matrix.
     *
     * when sorting a list of trajectories, then trajectories with a lower label
     * have a lower index in the sorted list. I.e the list is sorted ascending according
     * to the trajectory label values.
     *
     * @param o other trajectory
     * @return -1 if this trajectory's label is smaller than the one from the reference trajectory,
     *  0 if same label value and 1 in case it is larger.
     */
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

package datastructures;

import managers.MetaDataManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * TrajectoryExtender allows to compute trajectory point extensions using the currently assigned points
 * as a cues. It allows to compute points that can be appended to a trajectory
 * as well as points that can be prepended.
 *
 * Only points that are within valid dataset frame indices are computed.
 * For computing new points, the average direction is computed and applied to the currently last existing point.
 *
 * Note that the number of prepending / appending points can deviate from the given add count:
 * We can only prepend points to a trajectory, as long as it does not get underun. Similarly,
 * we only can append points to a trajectory, as long as it does not exceed the largest possible end-frame index.
 *
 * This helper class is used within the trajectory to extend trajectories, used in case a user sets `-ct 1`.
 */
public class TrajectoryExtender {

    // A collection of points modeling the tracking points of a trajectory
    private List<Point2d> points;

    // the previous starting frame of the target trajectory.
    private int startFrame;

    // The previous ending frame of the target trajectory
    private int endFrame;

    // The number of extensions per side (append/prepend count) we would like to have.
    private int useAvgFrameCount;

    public TrajectoryExtender(List<Point2d> points, int startFrame, int endFrame, int useAvgFrameCount) {
        this.points = points;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.useAvgFrameCount = useAvgFrameCount;
    }

    public TrajectoryExtender(List<Point2d> points, int startFrame, int endFrame) {
        this(points, startFrame, endFrame, 5);
    }

    /**
     * Try to prepend a given number of points to this trajectory by subtracting
     * continuously the average trajectory direction of the first 5 frames.
     *
     * Not that points are only prepended, if the start frame does not
     * underun the frame index 0.
     *
     * @param addCount number of points we want to add at most.
     * @return the list of points to be prepended.
     */
    public ArrayList<Point2d> getLeftPointContinuation(int addCount) {
        ArrayList<Point2d> additions = new ArrayList<>();

        int selCount = getSelectablePointCount(useAvgFrameCount);
        if (selCount == 0) return additions;

        Point2d avgDir = avgDirection(selectFirstNPoints(selCount)).div_by(-1);

        // save from underunning startFrame index
        // startFrame - addCount >= 0 => use addCount
        // startFrame - addCount < 0 => use startFrame since startFrame < addCount and startFrame-startFrame == 0
        int allowedAddCount = (startFrame - addCount >= 0) ? addCount : startFrame;
        additions = generateAdditions(points.get(0), avgDir, allowedAddCount);
        Collections.reverse(additions);

        return additions;
    }

    /**
     * Try to append a given number of points to this trajectory by adding
     * continuously the average trajectory direction of the last 5 frames.
     *
     * Not that points are only appended, if the end frame index does
     * not exceed the total number of frames.
     *
     * @param addCount number of points we want to add at most.
     * @return the list of points to be appended.
     */
    public ArrayList<Point2d> getRightPointContinuation(int addCount) {
        ArrayList<Point2d> additions = new ArrayList<>();

        int selCount = getSelectablePointCount(useAvgFrameCount);
        if (selCount == 0) return additions;

        Point2d avgDir = avgDirection(selectLastNPoints(selCount));

        int lastAllowedFrameIndex = MetaDataManager.frameCount();
        int diff = lastAllowedFrameIndex - endFrame;
        if (diff == 0) return additions;
        int allowedAddCount = (diff >= addCount) ? addCount : diff;

        Point2d startingPoint = points.get(points.size() - 1);
        additions = generateAdditions(startingPoint, avgDir, allowedAddCount);

        return additions;
    }

    /**
     * Generates a given number of additions from a given starting point, using a given direction.
     * The direction is n times applied to the currently last point. Initially, the provided
     * starting point is used.
     *
     * @param start starting point from which we generated new points.
     * @param avgDir average direction used for computing the next addition point.
     * @param allowedAddCount the number of points we would like to generate
     * @return a set of addition points.
     */
    public ArrayList<Point2d> generateAdditions(Point2d start, Point2d avgDir, int allowedAddCount) {
        ArrayList<Point2d> additions = new ArrayList<>();
        Point2d startP = start;
        for (int n = 0; n < allowedAddCount; n++) {
            startP = startP.copy().add(avgDir);
            startP.markAsBelongsToAddition();
            additions.add(startP);
        }
        return additions;
    }

    /**
     * Obtain the number of de facto selectable points, given
     * a wished use-count. If the wish count exceeds the possible
     * count, the count is truncated to the possible count.
     *
     * @param useCount the number we would like to select.
     * @return the actual possible select count.
     */
    public int getSelectablePointCount(int useCount) {
        int N = points.size();

        // N < useCount => use N points
        // N > useCount => use useCount
        // N == useCount => use useCount
        return (N < useCount) ? N : useCount;
    }

    /**
     * Compute the average direction given a set of points.
     * For computing the avg direction, we calculate the average
     * over the finite differences along the points.
     *
     * @param positions
     * @return average direction
     */
    public Point2d avgDirection(LinkedList<Point2d> positions) {
        int N = positions.size();
        Point2d avgDir = new Point2d(0, 0);
        int counter = 0;
        for (int n = 0; n < N - 1; n++) {
            Point2d p1 = positions.get(n + 1);
            Point2d p0 = positions.get(n);
            avgDir.add(p1.copy().sub(p0));
            counter++;
        }
        avgDir.div_by(counter);
        return avgDir;
    }

    /**
     * Select the first n points from the collection of all points.
     *
     * @param n number of points to be selected.
     * @return the first n points in the point collection.
     */
    public LinkedList<Point2d> selectFirstNPoints(int n) {
        LinkedList<Point2d> selection = new LinkedList<>();
        for (int k = 0; k < n; k++) {
            selection.add(points.get(k));
        }
        return selection;
    }

    /**
     * Select the last n points from the collection of all points.
     *
     * @param n number of points to be selected.
     * @return the last n points in the point collection.
     */
    public LinkedList<Point2d> selectLastNPoints(int n) {
        LinkedList<Point2d> selection = new LinkedList<>();
        int N = points.size() - 1;
        for (int k = 0; k < n; k++) {
            selection.add(points.get(N-k));
        }
        Collections.reverse(selection);
        return selection;
    }
}

import datastructures.Point2d;
import datastructures.Point3d;
import datastructures.Trajectory;
import junit.framework.Assert;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.MetaDataManager;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.CalibrationsReader;
import readers.DepthFieldReader;
import similarity.SimilarityTask;

import java.lang.reflect.Field;
import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SimilarityTaskTest {

    private SimilarityTaskHelper nullHelper;

    /**
     * Helper method to set manually the trajectory manager trajectories.
     *
     * @param trajectories trajectories to be assigned to the TrajectoryManager singleton.
     */
    private void setTrajectoryManagerTrajectories(HashMap<Integer, Trajectory> trajectories) {
        Field field = null;
        try {
            field = TrajectoryManager.class.getDeclaredField("trajectories");
            field.setAccessible(true);
            try {
                field.set(TrajectoryManager.getInstance(), trajectories);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private TreeMap<Integer, Double> getNeighborsHashFor(Trajectory tra) {
        Field field = null;
        try {
            field = Trajectory.class.getDeclaredField("avgSpatialDistToNeighbors");
            field.setAccessible(true);
            try {
                return (TreeMap<Integer, Double>)field.get(tra);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper class to access protected members of SimilarityTask without having
     * to break encapsulation globally.
     *
     * The prefix `p_` prepended on every method stands for `public`, the public,
     * wrapped version of a SimilarityTask method.
     */
    class SimilarityTaskHelper extends SimilarityTask {

        /**
         * @param a
         * @param trajectories
         */
        public SimilarityTaskHelper(Trajectory a, Collection<Trajectory> trajectories) {
            super(a, trajectories);
        }

        @Override
        protected double similarityBetween(Trajectory a, Trajectory b) {
            return 1;
        }

        public int p_getLowerFrameIndexBetween(Trajectory a, Trajectory b) {
            return getLowerFrameIndexBetween(a, b);
        }

        public int p_getUpperFrameIndexBetween(Trajectory a, Trajectory b) {
            return getUpperFrameIndexBetween(a, b);
        }

        public int p_overlappingFrameCount(int from_idx, int to_idx) {
            return overlappingFrameCount(from_idx, to_idx);
        }

        public int p_timestepSize(int common_frame_count) {
            return timestepSize(common_frame_count);
        }

        public boolean p_isTooShortOverlapping(Trajectory a, Trajectory b, int overlappingFrameCount) {
            return isTooShortOverlapping(a, b, overlappingFrameCount);
        }

        public boolean p_isTooShortOverlapping(int overlappingFrameCount) {
            return isTooShortOverlapping(overlappingFrameCount);
        }

        public boolean p_hasOverlapWithoutContinuation(Trajectory a, Trajectory b) {
            return hasOverlapWithoutContinuation(a, b);
        }

        public Point2d p_forward_difference(Trajectory tra, int dt, int frame_idx) {
            return forward_difference(tra, dt, frame_idx);
        }

        public double p_spatialDistBetween(Trajectory a, Trajectory b, int frame_idx) {
            return spatialDistBetween(a, b, frame_idx);
        }

        public boolean p_trajectoryPointsInvalid(Point2d pa, Point2d pb) {
            return trajectoryPointsInvalid(pa, pb);
        }

        public void p_appendAvgSpatialDistances(Trajectory a, Trajectory b, double sp_dist) {
            appendAvgSpatialDistances(a, b, sp_dist);
        }

        public Point3d p_forward_difference3d(Trajectory tra, int dt, int frame_idx) {
            return forward_difference3d(tra, dt, frame_idx);
        }
    }

    @Before
    public void prepare() {
        ArgParser.release();
        TrajectoryManager.release();
        MetaDataManager.release();
        CalibrationManager.release();
        DepthManager.release();

        String[] args = {"-ct", "1"};
        ArgParser.getInstance(args);

        nullHelper = new SimilarityTaskHelper(null, null);
        ArrayList<String> metaData = new ArrayList<String>();
        metaData.add("100");
        metaData.add("100");
        metaData.add("1");
        MetaDataManager.getInstance(metaData);
    }

    @Test
    public void testGetLowerFrameIndexBetween() {
        int N = 10;
        for (int n = 0; n < N; n++) {
            TrajectoryManager.release();
            int startA = (int)((Math.random() * 50));
            int startB = (int)((Math.random() * 50));
            int max = Math.max(startA, startB);
            Trajectory a = new Trajectory(startA);
            Trajectory b = new Trajectory(startB);
            assertEquals(max, nullHelper.p_getLowerFrameIndexBetween(a, b));
        }
    }

    @Test
    public void testGetLowerFrameIndexBetweenAdditions() {
        MetaDataManager.getInstance().setFrameCount(8);

        Trajectory a = new Trajectory(3);
        Trajectory b = new Trajectory(7);

        a.addPoint(new Point2d(0.0, 10.1));
        a.addPoint(new Point2d(0.0, 10.2));
        a.addPoint(new Point2d(0.0, 10.2));

        b.addPoint(new Point2d(0.0, 20.1));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));

        a.markClosed();
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        assertEquals(4, nullHelper.p_getLowerFrameIndexBetween(a, b));
        assertEquals(4, b.getOverlappingStartFrameIndexBetween(a));
        assertEquals(4, a.getOverlappingStartFrameIndexBetween(b));
        assertEquals(0, a.getStartFrame());
        assertEquals(4, b.getStartFrame());
        assertEquals(3, a.startFrameWithoutLeftAdditions());
        assertEquals(7, b.startFrameWithoutLeftAdditions());
    }

    @Test
    public void testGetLowerFrameIndexBetweenAdditionsWithoutSettingCT() {
        ArgParser.release();
        String[] args = {"-ct", "0"};
        ArgParser.getInstance(args);

        MetaDataManager.getInstance().setFrameCount(8);

        Trajectory a = new Trajectory(3);
        Trajectory b = new Trajectory(7);

        a.addPoint(new Point2d(0.0, 10.1));
        a.addPoint(new Point2d(0.0, 10.2));
        a.addPoint(new Point2d(0.0, 10.2));

        b.addPoint(new Point2d(0.0, 20.1));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));

        a.markClosed();
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        assertEquals(4, nullHelper.p_getLowerFrameIndexBetween(a, b));
        assertEquals(4, b.getOverlappingStartFrameIndexBetween(a));
        assertEquals(4, a.getOverlappingStartFrameIndexBetween(b));
        assertEquals(0, a.getStartFrame());
        assertEquals(4, b.getStartFrame());
        assertEquals(3, a.startFrameWithoutLeftAdditions());
        assertEquals(7, b.startFrameWithoutLeftAdditions());
    }

    @Test
    public void testGetUpperFrameIndexBetween() {
        int N = 10;
        for (int n = 0; n < N; n++) {
            TrajectoryManager.release();
            int startA = (int)((Math.random() * 50));
            int startB = (int)((Math.random() * 50));
            int min = Math.min(startA, startB);
            Trajectory a = new Trajectory(startA);
            a.addPoint(Point2d.one());
            Trajectory b = new Trajectory(startB);
            b.addPoint(Point2d.one());
            a.markClosed();
            b.markClosed();
            assertEquals(min, nullHelper.p_getUpperFrameIndexBetween(a, b));
        }
    }

    @Test
    public void testGetUpperFrameIndexBetweenAdditions() {
        MetaDataManager.getInstance().setFrameCount(12);

        Trajectory a = new Trajectory(3);
        Trajectory b = new Trajectory(6);

        a.addPoint(new Point2d(0.0, 10.1));
        a.addPoint(new Point2d(0.0, 10.2));
        a.addPoint(new Point2d(0.0, 10.2));

        // only two additions, since 12 - (6 + 5 - 1) = 2
        b.addPoint(new Point2d(0.0, 20.1));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));

        a.markClosed();
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        assertEquals(8, nullHelper.p_getUpperFrameIndexBetween(a, b));
        assertEquals(8, a.getOverlappingEndFrameIndexBetween(b));
        assertEquals(8, b.getOverlappingEndFrameIndexBetween(a));
        assertEquals(8, a.getEndFrame());
        assertEquals(12, b.getEndFrame());
        assertEquals(5, a.endFrameWithoutRightAdditions());
        assertEquals(10, b.endFrameWithoutRightAdditions());
    }

    @Test
    public void testGetUpperFrameIndexBetweenAdditionsWithoutSettingCT() {
        ArgParser.release();
        String[] args = {"-ct", "0"};
        ArgParser.getInstance(args);
        MetaDataManager.getInstance().setFrameCount(12);

        Trajectory a = new Trajectory(3);
        Trajectory b = new Trajectory(6);

        a.addPoint(new Point2d(0.0, 10.1));
        a.addPoint(new Point2d(0.0, 10.2));
        a.addPoint(new Point2d(0.0, 10.2));

        // only two additions, since 12 - (6 + 5 - 1) = 2
        b.addPoint(new Point2d(0.0, 20.1));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));
        b.addPoint(new Point2d(0.0, 20.2));

        a.markClosed();
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        assertEquals(8, nullHelper.p_getUpperFrameIndexBetween(a, b));
        assertEquals(8, a.getOverlappingEndFrameIndexBetween(b));
        assertEquals(8, b.getOverlappingEndFrameIndexBetween(a));
        assertEquals(8, a.getEndFrame());
        assertEquals(12, b.getEndFrame());
        assertEquals(5, a.endFrameWithoutRightAdditions());
        assertEquals(10, b.endFrameWithoutRightAdditions());
    }

    @Test
    public void testOverlappingFrameCountSameFrameIndex() {
        int sameFrameIdx = (int)(Math.random() * 10);
        assertEquals(1, nullHelper.p_overlappingFrameCount(sameFrameIdx, sameFrameIdx));
    }

    @Test
    public void testOverlappingFrameCountDifferentFrameIndices() {
        int N = 10;
        for (int n = 0; n < N; n++) {
            int startIdx = (int) (Math.random() * 10);
            int step = (int) (Math.random() * 5) + 1;
            int endIdx = startIdx + step;

            // 1+step since, we count the start frame plus #step numbers of successor frames.
            assertEquals(1 + step, nullHelper.p_overlappingFrameCount(startIdx, endIdx));
        }
    }

    @Test
    public void testOverlappingCountIncreasesForNoneOverlappingTrajectoriesWhenExtendingThemCenterCase() {
        MetaDataManager.getInstance().setFrameCount(12);

        // active in frames: 5 6 7
        Trajectory a = new Trajectory(5);
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.markClosed();

        // active in frames: 8 9 10
        Trajectory b = new Trajectory(8);
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());

        int from_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        int to_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);

        int overlapCount = nullHelper.p_overlappingFrameCount(from_idx, to_idx);
        if (overlapCount <= 0) overlapCount = 0;

        assertEquals(0, overlapCount);

        // active in frames: 2 3 4 ,5 6 7, 8 9 10
        a.extendPointTracking();

        // active in frames: 5 6 7 ,8 9 10, 11 12 (no 13, since limited to 12)
        b.extendPointTracking();

        from_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        to_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);
        overlapCount = nullHelper.p_overlappingFrameCount(from_idx, to_idx);

        assertEquals(6, overlapCount);
    }

    @Test
    public void testOverlappingCountIncreasesForNoneOverlappingTrajectoriesWhenExtendingThem() {
        MetaDataManager.getInstance().setFrameCount(12);

        // active in frames: 0 1 2
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.markClosed();

        // active in frames: 8 9 10
        Trajectory b = new Trajectory(7);
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());

        int from_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        int to_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);

        int overlapCount = nullHelper.p_overlappingFrameCount(from_idx, to_idx);
        if (overlapCount <= 0) overlapCount = 0;

        assertEquals(0, overlapCount);

        // active in frames: 0 1 2, 3 4 5
        a.extendPointTracking();

        // active in frames: 4 5 6, 7 8 9, 10 11 12 (no 13, since limited to 12)
        b.extendPointTracking();

        from_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        to_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);
        overlapCount = nullHelper.p_overlappingFrameCount(from_idx, to_idx);

        assertEquals(2, overlapCount);
    }

    @Test
    public void testTimestepSize() {
        // defined as final member in SimilarityTask
        int minTimestep = 4;
        for (int n = 0; n <= minTimestep; n++) {
            assertEquals(n, nullHelper.p_timestepSize(n));
        }

        int N = 5;
        for (int n = minTimestep + 1; n < minTimestep + 1 + N; n++) {
            assertEquals(minTimestep, nullHelper.p_timestepSize(n));
        }
    }

    @Test
    public void testIsTooShortOverlappingOnlyFrameCount() {
        int minTraLen = nullHelper.minExpectedTrajectoryLength();

        for (int n = 1; n <= minTraLen; n++) {
            assertTrue(nullHelper.p_isTooShortOverlapping(n));
        }

        for (int n = minTraLen + 1; n < minTraLen + 10; n++) {
            assertFalse(nullHelper.p_isTooShortOverlapping(n));
        }
    }

    @Test
    public void testIsTooShortOverlappingIsOverlappingWithoutCont() {
        LinkedList<String[]> argsList = new LinkedList<>();
        String[] args1 = {"-ct", "1"};
        String[] args2 = {"-ct", "2"};
        argsList.add(args1);
        argsList.add(args2);

        // Too short logic works when enabling or disabling the ct flag
        for (String[] args : argsList) {
            ArgParser.release();
            ArgParser.getInstance(args);

            // Tra a, not continued, living in frame indices:
            // 0 1 ,2 3 4 5 6
            Trajectory a = new Trajectory(0);
            a.addPoint(Point2d.one());
            a.addPoint(Point2d.one());
            a.addPoint(Point2d.one());
            a.addPoint(Point2d.one());
            a.addPoint(Point2d.one());
            a.addPoint(Point2d.one());
            a.addPoint(Point2d.one());
            a.markClosed();

            // Tra b, not continued, living in frame indices:
            // 2 3 4 5 6, 7 8
            Trajectory b = new Trajectory(2);
            b.addPoint(Point2d.one());
            b.addPoint(Point2d.one());
            b.addPoint(Point2d.one());
            b.addPoint(Point2d.one());
            b.addPoint(Point2d.one());
            b.addPoint(Point2d.one());
            b.addPoint(Point2d.one());
            b.markClosed();

            // lower index is the one of the later starting trajectory
            int lower_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
            assertEquals(2, lower_idx);

            // upper index is the one of the earlier ending trajectory
            int upper_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);
            assertEquals(6, upper_idx);

            // The overlap count is as long as expected
            int overlapCount = nullHelper.p_overlappingFrameCount(lower_idx, upper_idx);
            assertEquals(5, overlapCount);

            // The overlap is as long as minimally expected
            assertTrue(overlapCount >= nullHelper.minExpectedTrajectoryLength());

            // shouldn't be too short, since it has a overlap,
            // which is longer than the min. expected overlap length.
            assertFalse(nullHelper.p_isTooShortOverlapping(a, b, overlapCount));
        }
    }

    @Test
    public void testIsTooShortOverlappingIsOverlappingAfterExtendingIt() {
        // Tra a, not continued, living in frame indices:
        // 0 1 2 3 4 ,5] 6 7 8
        MetaDataManager.getInstance().setFrameCount(20);
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.markClosed();

        // Tra b, not continued, living in frame indices:
        // 5 6 7 [8, 10 11 12 13 14] 15 16 17
        Trajectory b = new Trajectory(8);
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        // lower index is the one of the later starting trajectory
        int lower_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        assertEquals(5, lower_idx);

        // upper index is the one of the earlier ending trajectory
        int upper_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);
        assertEquals(8, upper_idx);

        // The overlap count is as long as expected: overlap in frames 5,6,7,8
        int overlapCount = nullHelper.p_overlappingFrameCount(lower_idx, upper_idx);
        assertEquals(4, overlapCount);

        // The overlap is as long as minimally expected
        assertTrue(overlapCount >= nullHelper.minExpectedTrajectoryLength());

        // shouldn't be too short, since it has a overlap,
        // which is longer than the min. expected overlap length.
        assertFalse(nullHelper.p_isTooShortOverlapping(a, b, overlapCount));
    }

    @Test
    public void testIsTooShortOverlappingIsNotOverlapping() {
        // Tra a, not continued, living in frame indices:
        // 0 1 2 3 4 5
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.markClosed();

        // Tra b, not continued, living in frame indices:
        // 8, 10 11 12 13 14
        Trajectory b = new Trajectory(8);
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.addPoint(Point2d.one());
        b.markClosed();

        // lower index is the one of the later starting trajectory
        int lower_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        assertEquals(8, lower_idx);

        // upper index is the one of the earlier ending trajectory
        int upper_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);
        assertEquals(5, upper_idx);

        // The overlap count is as long as expected
        int overlapCount = nullHelper.p_overlappingFrameCount(lower_idx, upper_idx);
        assertEquals(-2, overlapCount);

        // The overlap is as long as minimally expected
        assertFalse(overlapCount >= nullHelper.minExpectedTrajectoryLength());

        // shouldn't be too short, since it has a overlap,
        // which is longer than the min. expected overlap length.
        assertTrue(nullHelper.p_isTooShortOverlapping(a, b, overlapCount));
    }

    @Test
    public void testHasOverlapWithoutContinuationNoExpWithOverlap() {
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        a.addPoint(Point2d.one());
        a.markClosed();
        Trajectory b = new Trajectory(1);
        b.addPoint(Point2d.one());
        b.markClosed();
        nullHelper.p_hasOverlapWithoutContinuation(a, b);
        assertTrue(nullHelper.p_hasOverlapWithoutContinuation(a, b));
    }

    @Test
    public void testHasOverlapWithoutContinuationNoExpWithoutOverlap() {
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        a.markClosed();
        Trajectory b = new Trajectory(1);
        b.addPoint(Point2d.one());
        b.markClosed();
        nullHelper.p_hasOverlapWithoutContinuation(a, b);
        assertFalse(nullHelper.p_hasOverlapWithoutContinuation(a, b));
    }

    @Test
    public void testHasOverlapWithoutContinuationExpWithoutInitialOverlap() {
        MetaDataManager.getInstance().setFrameCount(20);
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        a.markClosed();
        Trajectory b = new Trajectory(1);
        b.addPoint(Point2d.one());
        b.markClosed();
        a.extendPointTracking();
        b.extendPointTracking();

        nullHelper.p_hasOverlapWithoutContinuation(a, b);
        assertFalse(nullHelper.p_hasOverlapWithoutContinuation(a, b));
        int from_idx = nullHelper.p_getLowerFrameIndexBetween(a, b);
        int to_idx = nullHelper.p_getUpperFrameIndexBetween(a, b);
        int overlapCount = nullHelper.p_overlappingFrameCount(from_idx, to_idx);
        assertTrue(overlapCount > 0);
    }

    @Test
    public void testForward_differenceDtOneSimpleCase() {
        double eps = 1e-9;
        Trajectory a = new Trajectory(0);
        a.addPoint(new Point2d(2, 1));
        a.addPoint(new Point2d(0.5, 2));
        a.addPoint(new Point2d(3.5, 2.3));
        a.addPoint(new Point2d(4.5, 1.3));

        Point2d d = nullHelper.p_forward_difference(a, 1, 0);
        assertEquals(-1.5, d.x(), eps);
        assertEquals(1, d.y(), eps);

        d = nullHelper.p_forward_difference(a, 1, 1);
        assertEquals(3, d.x(), eps);
        assertEquals(0.3, d.y(), eps);

        d = nullHelper.p_forward_difference(a, 1, 2);
        assertEquals(1, d.x(), eps);
        assertEquals(-1, d.y(), eps);
    }

    @Test
    public void testForward_differenceDtTwoSimpleCase() {
        double eps = 1e-9;
        Trajectory a = new Trajectory(0);
        a.addPoint(new Point2d(2, 1));
        a.addPoint(new Point2d(0.5, 2));
        a.addPoint(new Point2d(3.5, 2.3));
        a.addPoint(new Point2d(4.5, 1.3));

        // (0.75, 0.65) = [(3.5,2.3)-(2,1)] / 2
        Point2d d = nullHelper.p_forward_difference(a, 2, 0);
        assertEquals(0.75, d.x(), eps);
        assertEquals(0.65, d.y(), eps);

        // (2, -0.35) = [(4.5,1.3)-(0.5,2)] / 2
        d = nullHelper.p_forward_difference(a, 2, 1);
        assertEquals(2, d.x(), eps);
        assertEquals(-0.35, d.y(), eps);
    }

    @Test
    public void testForward_differenceDtThreeSimpleCase() {
        double eps = 1e-9;
        Trajectory a = new Trajectory(0);
        a.addPoint(new Point2d(2, 1));
        a.addPoint(new Point2d(0.5, 2));
        a.addPoint(new Point2d(3.5, 2.3));
        a.addPoint(new Point2d(4.5, 1.3));

        // [4.5,1.3)-(2,1)] / 3
        Point2d d = nullHelper.p_forward_difference(a, 3, 0);
        assertEquals(2.5/3, d.x(), eps);
        assertEquals(0.3/3, d.y(), eps);
    }

    @Test
    public void testForward_differenceDtOneRandomCase() {
        int N = 10;
        double eps = 1e-9;
        for (int n = 0; n < N; n++) {
            Trajectory a = new Trajectory(0);
            double x0 = Math.random();
            double y0 = Math.random();
            a.addPoint(new Point2d(x0, y0));

            double x1 = Math.random();
            double y1 = Math.random();
            a.addPoint(new Point2d(x1, y1));
            Point2d d = nullHelper.p_forward_difference(a, 1, 0);
            assertEquals(x1 - x0, d.x(), eps);
            assertEquals(y1 - y0, d.y(), eps);
        }
    }

    @Test
    public void testSpatialDistBetween() {
        double eps = 1e-9;
        Trajectory a = new Trajectory(0);
        a.addPoint(new Point2d(6, 4));
        a.addPoint(new Point2d(1, 3));

        Trajectory b = new Trajectory(0);
        b.addPoint(new Point2d(3, 8));
        b.addPoint(new Point2d(1, 6));

        // sqrt((3-6)^2 + (8-4)^2) = 5
        assertEquals(5, nullHelper.p_spatialDistBetween(a, b, 0), eps);
        assertEquals(3, nullHelper.p_spatialDistBetween(a, b, 1), eps);
    }

    @Test
    public void testSpatialDistBetweenRandomCase() {
        double eps = 1e-9;
        int N = 10;
        for (int n = 0; n < N; n++) {

            Trajectory a = new Trajectory(0);
            double x0 = Math.random();
            double y0 = Math.random();
            a.addPoint(new Point2d(x0, y0));

            Trajectory b = new Trajectory(0);
            double x1 = Math.random();
            double y1 = Math.random();
            b.addPoint(new Point2d(x1, y1));

            double d = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
            assertEquals(d, nullHelper.p_spatialDistBetween(a, b, 0), eps);
        }
    }

    @Test
    public void testTrajectoryPointsInvalid() {
        Point2d pa1 = new Point2d(1, 1);
        Point2d pa0 = new Point2d(1, 1);
        Point2d pb1 = new Point2d(1, 1);
        Point2d pb0 = new Point2d(1, 1);

        pa0.markAsInvalid();
        pb0.markAsInvalid();

        assertTrue(nullHelper.p_trajectoryPointsInvalid(pa0, pb0));
        assertTrue(nullHelper.p_trajectoryPointsInvalid(pa0, pb1));
        assertTrue(nullHelper.p_trajectoryPointsInvalid(pa1, pb0));
        assertFalse(nullHelper.p_trajectoryPointsInvalid(pa1, pb1));
    }

    @Test
    public void testAppendAvgSpatialDistances() {
        Trajectory a = new Trajectory(0);
        Trajectory b = new Trajectory(0);
        Trajectory c = new Trajectory(0);

        a.markClosed();
        b.markClosed();

        nullHelper.p_appendAvgSpatialDistances(a, b, 3.14);
        nullHelper.p_appendAvgSpatialDistances(a, c, 2.14);
        nullHelper.p_appendAvgSpatialDistances(b, c, 1.14);

        TreeMap<Integer, Double> neighborsA = getNeighborsHashFor(a);
        TreeMap<Integer, Double> neighborsB = getNeighborsHashFor(b);
        TreeMap<Integer, Double> neighborsC = getNeighborsHashFor(c);

        // check assignments of nearest avg spatial neighbor value assignments
        // also considers the correct order of assignment
        assertEquals(3.14, neighborsA.values().toArray()[0]);
        assertEquals(2.14, neighborsA.values().toArray()[1]);

        assertEquals(3.14, neighborsB.values().toArray()[0]);
        assertEquals(1.14, neighborsB.values().toArray()[1]);

        assertEquals(2.14, neighborsC.values().toArray()[0]);
        assertEquals(1.14, neighborsC.values().toArray()[1]);

        // Test order of key assignments
        assertEquals(b.getLabel(), neighborsA.keySet().toArray()[0]);
        assertEquals(c.getLabel(), neighborsA.keySet().toArray()[1]);

        assertEquals(a.getLabel(), neighborsB.keySet().toArray()[0]);
        assertEquals(c.getLabel(), neighborsB.keySet().toArray()[1]);

        assertEquals(a.getLabel(), neighborsC.keySet().toArray()[0]);
        assertEquals(b.getLabel(), neighborsC.keySet().toArray()[1]);

    }

    @Test
    public void testForward_difference3d() {
        new CalibrationsReader("foobar", "./testdata/");
        new DepthFieldReader("foobar", "1", "./testdata/");
        new DepthFieldReader("foobar", "2", "./testdata/");

        Trajectory a = new Trajectory(0);
        a.addPoint(new Point2d(0, 0));
        a.addPoint(new Point2d(0.1, 0.1));
        a.markClosed();

        HashMap<Integer, Trajectory> trajectories = new HashMap<>();
        trajectories.put(a.getLabel(), a);
        setTrajectoryManagerTrajectories(trajectories);

        TrajectoryManager.getInstance().transformTrajectoryPointsToEuclidianSpace();

        Point3d d = nullHelper.p_forward_difference3d(a, 1, 0);

        Point3d p0 = a.getEuclidPositionAtFrame(0);
        Point3d p1 = a.getEuclidPositionAtFrame(1);

        Point3d gt_d = p1.copy().sub(p0);

        assertEquals(gt_d.x(), d.x(), 0);
        assertEquals(gt_d.y(), d.y(), 0);
        assertEquals(gt_d.z(), d.z(), 0);
    }

    @Test
    public void testRun() {
        Trajectory a = new Trajectory(0);
        a.addPoint(Point2d.one());
        Trajectory b = new Trajectory(0);
        b.addPoint(Point2d.one());
        a.markClosed();
        b.markClosed();
        HashMap<Integer, Trajectory> trajectories = new HashMap<>();
        trajectories.put(a.getLabel(), a);
        trajectories.put(b.getLabel(), b);
        setTrajectoryManagerTrajectories(trajectories);
        new SimilarityTaskHelper(a, TrajectoryManager.getTrajectories()).run();
        new SimilarityTaskHelper(b, TrajectoryManager.getTrajectories()).run();
        Object[] tras = TrajectoryManager.getTrajectories().toArray();
        Trajectory t1 = (Trajectory)tras[0];
        Trajectory t2 = (Trajectory)tras[1];
        assertTrue(t1.toSimilarityString().equals("[1.0, 1.0]"));
        assertTrue(t2.toSimilarityString().equals("[1.0, 1.0]"));
    }
}

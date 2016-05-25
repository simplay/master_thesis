import datastructures.Point2d;
import datastructures.Trajectory;
import junit.framework.Assert;
import managers.MetaDataManager;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import similarity.SimilarityTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
    }

    @Before
    public void prepare() {
        ArgParser.release();
        TrajectoryManager.release();
        MetaDataManager.release();

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

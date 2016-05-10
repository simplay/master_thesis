import datastructures.Point2d;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrajectoryTest {

    @Before
    public void initObjects() {
        TrajectoryManager.release();
    }

    @Test
    public void testCreationWorks() {
        for (int k = 0; k < 10; k++) {
            int startFrame = ((int)Math.random()*10);
            Trajectory tra = new Trajectory(startFrame);
            assertEquals(tra.getLabel(), k+1);
            assertEquals(tra.getStartFrame(), startFrame);
        }
    }

    @Test
    public void testClosable() {
        Trajectory tra = new Trajectory(0);
        assertEquals(false, tra.isClosed());
        tra.markClosed();
        assertEquals(true, tra.isClosed());
    }

    @Test
    public void testDeletable() {
        Trajectory tra = new Trajectory(0);
        assertEquals(false, tra.isDeletable());
        tra.markAsDeletable();
        assertEquals(true, tra.isDeletable());
    }

    @Test
    public void testTrajectoryFrameBoundaries() {
        int startFrame = (int)(Math.random()*10);
        int tilIdx = (int)(Math.random()*10);

        Trajectory tra = new Trajectory(startFrame);

        for (int k = 0; k < tilIdx; k++) {
            tra.addPoint(new Point2d(0, 6));
        }
        tra.markClosed();
        int endFrameIdx = tra.length() + startFrame;

        assertEquals(startFrame, tra.getStartFrame());
        assertEquals(endFrameIdx, tra.getEndFrame());
    }

    @Test
    public void test() {

    }
}

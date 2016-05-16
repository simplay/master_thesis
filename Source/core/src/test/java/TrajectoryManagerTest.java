import datastructures.Point2d;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TrajectoryManagerTest {

    @Before
    public void initObjects() {
        TrajectoryManager.release();
    }

    @Test
    public void testTrajectoryCount() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        TrajectoryManager.getInstance().appendPointTo(tras.get(0).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(2).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(2).getLabel(), new Point2d(0,0));
        assertEquals(4, TrajectoryManager.getInstance().trajectoryCount());
    }

    @Test
    public void testGetAndSetTrajectories() {
        int N = 10;
        for (int k = 0; k < N; k++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        }

        for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(0)) {
            assertTrue(TrajectoryManager.getTrajectories().contains(tra));
        }
    }

    @Test
    public void testGetGetTrajectorySubset() {
        int N = 400;
        for (int k = 0; k < N; k++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        }
        Object[] tras = TrajectoryManager.getTrajectories().toArray();

        for (int n = 0; n < N; n++) {
            List<Trajectory> subset = TrajectoryManager.getTrajectorySubset(n);
            int idx = 0;
            for (Trajectory tra : subset) {
                // index shift idx: idx + n, since we use the last n trajectories
                assertEquals(tra, tras[idx + n]);
                idx++;
            }
        }


    }
}

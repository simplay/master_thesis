import datastructures.*;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.TrajectoryManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TrajectoryManagerTest {

    @Before
    public void initObjects() {
        TrajectoryManager.release();
    }

    @Test
    public void testTrajectoriesAreSortedAscByTheirLabelValue() {
        int N = 500;
        for (int k = 0; k < N; k++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        }

        int prevLabel = -1;
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            assertTrue(tra.getLabel() > prevLabel);
            prevLabel = tra.getLabel();
        }
    }

    @Test
    public void testIterator() {
        int N = 500;
        for (int k = 0; k < N; k++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        }

        int prevLabel = -1;
        int count = 0;
        Iterator<Trajectory> iter = TrajectoryManager.getInstance().iterator();
        while (iter.hasNext()) {
            Trajectory tra = iter.next();
            assertTrue(tra.getLabel() > prevLabel);
            count++;
        }
        assertEquals(N, count);
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

    @Test
    public void testRelease() {
        int N = 10;
        for (int k = 0; k < N; k++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        }

        assertEquals(N, TrajectoryManager.getInstance().trajectoryCount());
        TrajectoryManager.release();
        assertEquals(0, TrajectoryManager.getInstance().trajectoryCount());
    }

    @Test
    public void testGetActivesForFrame() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=1
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=4
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=5
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=6
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 2); // l=7
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=8
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=9
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=10

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        TrajectoryManager.getInstance().appendPointTo(tras.get(0).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(4).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(8).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(8).getLabel(), new Point2d(0,0));

        tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        // close last trajectory explicit
        tras.get(9).markClosed();

        assertEquals(10, TrajectoryManager.getInstance().trajectoryCount());
        assertEquals(10, tras.size());

        // check if activity is currectly computed
        assertEquals(1, tras.get(0).currentActiveFrame());
        assertEquals(0, tras.get(1).currentActiveFrame());
        assertEquals(0, tras.get(2).currentActiveFrame());
        assertEquals(0, tras.get(3).currentActiveFrame());
        assertEquals(2, tras.get(4).currentActiveFrame());
        assertEquals(1, tras.get(5).currentActiveFrame());
        assertEquals(2, tras.get(6).currentActiveFrame());
        assertEquals(3, tras.get(7).currentActiveFrame());
        assertEquals(5, tras.get(8).currentActiveFrame());
        assertEquals(3, tras.get(9).currentActiveFrame());

        LinkedList<Trajectory> activeInF = TrajectoryManager.getInstance().getActivesForFrame(0);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(true, activeInF.contains(tras.get(1)));
        assertEquals(true, activeInF.contains(tras.get(2)));
        assertEquals(true, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().getActivesForFrame(1);
        assertEquals(true, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(true, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().getActivesForFrame(2);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(true, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(true, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().getActivesForFrame(3);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(true, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().getActivesForFrame(4);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().getActivesForFrame(5);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(true, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));
    }

    @Test
    public void testAllTrajectoriesActiveInGivenFrame() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=1
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=4
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=5
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=6
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 2); // l=7
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=8
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=9
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=10

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        TrajectoryManager.getInstance().appendPointTo(tras.get(0).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(4).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(8).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(8).getLabel(), new Point2d(0,0));

        tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        // close last trajectory explicit
        tras.get(9).markClosed();

        assertEquals(10, TrajectoryManager.getInstance().trajectoryCount());
        assertEquals(10, tras.size());

        LinkedList<Trajectory> activeInF = TrajectoryManager.getInstance().allTrajectoriesActiveInGivenFrame(0);
        assertEquals(true, activeInF.contains(tras.get(0)));
        assertEquals(true, activeInF.contains(tras.get(1)));
        assertEquals(true, activeInF.contains(tras.get(2)));
        assertEquals(true, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().allTrajectoriesActiveInGivenFrame(1);
        assertEquals(true, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(true, activeInF.contains(tras.get(4)));
        assertEquals(true, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().allTrajectoriesActiveInGivenFrame(2);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(true, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(true, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(false, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().allTrajectoriesActiveInGivenFrame(3);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(true, activeInF.contains(tras.get(7)));
        assertEquals(true, activeInF.contains(tras.get(8)));
        assertEquals(true, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().allTrajectoriesActiveInGivenFrame(4);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(true, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));

        activeInF = TrajectoryManager.getInstance().allTrajectoriesActiveInGivenFrame(5);
        assertEquals(false, activeInF.contains(tras.get(0)));
        assertEquals(false, activeInF.contains(tras.get(1)));
        assertEquals(false, activeInF.contains(tras.get(2)));
        assertEquals(false, activeInF.contains(tras.get(3)));
        assertEquals(false, activeInF.contains(tras.get(4)));
        assertEquals(false, activeInF.contains(tras.get(5)));
        assertEquals(false, activeInF.contains(tras.get(6)));
        assertEquals(false, activeInF.contains(tras.get(7)));
        assertEquals(true, activeInF.contains(tras.get(8)));
        assertEquals(false, activeInF.contains(tras.get(9)));
    }

    @Test
    public void testPrepareForSimilarityCompuation() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=1
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=4

        // Test call to see that it does not crash.
        TrajectoryManager.prepareForSimilarityCompuation();
        assertEquals(4, TrajectoryManager.getTrajectories().size());
    }

    @Test
    public void testAllTrajectoryWithLength() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=1
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=4
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=5

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        TrajectoryManager.getInstance().appendPointTo(tras.get(0).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(2).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(4).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(4).getLabel(), new Point2d(0,0));

        LinkedList<Trajectory> len0 = TrajectoryManager.getInstance().allTrajectoryWithLength(0);
        assertEquals(false, len0.contains(tras.get(0)));
        assertEquals(true, len0.contains(tras.get(1)));
        assertEquals(false, len0.contains(tras.get(2)));
        assertEquals(true, len0.contains(tras.get(3)));
        assertEquals(false, len0.contains(tras.get(4)));

        LinkedList<Trajectory> len1 = TrajectoryManager.getInstance().allTrajectoryWithLength(1);
        assertEquals(true, len1.contains(tras.get(0)));
        assertEquals(false, len1.contains(tras.get(1)));
        assertEquals(true, len1.contains(tras.get(2)));
        assertEquals(false, len1.contains(tras.get(3)));
        assertEquals(false, len1.contains(tras.get(4)));

        LinkedList<Trajectory> len2 = TrajectoryManager.getInstance().allTrajectoryWithLength(2);
        assertEquals(false, len2.contains(tras.get(0)));
        assertEquals(false, len2.contains(tras.get(1)));
        assertEquals(false, len2.contains(tras.get(2)));
        assertEquals(false, len2.contains(tras.get(3)));
        assertEquals(true, len2.contains(tras.get(4)));
    }

    @Test
    public void testAllTrajectoriesStartingAt() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=1
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 2); // l=4
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=5
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 2); // l=6
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 3); // l=7

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        LinkedList<Trajectory> trasAtF0 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(0);
        assertEquals(true, trasAtF0.contains(tras.get(1)));
        assertEquals(true, trasAtF0.contains(tras.get(2)));

        LinkedList<Trajectory> trasAtF1 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(1);
        assertEquals(true, trasAtF1.contains(tras.get(0)));

        LinkedList<Trajectory> trasAtF2 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(2);
        assertEquals(true, trasAtF2.contains(tras.get(3)));
        assertEquals(true, trasAtF2.contains(tras.get(5)));

        LinkedList<Trajectory> trasAtF3 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(3);
        assertEquals(true, trasAtF3.contains(tras.get(4)));
        assertEquals(true, trasAtF3.contains(tras.get(6)));
    }

    @Test
    public void testFilterOnePointedTrajectories() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=1
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 2); // l=4

        ArrayList<Trajectory> tras= new ArrayList<>(TrajectoryManager.getTrajectories());

        TrajectoryManager.getInstance().appendPointTo(tras.get(1).getLabel(), new Point2d(0,0));
        TrajectoryManager.getInstance().appendPointTo(tras.get(3).getLabel(), new Point2d(0,0));
        tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        assertEquals(4, tras.size());
        TrajectoryManager.getInstance().filterOnePointedTrajectories();

        ArrayList<Trajectory> trasAfterFiltering = new ArrayList<>(TrajectoryManager.getTrajectories());

        assertEquals(2, trasAfterFiltering.size());
        assertEquals(false, trasAfterFiltering.contains(tras.get(0)));
        assertEquals(true, trasAfterFiltering.contains(tras.get(1)));
        assertEquals(false, trasAfterFiltering.contains(tras.get(2)));
        assertEquals(true, trasAfterFiltering.contains(tras.get(3)));
    }

    @Test
    public void testFilterTrajectoriesShorterThanMinLen() {
        int N = 4;
        for (int n = 0; n < N; n++) {
            // Is 1 pointed
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 1); // l=1

            // has length 1
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=2

            // has length 2
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0); // l=3

            // has length 3
            TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 2); // l=4

            ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

            TrajectoryManager.getInstance().appendPointTo(tras.get(1).getLabel(), new Point2d(0, 0));
            TrajectoryManager.getInstance().appendPointTo(tras.get(2).getLabel(), new Point2d(0, 0));
            TrajectoryManager.getInstance().appendPointTo(tras.get(2).getLabel(), new Point2d(0, 0));
            TrajectoryManager.getInstance().appendPointTo(tras.get(3).getLabel(), new Point2d(0, 0));
            TrajectoryManager.getInstance().appendPointTo(tras.get(3).getLabel(), new Point2d(0, 0));
            TrajectoryManager.getInstance().appendPointTo(tras.get(3).getLabel(), new Point2d(0, 0));

            tras = new ArrayList<>(TrajectoryManager.getTrajectories());
            assertEquals(4, tras.size());

            TrajectoryManager.getInstance().filterTrajectoriesShorterThanMinLen(n);
            ArrayList<Trajectory> trasFiltered = new ArrayList<>(TrajectoryManager.getTrajectories());
            for (int k = 0; k < trasFiltered.size(); k++) {
                assertEquals(true, tras.contains(trasFiltered.get(k)));
            }
            TrajectoryManager.release();
        }
    }

    @Test
    public void testFilterNoSimilarityTrajectories() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());
        assertEquals(3, tras.size());
        tras.get(0).assignSimilarityValueTo(tras.get(1).getLabel(), 1);
        tras.get(2).assignSimilarityValueTo(tras.get(1).getLabel(), 1);
        TrajectoryManager.getInstance().filterNoSimilarityTrajectories();
        ArrayList<Trajectory> trasFiltered = new ArrayList<>(TrajectoryManager.getTrajectories());

        assertEquals(2, trasFiltered.size());
        assertEquals(true, tras.contains(trasFiltered.get(0)));
        assertEquals(true, tras.contains(trasFiltered.get(1)));
        assertEquals(false, trasFiltered.contains(tras.get(1)));
    }

    @Test
    public void testFilterDeletableTrajectories() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());

        tras.get(0).markAsDeletable();
        tras.get(2).markAsDeletable();

        TrajectoryManager.getInstance().filterDeletableTrajectories();
        ArrayList<Trajectory> trasFiltered = new ArrayList<>(TrajectoryManager.getTrajectories());

        assertEquals(1, trasFiltered.size());
        assertEquals(true, tras.contains(trasFiltered.get(0)));
        assertEquals(false, trasFiltered.contains(tras.get(0)));
        assertEquals(false, trasFiltered.contains(tras.get(2)));
    }

    @Test
    public void testTransformTrajectoryPointsToEuclidianSpace() {
        CalibrationManager.release();
        DepthManager.release();

        // Assign random focal lengths for the depth camera
        double f_x = 200d*Math.random();
        double f_y = 200d*Math.random();

        // Assign a random principal point for the depth camera
        double p_x = 150d*Math.random();
        double p_y = 150d*Math.random();

        ArrayList<LabeledFileLine> lfl = new ArrayList<>();

        // Set Calibration data
        lfl.add(new LabeledFileLine("f_d", f_x + " " + f_y));
        lfl.add(new LabeledFileLine("p_d", p_x + " " + p_y));
        LabeledFile lf = new LabeledFile(lfl);
        CalibrationManager.getInstance(lf);

        // All trajectories have length K and there are K depth fields
        int K = 3;

        // Each depth field is of resolution [X x Y].
        int X = 2; int Y = 2;

        // Save depth fields
        LinkedList<DepthField> dfs = new LinkedList<>();
        for (int k = 0; k < K; k++) {
            dfs.add(new DepthField(X, Y));
        }

        // Init important storage datastructures.
        LinkedList<Double> xs = new LinkedList<>();
        LinkedList<Double> ys = new LinkedList<>();
        LinkedList<Double> ds = new LinkedList<>();

        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);

        ArrayList<Trajectory> tras = new ArrayList<>(TrajectoryManager.getTrajectories());


        for (double x = 0.0; x < X; x++) {
            for (double y = 0.0; y < Y; y++) {
                xs.add(x);
                ys.add(y);
                double depth = 100 * Math.random();
                ds.add(depth);
            }
        }

        // assign depth fields and trajectories
        // Iterate over grid resolution
        for (int k = 0; k < K; k++) {
            int idx = 0;
            for (double x = 0.0; x < X; x++) {
                for (double y = 0.0; y < Y; y++) {
                    if (k > 0) {
                        Point2d p = new Point2d(x, y);
                        Trajectory tra = tras.get(idx);
                        TrajectoryManager.getInstance().appendPointTo(tra.getLabel(), p);
                    }

                    // Each position in the depth map has a random depth value assigned
                    double depth = ds.get(idx);

                    // Iterate over all trajectories
                    dfs.get(k).setAt((int) x, (int) y, depth);
                    idx++;
                }
            }
        }

        // Save depth maps in managers
        for (DepthField df : dfs) {
            DepthManager.getInstance().add(df);
        }

        for (Trajectory tra : tras) {
            tra.markClosed();
        }

        TrajectoryManager.getInstance().transformTrajectoryPointsToEuclidianSpace();

        // Test if transformed points are computed correctly,
        // by making use of depth cues and camera calibration data.
        for (int idx = 0; idx < 1; idx++) {
            double x = xs.get(idx);
            double y = ys.get(idx);
            double depth = ds.get(idx);
            for (int k = 0; k < K; k++) {
                Point3d p3 = tras.get(idx).getEuclidPositionAtFrame(k);
                Assert.assertEquals(depth*((y-p_x)/f_x), p3.y(), 0);
                Assert.assertEquals(depth*((x-p_y)/f_y), p3.x(), 0);
                Assert.assertEquals(depth, p3.z(), 0);
            }
        }
    }

    @Test
    public void testToFramewiseOutputString() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(0, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(2, 3), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(4, 5), 1);

        assertEquals("1 0.0 1.0\n2 2.0 3.0", TrajectoryManager.getInstance().toFramewiseOutputString(0));
        assertEquals("3 4.0 5.0", TrajectoryManager.getInstance().toFramewiseOutputString(1));
    }

    @Test
    public void testToFramewiseOutputStringIsOrderedAccordingtoTrajectoryLabel() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 1);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(3, 4), 1);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(5, 6), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(7, 8), 1);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(9, 10), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(11, 12), 0);

        assertEquals("3 5.0 6.0\n5 9.0 10.0\n6 11.0 12.0", TrajectoryManager.getInstance().toFramewiseOutputString(0));
        assertEquals("1 1.0 2.0\n2 3.0 4.0\n4 7.0 8.0", TrajectoryManager.getInstance().toFramewiseOutputString(1));
    }

}

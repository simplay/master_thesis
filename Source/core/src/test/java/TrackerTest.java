import datastructures.TrackingCandidates;
import datastructures.Trajectory;
import managers.FlowFieldManager;
import managers.InvalidRegionManager;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import readers.CandidateFileReader;
import readers.FlowFileReader;
import readers.InvalidRegionReader;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackerTest {

    @Before
    public void prepare() {
        FlowFieldManager.release();
        ArgParser.release();
        TrackingCandidates.release();
        InvalidRegionManager.release();
        TrajectoryManager.release();

        String[] args = {"-d", "dummy", "-task", "2"};
        ArgParser.getInstance(args);

        new FlowFileReader("dummy", "fw", "1", "./testdata/");
        new FlowFileReader("dummy", "bw", "1", "./testdata/");
        new CandidateFileReader("dummy", "1", "./testdata/");
        new InvalidRegionReader("dummy", "1", "./testdata/");
    }

    @Test
    public void testTackingOneFrame() {
        new Tracker(1, 1);

        int[] gtLen0TraLabels = {3, 5, 6, 7, 8};
        int[] gtLen1TraLabels = {1, 2, 4};

        int[] gtstartingAtFrame0 = {1, 2, 3, 4, 5, 6, 7, 8};

        LinkedList<Trajectory> len0Tras = TrajectoryManager.getInstance().allTrajectoryWithLength(0);
        LinkedList<Integer> trackedLen0TraLabels = new LinkedList<>();
        for (Trajectory tra : len0Tras) {
            trackedLen0TraLabels.add(tra.getLabel());
        }

        for (Integer label : gtLen0TraLabels) {
            assertTrue(trackedLen0TraLabels.contains(label));
        }
        assertEquals(5, len0Tras.size());

        LinkedList<Trajectory> len1Tras = TrajectoryManager.getInstance().allTrajectoryWithLength(1);
        LinkedList<Integer> trackedLen1TraLabels = new LinkedList<>();
        for (Trajectory tra : len1Tras) {
            trackedLen1TraLabels.add(tra.getLabel());
        }

        for (Integer label : gtLen1TraLabels) {
            assertTrue(trackedLen1TraLabels.contains(label));
        }
        assertEquals(3, len1Tras.size());

        assertEquals(8, TrajectoryManager.getTrajectories().size());



        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            tra.markClosed();
        }

        LinkedList<Trajectory> startingAtF0 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(0);

        LinkedList<Integer> extrStartingAtF0 = new LinkedList<>();
        for (Trajectory tra : startingAtF0) {
            extrStartingAtF0.add(tra.getLabel());
        }
        for (Integer label : gtstartingAtFrame0) {
            assertTrue(extrStartingAtF0.contains(label));
        }
    }

    @Test
    public void testTackingTwoFrames() {
        new FlowFileReader("dummy", "fw", "2", "./testdata/");
        new FlowFileReader("dummy", "bw", "2", "./testdata/");
        new CandidateFileReader("dummy", "2", "./testdata/");
        new InvalidRegionReader("dummy", "2", "./testdata/");

        new Tracker(2, 1);

        int[] gtLen0TraLabels = {3, 5, 6, 7, 8};
        int[] gtLen1TraLabels = {2, 4};
        int[] gtLen2TraLabels = {1};

        int[] gtstartingAtFrame0 = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] gtstartingAtFrame1 = {9};

        LinkedList<Trajectory> len0Tras = TrajectoryManager.getInstance().allTrajectoryWithLength(0);
        LinkedList<Integer> trackedLen0TraLabels = new LinkedList<>();
        for (Trajectory tra : len0Tras) {
            trackedLen0TraLabels.add(tra.getLabel());
        }

        for (Integer label : gtLen0TraLabels) {
            assertTrue(trackedLen0TraLabels.contains(label));
        }
        assertEquals(5, len0Tras.size());

        LinkedList<Trajectory> len1Tras = TrajectoryManager.getInstance().allTrajectoryWithLength(1);
        LinkedList<Integer> trackedLen1TraLabels = new LinkedList<>();
        for (Trajectory tra : len1Tras) {
            trackedLen1TraLabels.add(tra.getLabel());
        }

        for (Integer label : gtLen1TraLabels) {
            assertTrue(trackedLen1TraLabels.contains(label));
        }
        assertEquals(3, len1Tras.size());

        assertEquals(9, TrajectoryManager.getTrajectories().size());


        LinkedList<Trajectory> len2Tras = TrajectoryManager.getInstance().allTrajectoryWithLength(2);
        LinkedList<Integer> trackedLen2TraLabels = new LinkedList<>();
        for (Trajectory tra : len2Tras) {
            trackedLen2TraLabels.add(tra.getLabel());
        }

        for (Integer label : gtLen2TraLabels) {
            assertTrue(trackedLen2TraLabels.contains(label));
        }
        assertEquals(3, len1Tras.size());


        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            tra.markClosed();
        }

        LinkedList<Trajectory> startingAtF0 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(0);
        LinkedList<Trajectory> startingAtF1 = TrajectoryManager.getInstance().allTrajectoriesStartingAt(1);

        LinkedList<Integer> extrStartingAtF0 = new LinkedList<>();
        for (Trajectory tra : startingAtF0) {
            extrStartingAtF0.add(tra.getLabel());
        }
        for (Integer label : gtstartingAtFrame0) {
            assertTrue(extrStartingAtF0.contains(label));
        }


        LinkedList<Integer> extrStartingAtF1 = new LinkedList<>();
        for (Trajectory tra : startingAtF1) {
            extrStartingAtF1.add(tra.getLabel());
        }
        for (Integer label : gtstartingAtFrame1) {
            assertTrue(extrStartingAtF1.contains(label));
        }

    }
}

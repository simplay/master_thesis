import datastructures.Point2d;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.NearestSpatialNeighborsWriter;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NearestSpatialNeighborsWriterTest {

    private String dataset = "foobar";

    private Trajectory t1;
    private Trajectory t2;
    private Trajectory t3;
    private Trajectory t4;

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        ArgParser.release();

        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);

        LinkedList<Trajectory> tras = TrajectoryManager.getInstance().getActivesForFrame(0);
        t1 = tras.get(0);
        t2 = tras.get(1);
        t3 = tras.get(2);
        t4 = tras.get(3);

        t1.appendAvgSpatialDist(t2.getLabel(), 2);
        t1.appendAvgSpatialDist(t3.getLabel(), 4);
        t1.appendAvgSpatialDist(t4.getLabel(), 1);

        t2.appendAvgSpatialDist(t1.getLabel(), 6);
        t2.appendAvgSpatialDist(t3.getLabel(), 1);
        t2.appendAvgSpatialDist(t4.getLabel(), 3);

        t3.appendAvgSpatialDist(t1.getLabel(), 3.3);
        t3.appendAvgSpatialDist(t2.getLabel(), 3.2);
        t3.appendAvgSpatialDist(t4.getLabel(), 3.25);

        t4.appendAvgSpatialDist(t1.getLabel(), 1.3);
        t4.appendAvgSpatialDist(t2.getLabel(), 2.2);
        t4.appendAvgSpatialDist(t3.getLabel(), 3.25);
    }

    @Test
    public void testFetchesCorrectNearestNeighborsAllNeighbors() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "all", "-nn", "3"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t2.getLabel() + ", " + t3.getLabel() + ", " + t4.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t1.getLabel() + ", " + t3.getLabel() + ", " + t4.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t1.getLabel() + ", " + t2.getLabel() + ", " + t4.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsTopNeighbors() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top", "-nn", "3"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsTopNeighborsNEqaualsFour() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top", "-nn", "4"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsTopNeighborsNEqaualsTwo() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top", "-nn", "2"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t2.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t4.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t4.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t2.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsTopNeighborsNEqaualsOne() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top", "-nn", "1"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + "";
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + "";
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + "";
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + "";
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsBothNEqualsTwo() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both", "-nn", "2"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    /**
     * Edge case: When neighbor could to be returned is exactly the same as available neighbors.
     */
    @Test
    public void testFetchesCorrectNearestNeighborsBothNEqualsThree() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both", "-nn", "3"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsBothNEqualsFour() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both", "-nn", "4"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }

    @Test
    public void testFetchesCorrectNearestNeighborsBothNEqualsSix() {
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both", "-nn", "6"};
        ArgParser.getInstance(args);

        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t4.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t4.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());

        gt = t1.getLabel() + ", " + t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(3).trim());
    }
}

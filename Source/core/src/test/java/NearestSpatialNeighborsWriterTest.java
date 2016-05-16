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

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        String[] args = {"-d", dataset, "-task", "1"};
        ArgParser.getInstance(args);

        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);

        LinkedList<Trajectory> tras = TrajectoryManager.getInstance().getActivesForFrame(0);
        t1 = tras.get(0);
        t2 = tras.get(1);
        t3 = tras.get(2);

        t1.appendAvgSpatialDist(t2.getLabel(), 2);
        t1.appendAvgSpatialDist(t3.getLabel(), 4);

        t2.appendAvgSpatialDist(t1.getLabel(), 6);
        t2.appendAvgSpatialDist(t3.getLabel(), 1);

        t3.appendAvgSpatialDist(t1.getLabel(), 3.3);
        t3.appendAvgSpatialDist(t2.getLabel(), 3.2);
    }

    @Test
    public void testFetchesCorrectNearestNeighbors() {
        List<String> neighbors = new NearestSpatialNeighborsWriter(dataset, "./testdata/").getSerializedTrajectoryNeighbors();

        String gt = t2.getLabel() + ", " + t3.getLabel();
        assertEquals(gt, neighbors.get(0).trim());

        gt = t3.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(1).trim());

        gt = t2.getLabel() + ", " + t1.getLabel();
        assertEquals(gt, neighbors.get(2).trim());
    }
}

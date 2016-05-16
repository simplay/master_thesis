import datastructures.Point2d;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.SimilarityWriter;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SimilarityWriterTest {

    private String dataset = "foobar";

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1"};
        ArgParser.getInstance(args);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);

        LinkedList<Trajectory> tras = TrajectoryManager.getInstance().getActivesForFrame(0);
        Trajectory t1 = tras.get(0);
        Trajectory t2 = tras.get(1);

        t1.assignSimilarityValueTo(t1.getLabel(), -1.1d);
        t1.assignSimilarityValueTo(t2.getLabel(), 2.2d);
        t2.assignSimilarityValueTo(t1.getLabel(), -3.3d);
        t2.assignSimilarityValueTo(t2.getLabel(), 4.4d);
    }

    @Test
    public void testRowsCorrectlyFetched() {
        List<String> rows = new SimilarityWriter(dataset, "./testdata/").getMatrixRows();
        assertEquals("-1.1, 2.2", rows.get(0).trim());
        assertEquals("-3.3, 4.4", rows.get(1).trim());
    }
}

import datastructures.Point2d;
import managers.TrajectoryManager;
import org.junit.Test;
import org.junit.Before;
import pipeline_components.ArgParser;
import writers.LabelMappingWriter;

import static org.junit.Assert.assertEquals;

public class LabelMappingWritterTest {

    private String dataset = "foobar";

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        ArgParser.release();
        String[] args = {"-d", dataset, "-task", "1"};
        ArgParser.getInstance(args);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 0);
    }

    @Test
    public void testLabelsCorrectlyFetched() {
        String labels = new LabelMappingWriter(dataset).getSerializedTrajectoryLabels();
        assertEquals(labels, "1 2 3 4");
    }

}

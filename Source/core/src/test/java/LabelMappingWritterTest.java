import datastructures.Point2d;
import managers.TrajectoryManager;
import org.junit.Test;
import org.junit.Before;
import pipeline_components.ArgParser;
import writers.LabelMappingWriter;

import static org.junit.Assert.assertEquals;

public class LabelMappingWritterTest {

    @Before
    public void initObjects() {
        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1, 2), 0);
    }

    @Test
    public void testFoobar() {
        String pew = new LabelMappingWriter("foobar").getSerializedTrajectoryLabels();
        assertEquals(pew, "1 2 3 4");
    }
}

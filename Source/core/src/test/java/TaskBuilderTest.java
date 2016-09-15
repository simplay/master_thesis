import managers.CalibrationManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.CalibrationsReader;
import similarity.*;

import static org.junit.Assert.assertEquals;

public class TaskBuilderTest {

    @Before
    public void initObjects() {
        ArgParser.release();
        CalibrationManager.release();
        new CalibrationsReader("foobar", "./testdata/");
    }

    @Test
    public void testBuilderTask1() {
        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);
        assertEquals(SumDistTask.class, TaskBuilder.buildTask(ArgParser.getSimTask(), null, null).getClass());
    }

    @Test
    public void testBuilderTask2() {
        String[] args = {"-d", "foobar", "-task", "2"};
        ArgParser.getInstance(args);
        assertEquals(ProdDistTask.class, TaskBuilder.buildTask(ArgParser.getSimTask(), null, null).getClass());
    }

    @Test
    public void testBuilderTask3() {
        String[] args = {"-d", "foobar", "-task", "3"};
        ArgParser.getInstance(args);
        assertEquals(ProdDistEuclidTask.class, TaskBuilder.buildTask(ArgParser.getSimTask(), null, null).getClass());
    }

    @Test
    public void testBuilderTask4() {
        String[] args = {"-d", "foobar", "-task", "4"};
        ArgParser.getInstance(args);
        assertEquals(SumDistEuclidTask.class, TaskBuilder.buildTask(ArgParser.getSimTask(), null, null).getClass());
    }

    @Test
    public void testBuilderTask5() {
        String[] args = {"-d", "foobar", "-task", "5"};
        ArgParser.getInstance(args);
        assertEquals(ProdDistAllEuclidTask.class, TaskBuilder.buildTask(ArgParser.getSimTask(), null, null).getClass());
    }

    @Test
    public void testBuilderTask6() {
        String[] args = {"-d", "foobar", "-task", "6"};
        ArgParser.getInstance(args);
        assertEquals(ProdDistAllEuclidNoDepthVarTask.class, TaskBuilder.buildTask(ArgParser.getSimTask(), null, null).getClass());
    }
}

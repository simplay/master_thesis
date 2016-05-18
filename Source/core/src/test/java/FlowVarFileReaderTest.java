import datastructures.FlowVarField;
import datastructures.Point2d;
import managers.VarianceManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.FlowVarFileReader;

import static org.junit.Assert.assertEquals;

public class FlowVarFileReaderTest {
    @Before
    public void initObjects() {
        VarianceManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "2"};
        ArgParser.getInstance(args);
    }

    @Test
    public void testReadingAFileWorks() {
        new FlowVarFileReader("foobar", "1", "./testdata/");
        FlowVarField var = VarianceManager.getInstance().getVariance(0);

        double d00 = 0.0189008011854665; double d01 = 0.0205003868819295;
        double d10 = 0.0224310498669945; double d11 = 0.0747203415319888;

        assertEquals(d00, var.valueAt(new Point2d(0, 0)), 0);
        assertEquals(d01, var.valueAt(new Point2d(0, 1)), 0);
        assertEquals(d10, var.valueAt(new Point2d(1, 0)), 0);
        assertEquals(d11, var.valueAt(new Point2d(1, 1)), 0);
    }
}

import datastructures.*;
import managers.DepthManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.DepthFieldReader;

import static org.junit.Assert.assertEquals;

public class DepthFieldReaderTest {

    @Before
    public void initObjects() {
        DepthManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "2"};
        ArgParser.getInstance(args);
    }

    @Test
    public void testReadingAFileWorks() {
        new DepthFieldReader("foobar", "1", "./testdata/");
        DepthField df0 = DepthManager.getInstance().get(0);

        double d00 = 2.345; double d01 = 3.12;
        double d10 = 0.23; double d11 = 2.1;

        assertEquals(d00, df0.valueAt(0, 0), 0);
        assertEquals(d01, df0.valueAt(0, 1), 0);
        assertEquals(d10, df0.valueAt(1, 0), 0);
        assertEquals(d11, df0.valueAt(1, 1), 0);
    }
}

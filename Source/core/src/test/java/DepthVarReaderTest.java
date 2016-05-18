import datastructures.DepthVarField;
import managers.DepthVarManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.DepthVarReader;

import static org.junit.Assert.assertEquals;

public class DepthVarReaderTest {
    
    @Before
    public void initObjects() {
        DepthVarManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "2"};
        ArgParser.getInstance(args);
    }

    @Test
    public void testReadingAFileWorks() {
        new DepthVarReader("foobar", "1", "./testdata/");
        DepthVarField df0 = DepthVarManager.getInstance().get(0);

        double d00 = 0.00127473120875028; double d01 = 0.00122642151667226;
        double d10 = 0.00116964862472321; double d11 = 0.00101231656599509;

        assertEquals(d00, df0.valueAt(0, 0), 0);
        assertEquals(d01, df0.valueAt(0, 1), 0);
        assertEquals(d10, df0.valueAt(1, 0), 0);
        assertEquals(d11, df0.valueAt(1, 1), 0);
    }
}

import datastructures.DepthField;
import datastructures.FlowField;
import managers.DepthManager;
import managers.FlowFieldManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.FlowFileReader;

import static org.junit.Assert.assertEquals;

public class FlowFileReaderTest {
    @Before
    public void initObjects() {
        FlowFieldManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "2"};
        ArgParser.getInstance(args);
    }

    @Test
    public void testReadingAFileWorks() {
        new FlowFileReader("foobar", "fw", "1", "./testdata/");
        new FlowFileReader("foobar", "bw", "1", "./testdata/");

        FlowField ff1 = FlowFieldManager.getInstance().getForwardFlow(0);
        FlowField bf1 = FlowFieldManager.getInstance().getBackwardFlow(0);

        double fu00 = 0.0741980075836; double fu01 = 0.0682325437665;
        double fu10 = 0.286125779152; double fu11 = -1.26006686687;

        double fv00 = -0.651358366013; double fv01 =  -0.712285161018;
        double fv10 = -1.35115218163; double fv11 = -4.92138767242;

        double bu00 = 0.00177909294143; double bu01 = 0.00167478993535;
        double bu10 = 0.00347909294343; double bu11 = 0.00344478993535;

        double bv00 = 0.0450411140919; double bv01 = 0.0445248857141;
        double bv10 = 0.0466928370297; double bv11 = 0.163085758686;

        assertEquals(fu00, ff1.u_valueAt(0, 0), 0);
        assertEquals(fu01, ff1.u_valueAt(0, 1), 0);
        assertEquals(fu10, ff1.u_valueAt(1, 0), 0);
        assertEquals(fu11, ff1.u_valueAt(1, 1), 0);

        assertEquals(fv00, ff1.v_valueAt(0, 0), 0);
        assertEquals(fv01, ff1.v_valueAt(0, 1), 0);
        assertEquals(fv10, ff1.v_valueAt(1, 0), 0);
        assertEquals(fv11, ff1.v_valueAt(1, 1), 0);

        assertEquals(bu00, bf1.u_valueAt(0, 0), 0);
        assertEquals(bu01, bf1.u_valueAt(0, 1), 0);
        assertEquals(bu10, bf1.u_valueAt(1, 0), 0);
        assertEquals(bu11, bf1.u_valueAt(1, 1), 0);

        assertEquals(bv00, bf1.v_valueAt(0, 0), 0);
        assertEquals(bv01, bf1.v_valueAt(0, 1), 0);
        assertEquals(bv10, bf1.v_valueAt(1, 0), 0);
        assertEquals(bv11, bf1.v_valueAt(1, 1), 0);
    }
}

import datastructures.Mat3x4;
import managers.CalibrationManager;
import org.junit.Before;
import org.junit.Test;
import readers.CalibrationsReader;
import static org.junit.Assert.assertEquals;

public class CalibrationsReaderTest {

    @Before
    public void initObjects() {
        CalibrationManager.release();
    }

    @Test
    public void testReadingAFileWorks() {
        new CalibrationsReader("foobar", "./testdata/");
        CalibrationManager cm = CalibrationManager.getInstance();
        assertEquals(503.905, cm.getDepthFocalLen().x(), 0);
        assertEquals(504.261, cm.getDepthFocalLen().y(), 0);

        assertEquals(272.202, cm.getDepthPrincipalPoint().x(), 0);
        assertEquals(352.457, cm.getDepthPrincipalPoint().y(), 0);

        assertEquals(574.394, cm.getRgbFocalLen().x(), 0);
        assertEquals(573.71, cm.getRgbFocalLen().y(), 0);

        assertEquals(249.031, cm.getRgbPrincipalPoint().x(), 0);
        assertEquals(346.471, cm.getRgbPrincipalPoint().y(), 0);

        Mat3x4 E = cm.getExtrinsicMat();

        assertEquals(0.999749, E.getCol(0).x(), 0);
        assertEquals(-0.0051649, E.getCol(0).y(), 0);
        assertEquals(-0.0218031, E.getCol(0).z(), 0);

        assertEquals(0.00518867 , E.getCol(1).x(), 0);
        assertEquals(0.999986 , E.getCol(1).y(), 0);
        assertEquals(0.00103363, E.getCol(1).z(), 0);

        assertEquals(0.0217975 , E.getCol(2).x(), 0);
        assertEquals(-0.0011465 , E.getCol(2).y(), 0);
        assertEquals(0.999762, E.getCol(2).z(), 0);

        assertEquals(0.0243073, E.getCol(3).x(), 0);
        assertEquals(-0.000166518, E.getCol(3).y(), 0);
        assertEquals(0.0151706, E.getCol(3).z(), 0);
    }
}

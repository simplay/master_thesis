import datastructures.Mat3x4;
import managers.CalibrationManager;
import org.junit.Before;
import org.junit.Test;
import readers.CalibrationsReader;

import static org.junit.Assert.assertEquals;

public class CalibrationManagerTest {

    @Before
    public void initObjects() {
        CalibrationManager.release();
        new CalibrationsReader("foobar", "./testdata/");
    }

    @Test
    public void testDataLoaded() {
        assertEquals(503.905, CalibrationManager.depth_focal_len().x(), 0);
        assertEquals(504.261, CalibrationManager.depth_focal_len().y(), 0);

        assertEquals(272.202, CalibrationManager.depth_principal_point().x(), 0);
        assertEquals(352.457, CalibrationManager.depth_principal_point().y(), 0);

        assertEquals(574.394, CalibrationManager.rgb_focal_len().x(), 0);
        assertEquals(573.71, CalibrationManager.rgb_focal_len().y(), 0);

        assertEquals(249.031, CalibrationManager.rgb_principal_point().x(), 0);
        assertEquals(346.471, CalibrationManager.rgb_principal_point().y(), 0);

        Mat3x4 E = CalibrationManager.extrinsicMat();

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

    @Test
    public void testShouldWarpDepthFieldsTrueCase() {
        assertEquals(true, CalibrationManager.shouldWarpDepthFields());
    }

    @Test
    public void testShouldWarpDepthFieldsFalseCase() {
        CalibrationManager.release();
        assertEquals(false, CalibrationManager.shouldWarpDepthFields());
    }

    @Test
    public void testHasColorIntrinsicDataLoaded() {
        assertEquals(true, CalibrationManager.getInstance().hasColorIntrinsicDataLoaded());
    }

    @Test
    public void testIsHasDepthIntrinsicDataLoaded() {
        assertEquals(true, CalibrationManager.getInstance().isHasDepthIntrinsicDataLoaded());
    }

    @Test
    public void testHasNoDepthIntrinsicsFalseCase() {
        assertEquals(false, CalibrationManager.hasNoDepthIntrinsics());
    }

    @Test
    public void testHasNoDepthIntrinsicsTrueCase() {
        CalibrationManager.release();
        assertEquals(true, CalibrationManager.hasNoDepthIntrinsics());
    }


}

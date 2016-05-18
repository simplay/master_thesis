import datastructures.DepthField;
import managers.CalibrationManager;
import managers.DepthManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import readers.CalibrationsReader;
import readers.DepthFieldReader;

import static org.junit.Assert.assertEquals;

public class DepthManagerTest {
    
    @Before
    public void initObjects() {
        CalibrationManager.release();
        DepthManager.release();
        ArgParser.release();

        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);

        new DepthFieldReader("foobar", "1", "./testdata/");
        new CalibrationsReader("foobar", "./testdata/");
    }

    @Test
    public void testGet() {
        DepthField df = DepthManager.getInstance().get(0);
        double[][] gtData = new double[2][2];
        gtData[0] = new double[]{2.345d, 3.12d};
        gtData[1] = new double[]{0.23d, 2.1d};
        for (int k = 0; k < gtData.length; k++) {
            for (int l = 0; l < gtData[0].length; l++) {
                assertEquals(gtData[k][l], df.getData()[k][l], 0);
            }
        }
    }

    @Test
    public void testAdd() {
        DepthManager.release();

        int N = 5;
        for (int n = 0; n < N; n++) {
            DepthField df = new DepthField(2, 2);
            DepthManager.getInstance().add(df);
        }
        assertEquals(N, DepthManager.getInstance().length());
    }

    @Test
    public void testSetAt() {
        DepthField df = DepthManager.getInstance().get(0);
        double[][] gtData = new double[2][2];
        gtData[0] = new double[]{2.345d, 3.12d};
        gtData[1] = new double[]{0.23d, 2.1d};
        for (int k = 0; k < gtData.length; k++) {
            for (int l = 0; l < gtData[0].length; l++) {
                assertEquals(gtData[k][l], df.getData()[k][l], 0);
            }
        }

        gtData[0] = new double[]{Math.random(), Math.random()};
        gtData[1] = new double[]{Math.random(), Math.random()};

        DepthField newDf = new DepthField(2, 2);
        newDf.setAt(0, 0, gtData[0][0]);
        newDf.setAt(0, 1, gtData[0][1]);
        newDf.setAt(1, 0, gtData[1][0]);
        newDf.setAt(1, 1, gtData[1][1]);

        DepthManager.getInstance().setAt(newDf, 0);
        df = DepthManager.getInstance().get(0);

        for (int k = 0; k < gtData.length; k++) {
            for (int l = 0; l < gtData[0].length; l++) {
                assertEquals(gtData[k][l], df.getData()[k][l], 0);
            }
        }
    }

    @Test
    public void testWarpDepthFieldsOutOfBoundaryResultsInNullField() {
        DepthManager.warpDepthFields();
        DepthField df = DepthManager.getInstance().get(0);
        assertEquals(0, df.getData()[0][0], 0);
        assertEquals(0, df.getData()[0][1], 0);
        assertEquals(0, df.getData()[1][0], 0);
        assertEquals(0, df.getData()[1][1], 0);
    }
}

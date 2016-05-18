import datastructures.DepthField;
import datastructures.Mat3x4;
import datastructures.Point3d;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.DepthVarManager;
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

    @Test
    public void testWarpDepthFields() {
        DepthManager.release();

        // Build a random depth map
        DepthField df = new DepthField(1000, 1000);
        for (int k = 0; k < df.getData().length; k++) {
            for (int l = 0; l < df.getData()[0].length; l++) {
                df.setAt(k,l, Math.random() + 0.1);
            }
        }
        DepthManager.getInstance().add(df);

        // warp random depth field and fetch warped depth maps
        DepthManager.warpDepthFields();
        DepthField dfWarped = DepthManager.getInstance().get(0);

        // Get calibration data
        Mat3x4 E = CalibrationManager.extrinsicMat();

        double p_x = CalibrationManager.depth_principal_point().x();
        double p_y = CalibrationManager.depth_principal_point().y();
        double f_x = CalibrationManager.depth_focal_len().x();
        double f_y = CalibrationManager.depth_focal_len().y();

        double p_x_rgb = CalibrationManager.rgb_principal_point().x();
        double p_y_rgb = CalibrationManager.rgb_principal_point().y();
        double f_x_rgb = CalibrationManager.rgb_focal_len().x();
        double f_y_rgb = CalibrationManager.rgb_focal_len().y();

        // Compute manually warped depth field
        DepthField gtWarpedDf = new DepthField(df.m(), df.n());
        for (int i = 0; i < df.getData().length; i++) {
            for (int j = 0; j < df.getData()[0].length; j++) {
                double d = df.valueAt(i, j);

                // depths that are zero can be skipped since their warped
                // value is also zero and the warped depth map is by default
                // initialized by the value zero.
                if (d == 0d) continue;

                Point3d p = new Point3d((i-p_x)/f_x, (j-p_y)/f_y, 1d);
                p.scaleBy(d).transformBy(E);
                double d_tilde = p.z();

                // Compute lookup coordinates in warped depth map
                double i_tilde_prime = ((p.x()*f_x_rgb) / d_tilde) + p_x_rgb;
                double j_tilde_prime = ((p.y()*f_y_rgb) / d_tilde) + p_y_rgb;

                int i_tilde = (int) Math.round(i_tilde_prime);
                int j_tilde = (int) Math.round(j_tilde_prime);

                // if i_tilde, j_tilde are within a valid depth field index range.
                if (i_tilde >= 0 && i_tilde < df.m() && j_tilde >= 0 && j_tilde < df.n()) {

                    // takes the front most depth values of the warped depth field
                    if (d_tilde > 0) {
                        double frontmostDepth = gtWarpedDf.valueAt(i_tilde, j_tilde);
                        if (frontmostDepth > 0 && d_tilde < frontmostDepth) {
                            gtWarpedDf.setAt(i_tilde, j_tilde, d_tilde);
                        } else if(frontmostDepth == 0) {
                            gtWarpedDf.setAt(i_tilde, j_tilde, d_tilde);
                        }
                    }
                }

            }
        }

        // Compare value of gt depth field with queried warped depth values.
        for (int i = 0; i < df.getData().length; i++) {
            for (int j = 0; j < df.getData()[0].length; j++) {
                assertEquals(gtWarpedDf.getData()[i][j], dfWarped.getData()[i][j], 0);
            }
        }
     }
}

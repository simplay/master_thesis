import datastructures.Interpolator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class InterpolatorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testInterpolationRetrievesExactSamplesAtFixedIndices() {
        double[][] data = new double[2][2];
        double[] row_1 = {1d, 2d};
        double[] row_2 = {3d, 4d};
        data[0] = row_1;
        data[1] = row_2;
        Interpolator i = new Interpolator();

        assertEquals(1, i.interpolatedValueAt(data, 0, 0), 0.000000000d);
        assertEquals(2, i.interpolatedValueAt(data, 0, 1), 0.000000000d);
        assertEquals(3, i.interpolatedValueAt(data, 1, 0), 0.000000000d);
        assertEquals(4, i.interpolatedValueAt(data, 1, 1), 0.000000000d);
    }

    @Test
    public void testInterpolationCenters() {
        double[][] data = new double[2][2];
        double[] row_1 = {1d, 2d};
        double[] row_2 = {3d, 4d};
        data[0] = row_1;
        data[1] = row_2;
        Interpolator i = new Interpolator();

        // ((1+2)/2 + (3+4)/2) / 2
        assertEquals(2.5d, i.interpolatedValueAt(data, 0.5, 0.5), 0.000000000d);
        assertEquals(1.5d, i.interpolatedValueAt(data, 0.0, 0.5), 0.000000000d);
        assertEquals(2d, i.interpolatedValueAt(data, 0.5, 0.0), 0.000000000d);
    }

    @Test
    public void testInterpolationForRectMatricesWorksAsWell() {
        double[][] data = new double[2][3];
        double[] row_1 = {1d, 2d, 3d};
        double[] row_2 = {4d, 5d, 6d};
        data[0] = row_1;
        data[1] = row_2;
        Interpolator i = new Interpolator();

        // ((1+2)/2 + (3+4)/2) / 2
        assertEquals(3d, i.interpolatedValueAt(data, 0.5, 0.5), 0.000000000d);
        assertEquals(4d, i.interpolatedValueAt(data, 0.5, 1.5), 0.000000000d);
        assertEquals(4.5d, i.interpolatedValueAt(data, 0.5, 2), 0.000000000d);
        assertEquals(1.5d, i.interpolatedValueAt(data, 0.0, 0.5), 0.000000000d);
        assertEquals(2.5d, i.interpolatedValueAt(data, 0.5, 0.0), 0.000000000d);
    }

    @Test
    public void testOutOfBoundaryThrowsException() {
        double[][] data = new double[2][3];
        double[] row_1 = {1d, 2d, 3d};
        double[] row_2 = {4d, 5d, 6d};
        data[0] = row_1;
        data[1] = row_2;
        Interpolator i = new Interpolator();
        exception.expect(ArrayIndexOutOfBoundsException.class);
        exception.expectMessage("Dimensions (m,n)=(2,3) but accessing (x,y)=(1.5,2.0)");
        i.interpolatedValueAt(data, 1.5, 2);
    }

    @Test
    public void testRandomInterpolationPoints() {
        for (int t = 0; t < 10; t++) {
            double[][] data = new double[2][2];
            double[] row_1 = {Math.random(), Math.random()};
            double[] row_2 = {Math.random(), Math.random()};
            data[0] = row_1;
            data[1] = row_2;
            Interpolator i = new Interpolator();

            for (int k = 0; k < 1000; k++) {
                double idx = Math.random(); //0.6d;
                double idy = Math.random();

                // 3 times linear interpolation: f0 + [(f1-f0)/(x1-x0)]*(x-x0)
                // here x0 = 0 and x1 = 1
                // here: for a fixed row, we interpolated along the column, therefore use idy first.
                double u0 = data[0][0] + (data[0][1]-data[0][0])*idy;
                double u1 = data[1][0] + (data[1][1]-data[1][0])*idy;
                double v = u0 + (u1-u0)*idx;
                assertEquals(v, i.interpolatedValueAt(data, idx, idy), 0.000000001d);
            }
        }
    }
}
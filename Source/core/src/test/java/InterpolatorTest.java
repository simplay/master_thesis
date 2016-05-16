import datastructures.Interpolator;
import datastructures.Point2d;
import datastructures.Point3d;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

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
    public void testPoint2dCaseOutOfBoundaryThrowsException() {
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
    public void testPoint3dCaseOutOfBoundaryThrowsException() {
        Point3d[][] data = new Point3d[2][3];
        Point3d[] row_1 = {new Point3d(1d, 2d, 3d), new Point3d(4d, 5d, 6d), new Point3d(7d, 8d, 9d)};
        Point3d[] row_2 = {new Point3d(1d, 2d, 3d), new Point3d(4d, 5d, 6d), new Point3d(7d, 8d, 9d)};
        data[0] = row_1;
        data[1] = row_2;
        Interpolator i = new Interpolator();
        exception.expect(ArrayIndexOutOfBoundsException.class);
        exception.expectMessage("Dimensions (m,n)=(2,3) but accessing (x,y)=(1.5,2.0)");
        i.interpolatedValueAt(data, 1.5, 2);
    }

    @Test
    public void testRandomInterpolation2dPoints() {
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

    @Test
    public void testRoundedInterpolSameAsRoundedLookup() {
        double[][] data = new double[3][3];
        double[] row_1 = {Math.random(), Math.random(), Math.random()};
        double[] row_2 = {Math.random(), Math.random(), Math.random(), };
        double[] row_3 = {Math.random(), Math.random(), Math.random(), };
        data[0] = row_1;
        data[1] = row_2;
        data[2] = row_3;
        Interpolator i = new Interpolator();

        Point2d p = new Point2d(0.6 ,0.6);

        double iv = i.interpolatedValueAt(data, p.rounded().x(), p.rounded().y());
        double iiv = data[p.rounded().iU()][p.rounded().iV()];
        assertEquals(iv, iiv, 0);
    }

    @Test
    public void testRandomInterpolation3dPoints() {
        for (int t = 0; t < 10; t++) {
            Point3d[][] data = new Point3d[2][2];
            Point3d[] row_1 = {
                    new Point3d(Math.random(), Math.random(), Math.random()),
                    new Point3d(Math.random(), Math.random(), Math.random())
            };
            Point3d[] row_2 = {
                    new Point3d(Math.random(), Math.random(), Math.random()),
                    new Point3d(Math.random(), Math.random(), Math.random())
            };
            data[0] = row_1;
            data[1] = row_2;

            Interpolator i = new Interpolator();

            for (int k = 0; k < 1000; k++) {
                double idx = Math.random(); //0.6d;
                double idy = Math.random();

                // 3 times linear interpolation: f0 + [(f1-f0)/(x1-x0)]*(x-x0)
                // here x0 = 0 and x1 = 1
                // here: for a fixed row, we interpolated along the column, therefore use idy first.
                LinkedList<Double> vs = new LinkedList();

                double u0 = data[0][0].x() + (data[0][1].x() - data[0][0].x()) * idy;
                double u1 = data[1][0].x() + (data[1][1].x() - data[1][0].x()) * idy;
                vs.add(u0 + (u1 - u0) * idx);

                u0 = data[0][0].y() + (data[0][1].y() - data[0][0].y()) * idy;
                u1 = data[1][0].y() + (data[1][1].y() - data[1][0].y()) * idy;
                vs.add(u0 + (u1 - u0) * idx);

                u0 = data[0][0].z() + (data[0][1].z() - data[0][0].z()) * idy;
                u1 = data[1][0].z() + (data[1][1].z() - data[1][0].z()) * idy;
                vs.add(u0 + (u1 - u0) * idx);

                Point3d interpolPoint = i.interpolatedValueAt(data, idx, idy);

                assertEquals(vs.get(0), interpolPoint.x(), 0.000000001d);
                assertEquals(vs.get(1), interpolPoint.y(), 0.000000001d);
                assertEquals(vs.get(2), interpolPoint.z(), 0.000000001d);
            }
        }
    }

    @Test
    public void testSaveGetAt3dPointReturnZeroForInvalidLookup() {
        Point3d[][] data = new Point3d[2][2];
        Point3d[] row_1 = {
                new Point3d(Math.random(), Math.random(), Math.random()),
                new Point3d(Math.random(), Math.random(), Math.random())
        };
        Point3d[] row_2 = {
                new Point3d(Math.random(), Math.random(), Math.random()),
                new Point3d(Math.random(), Math.random(), Math.random())
        };
        data[0] = row_1;
        data[1] = row_2;

        Interpolator i = new Interpolator();
        Point3d ip = i.interpolatedValueAt(data, 1, 1);

        assertEquals(data[1][1].x(), ip.x(), 0);
        assertEquals(data[1][1].y(), ip.y(), 0);
        assertEquals(data[1][1].z(), ip.z(), 0);
    }
}

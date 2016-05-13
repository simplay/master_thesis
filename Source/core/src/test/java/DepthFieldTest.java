import datastructures.DepthField;
import datastructures.FlowField;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DepthFieldTest {

    @Test
    public void testCreate() {
        int m = 4;
        int n = 5;
        DepthField df = new DepthField(m, n);

        assertEquals(m, df.getData().length);
        assertEquals(n, df.getData()[0].length);
        assertEquals(m, df.m());
        assertEquals(n, df.n());

        for (int k = 0; k < df.getData().length; k++) {
            for (int l = 0; l < df.getData()[0].length; l++) {
                assertEquals(0, df.getData()[k][l], 0);
            }
        }
    }

    @Test
    public void testSetRowAt() {
        int m = 20; int n = 30;
        DepthField df = new DepthField(m, n);
        LinkedList<double[]> rows = new LinkedList<>();

        // assign depth values
        for (int k = 0; k < m; k++) {
            double[] row = new double[n];
            for (int l = 0; l < n; l++) {
                row[l] = Math.random();
            }
            rows.add(row);
            df.setRow(k, row);
        }

        // check assignments
        for (int k = 0; k < m; k++) {
            for (int l = 0; l < n; l++) {
                assertEquals(rows.get(k)[l], df.getData()[k][l], 0);
            }
        }
    }

    @Test
    public void testSetAt() {
        int m = 100; int n = 110;
        DepthField df = new DepthField(m, n);
        LinkedList<double[]> rows = new LinkedList<>();

        int N = 50;
        for (int k = 0; k < N; k++) {

            int randRowIdx = (int)(Math.random()*m);
            int randColIdx = (int)(Math.random()*n);
            double depth = Math.random();

            df.setAt(randRowIdx, randColIdx, depth);
            assertEquals(depth, df.getData()[randRowIdx][randColIdx], 0);
        }
    }

    @Test
    public void testValidRegionAt() {
        DepthField df = new DepthField(3, 3);

        double[] row1 = {0, 1, 1};
        double[] row2 = {1, 1, 1};
        double[] row3 = {1, 1, 0};

        df.setRow(0, row1);
        df.setRow(1, row2);
        df.setRow(2, row3);

        assertEquals(false, df.validRegionAt(0, 0));
        assertEquals(false, df.validRegionAt(0.5, 0.5));
        assertEquals(true, df.validRegionAt(1, 0.5));
        assertEquals(false, df.validRegionAt(1, 1));
        assertEquals(true, df.validRegionAt(1, 0.9));
        assertEquals(true, df.validRegionAt(0, 1));
        assertEquals(true, df.validRegionAt(0.5, 1));
        assertEquals(false, df.validRegionAt(1.5, 1));
    }

    @Test
    public void testValueAt() {

        int m = 3;
        int n = 3;
        DepthField df = new DepthField(3, 3);
        LinkedList<double[]> rows = new LinkedList<>();

        // assign random values to u,v - fields
        for (int idx = 0; idx < m; idx++) {
            double[] row = {Math.random(), Math.random(), Math.random(), Math.random()};
            rows.add(row);
            df.setRow(idx, row);
        }

        // Checks the exact lookup position
        for(int k = 0; k < df.getData().length; k++) {
            for (int l = 0; l < df.getData()[0].length; l++) {
                assertEquals(rows.get(k)[l], df.getData()[k][l], 0);
                assertEquals(rows.get(k)[l], df.valueAt(k, l), 0);
            }
        }

        LinkedList<double[][]> fields = new LinkedList<>();
        fields.add(df.getData());

        int N = 50;
        for (int k = 0; k < N; k++) {

            double x = Math.random()*2;
            double y = Math.random()*2;

            int idx = 0;
            for (double[][] field : fields) {
                int px_i = (int) Math.floor(x);
                int py_i = (int) Math.floor(y);

                int px_i2 = px_i + 1;
                int py_i2 = py_i + 1;

                double dx = x - px_i;
                double dy = y - py_i;

                double f_00 = field[px_i][py_i];
                double f_01 = field[px_i][py_i2];
                double f_10 = field[px_i2][py_i];
                double f_11 = field[px_i2][py_i2];


                double sum = 0d;
                sum += f_00 * (1.0d - dx) * (1.0d - dy);
                sum += f_01 * (1.0d - dx) * dy;
                sum += f_10 * dx * (1.0d - dy);
                sum += f_11 * dx * dy;

                assertEquals(sum, df.valueAt(x, y), 0);
            }
        }
    }
}

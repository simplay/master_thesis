import datastructures.FlowField;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;
/**
 * Created by simplay on 03/03/16.
 */
public class FlowFieldTest {

    @Test
    public void testDimensionalityMatches() {
        int m = 3;
        int n = 4;

        FlowField ff = new FlowField(m, n, "foobar");

        assertEquals(n, ff.getU()[0].length);
        assertEquals(m, ff.getU().length);
        assertEquals(n, ff.getV()[0].length);
        assertEquals(m, ff.getV().length);
        assertEquals(m, ff.m());
        assertEquals(n, ff.n());
    }

    @Test
    public void testInitializedWithZeros() {
        int m = 3;
        int n = 4;

        FlowField ff = new FlowField(m, n, "foobar");

        for (double[] row : ff.getU() ) {
            for (Double item : row) {
                assertEquals(new Double(0), item);
            }
        }
        for (double[] row : ff.getV() ) {
            for (Double item : row) {
                assertEquals(new Double(0), item);
            }
        }
    }

    @Test
    public void testSetRowByIndex() {
        int m = 3;
        int n = 4;

        FlowField ff = new FlowField(m, n, "foobar");

        LinkedList<double[]> uRows = new LinkedList<>();
        LinkedList<double[]> vRows = new LinkedList<>();

        for (int idx = 0; idx < m; idx++) {
            double[] uRow = {Math.random(), Math.random(), Math.random(), Math.random()};
            uRows.add(uRow);
            double[] vRow = {Math.random(), Math.random(), Math.random(), Math.random()};
            vRows.add(vRow);
            ff.setRow(idx, uRow, vRow);
        }

        int k1 = 0;
        for (double[] row : ff.getU() ) {
            int idx = 0;
            for (Double item : row) {
                assertEquals(new Double(uRows.get(k1)[idx]), item);
                idx++;
            }
            k1++;
        }

        k1 = 0;
        for (double[] row : ff.getV() ) {
            int idx = 0;
            for (Double item : row) {
                assertEquals(new Double(vRows.get(k1)[idx]), item);
                idx++;
            }
            k1++;
        }
    }

    @Test
    public void testType() {
        FlowField ff = new FlowField(1, 1, FlowField.FORWARD_FLOW);
        FlowField bf = new FlowField(1, 1, FlowField.BACKWARD_FLOW);
        assertTrue(ff.isForwardFlow());
        assertTrue(bf.isBackwardFlow());
        assertFalse(bf.isForwardFlow());
        assertFalse(ff.isBackwardFlow());
    }

    @Test
    public void testValueAt() {

        int m = 3;
        int n = 3;
        FlowField ff = new FlowField(m, n, FlowField.FORWARD_FLOW);
        LinkedList<double[]> uRows = new LinkedList<>();
        LinkedList<double[]> vRows = new LinkedList<>();

        // assign random values to u,v - fields
        for (int idx = 0; idx < m; idx++) {
            double[] uRow = {Math.random(), Math.random(), Math.random(), Math.random()};
            uRows.add(uRow);
            double[] vRow = {Math.random(), Math.random(), Math.random(), Math.random()};
            vRows.add(vRow);
            ff.setRow(idx, uRow, vRow);
        }

        // Checks the exact lookup position
        for(int k = 0; k < ff.getU().length; k++) {
            for (int l = 0; l < ff.getU()[0].length; l++) {
                assertEquals(uRows.get(k)[l], ff.getU()[k][l], 0);
                assertEquals(uRows.get(k)[l], ff.u_valueAt(k, l), 0);

                assertEquals(vRows.get(k)[l], ff.getV()[k][l], 0);
                assertEquals(vRows.get(k)[l], ff.v_valueAt(k, l), 0);
            }
        }

        LinkedList<double[][]> fields = new LinkedList<>();
        fields.add(ff.getU());
        fields.add(ff.getV());

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

                if (idx % 2 == 0) {
                    assertEquals(sum, ff.u_valueAt(x, y), 0);
                } else {
                    assertEquals(sum, ff.v_valueAt(x, y), 0);
                }
                idx++;
            }
        }
    }


}

import datastructures.FlowVarField;
import datastructures.Point2d;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class FlowVarFieldTest {

    @Test
    public void testCreate() {
        int m = 3; int n = 4;
        FlowVarField var = new FlowVarField(m, n);

        Field field = null;
        try {
            field = FlowVarField.class.getDeclaredField("matrix");
            field.setAccessible(true);
            try {
                double[][] state = (double[][])field.get(var);
                assertEquals(m, state.length);
                assertEquals(n, state[0].length);
                for (int k = 0; k < state.length; k++) {
                    for (int l = 0; l < state[0].length; l++) {
                        assertEquals(0, state[k][l], 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetRow() {
        int m = 3; int n = 4;
        FlowVarField var = new FlowVarField(m, n);

        LinkedList<double[]> rows = new LinkedList<>();

        for (int k = 0; k < m; k++) {
            double[] row = new double[n];
            for (int l = 0; l < n; l++) {
                row[l] = Math.random();
            }
            rows.add(row);
            var.setRow(k, row);
        }

        Field field = null;
        try {
            field = FlowVarField.class.getDeclaredField("matrix");
            field.setAccessible(true);
            try {
                double[][] state = (double[][])field.get(var);
                assertEquals(m, state.length);
                assertEquals(n, state[0].length);
                for (int k = 0; k < state.length; k++) {
                    for (int l = 0; l < state[0].length; l++) {
                        assertEquals(rows.get(k)[l], state[k][l], 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testValueAt() {
        int m = 40; int n = 45;
        FlowVarField var = new FlowVarField(m, n);

        LinkedList<double[]> rows = new LinkedList<>();

        for (int k = 0; k < m; k++) {
            double[] row = new double[n];
            for (int l = 0; l < n; l++) {
                row[l] = Math.random();
            }
            rows.add(row);
            var.setRow(k, row);
        }

        // Check exact position
        for (int k = 0; k < m; k++) {
            for (int l = 0; l < n; l++) {
                assertEquals(rows.get(k)[l], var.valueAt(new Point2d(k, l)), 0);
            }
        }

        Field field = null;
        try {
            field = FlowVarField.class.getDeclaredField("matrix");
            field.setAccessible(true);
            try {
                double[][] state = (double[][])field.get(var);

                int N = 50;
                for (int k = 0; k < N; k++) {

                    double x = Math.random()*2;
                    double y = Math.random()*2;

                    int px_i = (int) Math.floor(x);
                    int py_i = (int) Math.floor(y);

                    int px_i2 = px_i + 1;
                    int py_i2 = py_i + 1;

                    double dx = x - px_i;
                    double dy = y - py_i;

                    double f_00 = state[px_i][py_i];
                    double f_01 = state[px_i][py_i2];
                    double f_10 = state[px_i2][py_i];
                    double f_11 = state[px_i2][py_i2];


                    double sum = 0d;
                    sum += f_00 * (1.0d - dx) * (1.0d - dy);
                    sum += f_01 * (1.0d - dx) * dy;
                    sum += f_10 * dx * (1.0d - dy);
                    sum += f_11 * dx * dy;

                    assertEquals(sum, var.valueAt(new Point2d(x, y)), 0);
                }


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}

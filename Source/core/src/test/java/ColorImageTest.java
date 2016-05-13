import datastructures.ColorImage;
import datastructures.Point2d;
import datastructures.Point3d;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class ColorImageTest {

    @Test
    public void testCreate() {
        int m = 4;
        int n = 5;
        ColorImage img = new ColorImage(m, n);

        Field field = null;
        try {
            field = ColorImage.class.getDeclaredField("rgbValues");
            field.setAccessible(true);
            try {
                Point3d[][] state = (Point3d[][])field.get(img);
                assertEquals(m, state.length);
                assertEquals(n, state[0].length);
                for (int k = 0; k < state.length; k++) {
                    for (int l = 0; l < state[0].length; l++) {
                        assertEquals(null, state[k][l]);
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
    public void testSetElement() {
        int m = 20;
        int n = 25;
        ColorImage img = new ColorImage(m, n);

        Point3d[][] rgbValues = new Point3d[m][n];
        for (int k = 0; k < m; k++) {
            for (int l = 0; l < n; l++) {
                Point3d rgb = new Point3d(Math.random(), Math.random(), Math.random());
                rgbValues[k][l] = rgb;
                img.setElement(rgb, k, l);
            }
        }

        Field field = null;
        try {
            field = ColorImage.class.getDeclaredField("rgbValues");
            field.setAccessible(true);
            try {
                Point3d[][] state = (Point3d[][])field.get(img);
                assertEquals(m, state.length);
                assertEquals(n, state[0].length);
                for (int k = 0; k < state.length; k++) {
                    for (int l = 0; l < state[0].length; l++) {
                        assertEquals(rgbValues[k][l], state[k][l]);
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
        int m = 20;
        int n = 25;
        ColorImage img = new ColorImage(m, n);

        for (int k = 0; k < m; k++) {
            for (int l = 0; l < n; l++) {
                Point3d rgb = new Point3d(Math.random(), Math.random(), Math.random());
                img.setElement(rgb, k, l);
            }
        }

        int N = 20;
        Field field = null;
        try {
            field = ColorImage.class.getDeclaredField("rgbValues");
            field.setAccessible(true);
            try {
                Point3d[][] state = (Point3d[][])field.get(img);

                for (int t = 0; t < N; t++) {
                    double x = Math.random()*(m-1);
                    double y = Math.random()*(n-1);

                    int px_i = (int) Math.floor(x);
                    int py_i = (int) Math.floor(y);

                    int px_i2 = px_i + 1;
                    int py_i2 = py_i + 1;

                    double dx = x - px_i;
                    double dy = y - py_i;

                    Point3d f_00 = state[px_i][py_i];
                    Point3d f_01 = state[px_i][py_i2];
                    Point3d f_10 = state[px_i2][py_i];
                    Point3d f_11 = state[px_i2][py_i2];

                    double i_x = bilinearInterpolatedValue(f_00.x(), f_01.x(), f_10.x(), f_11.x(), dx, dy);
                    double i_y = bilinearInterpolatedValue(f_00.y(), f_01.y(), f_10.y(), f_11.y(), dx, dy);
                    double i_z = bilinearInterpolatedValue(f_00.z(), f_01.z(), f_10.z(), f_11.z(), dx, dy);

                    // manually interpolated point
                    Point3d ip = new Point3d(i_x, i_y, i_z);

                    // interpolated lookup valued
                    Point3d ipl = img.valueAt(new Point2d(x, y));

                    // checks whether interpolated components correspond to real computed values
                    assertEquals(ip.x(), ipl.x(), 0);
                    assertEquals(ip.y(), ipl.y(), 0);
                    assertEquals(ip.z(), ipl.z(), 0);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply the bilinear combination scheme.
     *
     * @param f_00 lookup value left top
     * @param f_01 lookup value right top
     * @param f_10 lookup value left bottom
     * @param f_11 lookup value right bottom
     * @param dx weight width
     * @param dy weight height
     * @return interpolated value
     */
    private double bilinearInterpolatedValue(double f_00, double f_01, double f_10, double f_11, double dx, double dy) {
        double sum = 0d;
        sum += f_00*(1.0d-dx)*(1.0d-dy);
        sum += f_01*(1.0d-dx)*dy;
        sum += f_10*dx*(1.0d-dy);
        sum += f_11*dx*dy;
        return sum;
    }

}

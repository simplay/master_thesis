import datastructures.Activity;
import datastructures.InvalidRegionsMask;
import datastructures.Point2d;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class InvalidRegionsMaskTest {

    @Test
    public void testCreationWorks() {
        int m = 10;
        int n = 10;
        InvalidRegionsMask mask = new InvalidRegionsMask(m, n);
        Field field = null;
        try {
            field = InvalidRegionsMask.class.getDeclaredField("state");
            field.setAccessible(true);
            try {
                double[][] state = (double[][])field.get(mask);
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
        int m = 2;
        int n = 2;
        InvalidRegionsMask mask = new InvalidRegionsMask(m, n);

        double[] r1 = {Math.random(), Math.random()};
        double[] r2 = {Math.random(), Math.random()};

        mask.setRow(0, r1);
        mask.setRow(1, r2);

        Field field = null;
        try {
            field = InvalidRegionsMask.class.getDeclaredField("state");
            field.setAccessible(true);
            try {
                double[][] state = (double[][])field.get(mask);
                assertEquals(r1[0], state[0][0], 0);
                assertEquals(r1[1], state[0][1], 0);
                assertEquals(r2[0], state[1][0], 0);
                assertEquals(r2[1], state[1][1], 0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIsInvalidAt() {
        int m = 3;
        int n = 3;
        InvalidRegionsMask mask = new InvalidRegionsMask(m, n);

        double[] r1 = {0, 1, 1};
        double[] r2 = {0, 1, 1};
        double[] r3 = {0, 0, 0};

        mask.setRow(0, r1);
        mask.setRow(1, r2);
        mask.setRow(2, r3);

        assertEquals(false, mask.isInvalidAt(new Point2d(1.7, 0.5)));
        assertEquals(false, mask.isInvalidAt(new Point2d(1.5, 0.5)));
        assertEquals(true, mask.isInvalidAt(new Point2d(1.5, 1.0)));
        assertEquals(true, mask.isInvalidAt(new Point2d(1.0, 1.0)));
        assertEquals(true, mask.isInvalidAt(new Point2d(1.5, 1.5)));
        assertEquals(false, mask.isInvalidAt(new Point2d(1.6, 1.5)));
        assertEquals(false, mask.isInvalidAt(new Point2d(1.51, 1.5)));
        assertEquals(true, mask.isInvalidAt(new Point2d(1.5, 1.6)));
        assertEquals(true, mask.isInvalidAt(new Point2d(1.5, 1.4)));
    }
}

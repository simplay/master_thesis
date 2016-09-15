import datastructures.Activity;
import datastructures.Point2d;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActivityTest {

    @Test
    public void testCreate() {
        // obtain a random integer between [50, 550]
        int m = (int)(Math.random()*500) + 50;
        int n = (int)(Math.random()*500) + 50;

        // obtain a random integer between [4, 9]
        int s = (int)(Math.random()*5)+4;

        Activity a = new Activity(m, n, s);

        boolean[][] initialStates = a.getStates();

        assertEquals(m, initialStates.length);
        assertEquals(n, initialStates[0].length);
        for (int u = 0; u < initialStates.length; u++) {
            for (int v = 0; v < initialStates[0].length; v++) {
                assertFalse(initialStates[u][v]);
            }
        }

        // Check via reflection whether the private field "samplingRate" was correctly assigned
        Field field = null;
        try {
            field = Activity.class.getDeclaredField("samplingRate");
            field.setAccessible(true);
            try {
                int assignedSamplingRate = (int)field.get(a);
                assertEquals(s, assignedSamplingRate);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testActiveAt() {

        Activity a = new Activity(20, 20, 1);
        boolean[][] initialStates = a.getStates();
        for (int u = 0; u < initialStates.length; u++) {
            for (int v = 0; v < initialStates[0].length; v++) {
                assertFalse(initialStates[u][v]);
            }
        }

        // Mark 40 times a random location as true
        for (int k = 0; k < 40; k++) {
            int randRowIdx = (int) (Math.random() * 20);
            int randColIdx = (int) (Math.random() * 20);
            a.markActiveAt(randRowIdx, randColIdx);
            assertTrue(initialStates[randRowIdx][randColIdx]);
        }
    }

    @Test
    public void testCopyStates() {
        Activity a1 = new Activity(20, 20, 1);
        Activity a2 = new Activity(20, 20, 1);

        boolean[][] initialStates = a1.getStates();

        // Mark 40 times a random location as true
        for (int k = 0; k < 100; k++) {
            int randRowIdx = (int) (Math.random() * 20);
            int randColIdx = (int) (Math.random() * 20);
            a1.markActiveAt(randRowIdx, randColIdx);
            assertTrue(initialStates[randRowIdx][randColIdx]);
        }

        // copy the state of a1 into a2
        a2.copyStates(a1);

        boolean[][] a1States = a1.getStates();
        boolean[][] a2States = a2.getStates();
        for (int u = 0; u < initialStates.length; u++) {
            for (int v = 0; v < initialStates[0].length; v++) {
                assertEquals(a1States[u][v], a2States[u][v]);
            }
        }
    }

    @Test
    public void testFlushStates() {
        Activity a = new Activity(20, 20, 1);
        boolean[][] initialStates = a.getStates();
        for (int u = 0; u < initialStates.length; u++) {
            for (int v = 0; v < initialStates[0].length; v++) {
                assertFalse(initialStates[u][v]);
            }
        }

        // Mark 40 times a random location as true
        for (int k = 0; k < 40; k++) {
            int randRowIdx = (int) (Math.random() * 20);
            int randColIdx = (int) (Math.random() * 20);
            a.markActiveAt(randRowIdx, randColIdx);
            assertTrue(initialStates[randRowIdx][randColIdx]);
        }

        a.flushStates();
        initialStates = a.getStates();

        for (int u = 0; u < initialStates.length; u++) {
            for (int v = 0; v < initialStates[0].length; v++) {
                assertFalse(initialStates[u][v]);
            }
        }
    }

    @Test
    public void testHasActivityAt() {

        for (int s = 1; s < 11; s++) {
            Activity a = new Activity(100, 100, s);

            // random integer values in [20, 70]
            //
            // the minimum window over the s-loop
            // would start at 20-5 = 15 and the max. window at 70+5 = 75
            int r_idx = (int)(Math.random()*50) + 20;
            int c_idx = (int)(Math.random()*50) + 20;

            a.markActiveAt(r_idx, c_idx);

            // iterate over window (s/2 x s/2)
            for (int k = -s / 2; k < s / 2; k++) {
                for (int l = -s / 2; l < s / 2; l++) {
                    int rowIdx = r_idx + k;
                    int colIdx = c_idx + l;
                    assertEquals(true, a.hasActivityAt(new Point2d(rowIdx, colIdx)));
                }
            }

            // outside of the window query position: negative case
            for (int k = -s / 2; k < s / 2; k++) {
                for (int l = -s / 2; l < s / 2; l++) {
                    int rowIdx = r_idx + k - s;
                    int colIdx = c_idx + l - s;
                    assertEquals(false, a.hasActivityAt(new Point2d(rowIdx, colIdx)));
                }
            }

            // Outside the window query position: positive case
            for (int k = -s / 2; k < s / 2; k++) {
                for (int l = -s / 2; l < s / 2; l++) {
                    int rowIdx = r_idx + k + s + 1;
                    int colIdx = c_idx + l + s + 1;
                    assertEquals(false, a.hasActivityAt(new Point2d(rowIdx, colIdx)));
                }
            }
        }
    }

}

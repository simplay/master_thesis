import datastructures.Mat3x4;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Mat3x4Test {

    @Test
    public void testGetCol() {
        double[] row1 = {1, 2, 3, 4};
        double[] row2 = {5, 6, 7, 8};
        double[] row3 = {9, 10, 11, 12};

        Mat3x4 T = new Mat3x4(row1, row2, row3);

        assertEquals(T.getCol(0).x(), 1.0);
        assertEquals(T.getCol(0).y(), 5.0);
        assertEquals(T.getCol(0).z(), 9.0);

        assertEquals(T.getCol(1).x(), 2.0);
        assertEquals(T.getCol(1).y(), 6.0);
        assertEquals(T.getCol(1).z(), 10.0);

        assertEquals(T.getCol(2).x(), 3.0);
        assertEquals(T.getCol(2).y(), 7.0);
        assertEquals(T.getCol(2).z(), 11.0);

        assertEquals(T.getCol(3).x(), 4.0);
        assertEquals(T.getCol(3).y(), 8.0);
        assertEquals(T.getCol(3).z(), 12.0);
    }

    @Test
    public void testOne() {
        Mat3x4 T = Mat3x4.one();
        assertEquals(T.getCol(0).x(), 1.0);
        assertEquals(T.getCol(0).y(), 0.0);
        assertEquals(T.getCol(0).z(), 0.0);

        assertEquals(T.getCol(1).x(), 0.0);
        assertEquals(T.getCol(1).y(), 1.0);
        assertEquals(T.getCol(1).z(), 0.0);

        assertEquals(T.getCol(2).x(), 0.0);
        assertEquals(T.getCol(2).y(), 0.0);
        assertEquals(T.getCol(2).z(), 1.0);

        assertEquals(T.getCol(3).x(), 0.0);
        assertEquals(T.getCol(3).y(), 0.0);
        assertEquals(T.getCol(3).z(), 0.0);
    }
}

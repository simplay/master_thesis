import org.junit.Test;
import static org.junit.Assert.*;

public class InterpolatorTest {
    @Test
    public void testDimensionalityMatches() {
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
}

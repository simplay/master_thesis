import org.junit.Test;
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
    }

    @Test
    public void testInitializedWithZeros() {
        int m = 3;
        int n = 4;

        FlowField ff = new FlowField(m, n, "foobar");

        for (float[] row : ff.getU() ) {
            for (Float item : row) {
                assertEquals(new Float(0), item);
            }
        }
        for (float[] row : ff.getV() ) {
            for (Float item : row) {
                assertEquals(new Float(0), item);
            }
        }
    }

    @Test
    public void testSetRowByIndex() {
        int m = 3;
        int n = 4;

        FlowField ff = new FlowField(m, n, "foobar");

        for (float[] row : ff.getU() ) {
            for (Float item : row) {
                assertEquals(new Float(0), item);
            }
        }
        for (float[] row : ff.getV() ) {
            for (Float item : row) {
                assertEquals(new Float(0), item);
            }
        }
    }


}
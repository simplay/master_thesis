import datastructures.InvalidRegionsMask;
import datastructures.Point2d;
import managers.InvalidRegionManager;
import org.junit.Before;
import org.junit.Test;
import readers.InvalidRegionReader;

import static org.junit.Assert.assertEquals;

public class InvalidRegionReaderTest {

    @Before
    public void initObjects() {
        InvalidRegionManager.release();
    }

    @Test
    public void testReadingAFileWorks() {
        new InvalidRegionReader("foobar", "1", "./testdata/");

        InvalidRegionsMask irf0 = InvalidRegionManager.getInstance().getInvalidRegionAt(0);
        assertEquals(true, irf0.isInvalidAt(new Point2d(0,0)));
        assertEquals(true, irf0.isInvalidAt(new Point2d(0,1)));
        assertEquals(false, irf0.isInvalidAt(new Point2d(1,0)));
        assertEquals(true, irf0.isInvalidAt(new Point2d(1,1)));
    }
}

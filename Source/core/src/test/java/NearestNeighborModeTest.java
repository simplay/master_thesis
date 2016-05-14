import datastructures.NearestNeighborMode;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class NearestNeighborModeTest {

    @Test
    public void testGetModeById() {
        assertEquals(NearestNeighborMode.TOP_N, NearestNeighborMode.getModeById("top"));
        assertEquals(NearestNeighborMode.TOP_AND_WORST_N, NearestNeighborMode.getModeById("both"));
        assertEquals(NearestNeighborMode.ALL, NearestNeighborMode.getModeById("all"));
        assertEquals(NearestNeighborMode.ALL, NearestNeighborMode.getModeById(String.valueOf(Math.random())));
    }

    @Test
    public void testGetId() {
        assertEquals("top", NearestNeighborMode.TOP_N.getId());
        assertEquals("both", NearestNeighborMode.TOP_AND_WORST_N.getId());
        assertEquals("all", NearestNeighborMode.ALL.getId());
    }
}

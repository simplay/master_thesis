import datastructures.TrackingCandidates;
import org.junit.Before;
import org.junit.Test;
import readers.CandidateFileReader;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class CandidateFileReaderTest {

    @Before
    public void initObjects() {
        TrackingCandidates.release();
    }

    @Test
    public void testReadingAFileWorks() {
        new CandidateFileReader("foobar", "1", "./testdata/");

        int a00 = 77; int a01 = 85;
        int a10 = 5; int a11 = 7;

        LinkedList<Integer[]> tcf1 = TrackingCandidates.getInstance().getCandidateOfFrame(0);

        // -1 since indices in matlab start counting at 1 and in java at 0.
        assertEquals(a00-1, tcf1.get(0)[0], 0);
        assertEquals(a01-1, tcf1.get(0)[1], 0);
        assertEquals(a10-1, tcf1.get(1)[0], 0);
        assertEquals(a11-1, tcf1.get(1)[1], 0);
    }
}

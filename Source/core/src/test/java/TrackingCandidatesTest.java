import datastructures.TrackingCandidates;
import managers.CalibrationManager;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class TrackingCandidatesTest {

    @Before
    public void initObjects() {
        TrackingCandidates.release();
    }

    @Test
    public void testAddAndGetCandidate() {

        int N = 100;

        String[] rows = new String[N];
        String[] cols = new String[N];
        for (int n = 0; n < N; n++) {
            rows[n] = String.valueOf((int)(Math.random()*999)+1);
            cols[n] = String.valueOf((int)(Math.random()*999)+1);
        }

        TrackingCandidates.getInstance().addCandidates(rows, cols);

        LinkedList<Integer[]> candidates = TrackingCandidates.getInstance().getCandidateOfFrame(0);

        int idx = 0;
        for (Integer rowIdx : candidates.get(0)) {
            assertEquals(rowIdx, Integer.parseInt(rows[idx])-1, 0);
            idx++;
        }

        idx = 0;
        for (Integer colIdx : candidates.get(1)) {
            assertEquals(colIdx, Integer.parseInt(cols[idx])-1, 0);
            idx++;
        }
    }
}

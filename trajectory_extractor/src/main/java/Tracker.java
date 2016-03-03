import java.util.LinkedList;

/**
 * Created by simplay on 03/03/16.
 */
public class Tracker {

    public Tracker() {

        LinkedList<Integer[]> startFrameCandidates = TrackingCandidates.getInstance().getCandidateOfFrame(0);

        Integer[] rows = startFrameCandidates.get(0);
        Integer[] cols = startFrameCandidates.get(1);
        FlowField fw_startFrame = FlowFieldManager.getInstance().getForwardFlow(0);


        for (int idx = 0; idx < rows.length; idx++) {
            int row_idx = rows[idx];
            int col_idx = cols[idx];

            float du = fw_startFrame.u_valueAt(row_idx, col_idx);
            float dv = fw_startFrame.v_valueAt(row_idx, col_idx);

            float next_u = row_idx+du;
            float next_v = col_idx+dv;

            System.out.println("next u="+next_u + " next v="+next_v);
        }
    }
}
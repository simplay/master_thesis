import java.awt.*;
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

        System.out.println("Setting starting points...");
        for (int idx = 0; idx < rows.length; idx++) {
            int row_idx = rows[idx];
            int col_idx = cols[idx];
            Point2f p = new Point2f(row_idx, col_idx);
            TrajectoryManager.getInstance().startNewTrajectoryAt(p, 0);
        }

        System.out.println("Computing initial tracking to positions");
        for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(0)) {
            Point2f p = tra.getPointAtFrame(0);
            float du = fw_startFrame.u_valueAt(p.u(), p.v());
            float dv = fw_startFrame.v_valueAt(p.u(), p.v());

            float next_u = p.u() + du;
            float next_v = p.v() + dv;

            Point2f next_p = new Point2f(next_u, next_v);
            TrajectoryManager.getInstance().appendPointTo(tra.getLabel(), next_p);

        }
    }
    
}
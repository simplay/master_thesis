import java.awt.*;
import java.util.LinkedList;

/**
 * Created by simplay on 03/03/16.
 */
public class Tracker {

    private float m;
    private float n;

    public Tracker() {

        m = FlowFieldManager.getInstance().m();
        n = FlowFieldManager.getInstance().n();

        System.out.println("Start point tracking...");

        for (int frame_idx = 0; frame_idx < 2; frame_idx++) {
            startNewTrajectory(frame_idx);
            continueTrackToNextFrame(frame_idx);
        }
        System.out.println("Finished point tracking...");
    }

    // TODO only track every k-th sample except (sampling rate)
    private void startNewTrajectory(int frame_idx) {
        LinkedList<Integer[]> currentFrame = TrackingCandidates.getInstance().getCandidateOfFrame(frame_idx);

        Integer[] rows = currentFrame.get(0);
        Integer[] cols = currentFrame.get(1);

        for (int idx = 0; idx < rows.length; idx++) {
            int row_idx = rows[idx];
            int col_idx = cols[idx];
            Point2f p = new Point2f(row_idx, col_idx);
            TrajectoryManager.getInstance().startNewTrajectoryAt(p, frame_idx);
        }
    }

    /**
     * @param currentFrame
     */
    private void continueTrackToNextFrame(int currentFrame) {
        FlowField fw_startFrame = FlowFieldManager.getInstance().getForwardFlow(currentFrame);
        for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(currentFrame)) {
            System.out.println(tra.toString());
            Point2f p = tra.getPointAtFrame(currentFrame);
            float du = fw_startFrame.u_valueAt(p.u(), p.v());
            float dv = fw_startFrame.v_valueAt(p.u(), p.v());

            float next_u = p.u() + du;
            float next_v = p.v() + dv;

            // skip all tracked to points that are out of the image frame
            if (next_u < 0f || next_v < 0f || next_u > m || next_v > n) {
                continue;
            }

            // TODO implement an occlusion check here by computing the tracked from
            // position using the tracked to position using bilinear interpolation
            // and the backward flow field

            Point2f next_p = new Point2f(next_u, next_v);
            TrajectoryManager.getInstance().appendPointTo(tra.getLabel(), next_p);
        }
    }

}
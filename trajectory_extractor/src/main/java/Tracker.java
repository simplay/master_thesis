import java.util.LinkedList;

public class Tracker {

    private float m;
    private float n;
    private Activity activity;
    private Activity activity_next;
    private int samplingRate;

    public Tracker(int till_index, int samplingRate) {
        this.samplingRate = samplingRate;
        m = FlowFieldManager.getInstance().m();
        n = FlowFieldManager.getInstance().n();

        System.out.println("Start point tracking...");

        activity = new Activity((int) m, (int) n, samplingRate);
        activity_next = new Activity((int) m, (int) n, samplingRate);

        // in the first pass, all states should be set to true
        // activity.setAllStatesEqualTrue();

        // the order here is crucial: first start to start new trajectories
        // and then continue others.
        for (int frame_idx = 0; frame_idx < till_index; frame_idx++) {
            startNewTrajectory(frame_idx);
            continueTrackToNextFrame(frame_idx);
            activity.copyStates(activity_next);
            activity_next.flushStates();
            System.out.println("Tracked Frame " + (frame_idx+1));
        }

        System.out.println("Finished point tracking...");
    }

    private void startNewTrajectory(int frame_idx) {
        LinkedList<Integer[]> currentFrame = TrackingCandidates.getInstance().getCandidateOfFrame(frame_idx);

        Integer[] rows = currentFrame.get(0);
        Integer[] cols = currentFrame.get(1);

        for (int idx = 0; idx < rows.length; idx++) {
            int row_idx = rows[idx];
            int col_idx = cols[idx];
            Point2f p = new Point2f(row_idx, col_idx);

            if (activity.hasActivityAt(p)) {
                // System.out.println("too crowded");
                continue;
            } else {
                activity_next.markActiveAt(row_idx, col_idx);
            }

            TrajectoryManager.getInstance().startNewTrajectoryAt(p, frame_idx);
        }
    }

    //TODO mark trajectories as closed when landed in continue clause
    /**
     * @param currentFrame
     */
    private void continueTrackToNextFrame(int currentFrame) {
        FlowField fw_currentFrame = FlowFieldManager.getInstance().getForwardFlow(currentFrame);
        FlowField bw_currentFrame = FlowFieldManager.getInstance().getBackwardFlow(currentFrame);
        FlowMagnitudeField fw_sq2_mags = FlowMagManager.getInstance().getMagFlow(currentFrame);

        for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(currentFrame)) {
            // System.out.println(tra.toString());
            Point2f p = tra.getPointAtFrame(currentFrame);
            float du = fw_currentFrame.u_valueAt(p.u(), p.v());
            float dv = fw_currentFrame.v_valueAt(p.u(), p.v());

            float next_u = p.u() + du;
            float next_v = p.v() + dv;

            // skip all tracked to points that are out of the image frame
            if (next_u < 0f || next_v < 0f || next_u > m || next_v > n) {
                tra.markClosed();
                continue;
            }

            float du_prev = bw_currentFrame.u_valueAt(next_u, next_v);
            float dv_prev = bw_currentFrame.v_valueAt(next_u, next_v);

            // reconstructed tracked from pos p.u() and p.v()
            float pu_rec = next_u+du_prev;
            float pv_rec = next_v+dv_prev;

            float rhs = 0.2f*(du*du+ dv*dv+ du_prev*du_prev+ dv_prev*dv_prev)+5f;
            float lhs = (pu_rec-p.u())*(pu_rec-p.u())+(pv_rec-p.v())*(pv_rec-p.v());

            // occlusion test: if occluded, then end this tracking point
            if (lhs >= rhs) {
                // System.out.println("occluded");
                tra.markClosed();
                continue;

            }


            if (fw_sq2_mags.valueAt(p.u(), p.v()) > 0.01f*(du*du+ dv*dv)+0.002f) {
                // System.out.println("Too bright");
                tra.markClosed();
                continue;
            }

            // position using the tracked to position using bilinear interpolation
            // and the backward flow field
            Point2f next_p = new Point2f(next_u, next_v);
            activity_next.markActiveAt((int)next_u, (int)next_v);
            TrajectoryManager.getInstance().appendPointTo(tra.getLabel(), next_p);
        }
    }

}
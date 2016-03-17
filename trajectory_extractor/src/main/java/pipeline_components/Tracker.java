package pipeline_components;

import datastructures.*;
import managers.FlowFieldManager;
import managers.InvalidRegionManager;
import managers.TrajectoryManager;

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

        // in the first pass, all states of activity are set to false, i.e. not activity.
        // the order here is crucial: first start to start new trajectories
        // and then continue others.
        for (int frame_idx = 0; frame_idx < till_index; frame_idx++) {
            startNewTrajectory(frame_idx);
            continueTrackToNextFrame(frame_idx);
            activity.copyStates(activity_next);
            activity_next.flushStates();
            System.out.println("Tracked Frame " + (frame_idx+1));
        }

        // Cloe all max length trajectories
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            if (!tra.isClosed()) {
                tra.markClosed();
            }
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

            // Checks whether there is already too much activity
            // in p's region (i.e there is a sample around (k/2)^2
            if (activity.hasActivityAt(p)) {
                continue;
            }

            activity_next.markActiveAt(row_idx, col_idx);
            TrajectoryManager.getInstance().startNewTrajectoryAt(p, frame_idx);
        }
    }

    /**
     * @param currentFrame
     */
    private void continueTrackToNextFrame(int currentFrame) {
        FlowField fw_currentFrame = FlowFieldManager.getInstance().getForwardFlow(currentFrame);
        InvalidRegionsMask invalidRegions = InvalidRegionManager.getInstance().getMagFlow(currentFrame);

        for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(currentFrame)) {
            Point2f p = tra.getPointAtFrame(currentFrame);

            // Performs a consistency check
            if (invalidRegions.isInvalidAt(p)) {
                tra.markClosed();
                continue;
            }

            double du = fw_currentFrame.u_valueAt(p.u(), p.v());
            double dv = fw_currentFrame.v_valueAt(p.u(), p.v());

            double next_u = p.u() + du;
            double next_v = p.v() + dv;

            // skip all tracked to points that are out of the image frame
            if (next_u < 0f || next_v < 0f || next_u > m || next_v > n) {
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
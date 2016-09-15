package pipeline_components;

import datastructures.*;
import managers.DepthManager;
import managers.FlowFieldManager;
import managers.InvalidRegionManager;
import managers.TrajectoryManager;
import java.util.LinkedList;

// TODO: describe tracking algorithm in detail, also mention occlusion case.
/**
 * Tracker is responsible for tracing points of interest over a sequence of frames,
 * and forming trajectories from proximate/coherent points.
 * In other words, a trajectory is a collection of points of a feature, traced over
 * a sequence of points.
 *
 * Point Tracking:
 * For a given feature point in **frame t** at location (tx, ty),
 * we compute its corresponding **tracked to** position in **frame (t+1)**
 * by applying the forward flow to the current position.
 *
 * We can either start or continue a point tracking.
 * Initially, for starting the point tracking in a certain frame, we make use of
 * features computed by a Harris corner detector (i.e. the tracking candidates).
 *
 */
public class Tracker {

    // Dataset frame row count
    private float m;

    // Dataset frame column count
    private float n;

    // The activities in the current frame.
    private Activity activity;

    // The activities in the previous frame.
    private Activity activity_next;

    /**
     * Runs the point tracking till a given frame, starting from the first frame.
     * Starting.
     *
     * Running till a given frame index means, that all data, that is present till and with
     * the provided frame index is used in the tracking stage.
     *
     * This results in extracting trajectories with length at most equal to
     * the sequence length till the given frame index.
     *
     * @param till_index till frame index.
     * @param samplingRate encodes sparsity.
     */
    public Tracker(int till_index, int samplingRate) {
        m = FlowFieldManager.getInstance().m();
        n = FlowFieldManager.getInstance().n();

        Logger.println("Start point tracking...");

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
            Logger.println("Tracked Frame " + (frame_idx+1));
        }

        // Cloe all max length trajectories
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            if (!tra.isClosed()) {
                tra.markClosed();
            }
        }

        Logger.println("Finished point tracking...");
    }

    /**
     * Starts a new point tracking in a given frame.
     *
     * To start a new trajectory, we use the tracking candidates (Harris corner detector feature locations).
     *
     * A new trajectory can only be started if there is not too much activity in
     * the neighborhood present yet and it is on a valid position.
     *
     * @param frame_idx index of frame for which we want to start new trajectories.
     */
    private void startNewTrajectory(int frame_idx) {
        LinkedList<Integer[]> currentFrame = TrackingCandidates.getInstance().getCandidateOfFrame(frame_idx);
        InvalidRegionsMask invalidRegions = InvalidRegionManager.getInstance().getInvalidRegionAt(frame_idx);

        Integer[] rows = currentFrame.get(0);
        Integer[] cols = currentFrame.get(1);

        for (int idx = 0; idx < rows.length; idx++) {
            int row_idx = rows[idx];
            int col_idx = cols[idx];


            // Performs a consistency check: Do not start trajectories at invalid loactions
            if (invalidRegions.isInvalidAt(new Point2d(row_idx, col_idx))) {
                continue;
            }

            // skip at invalid depth regions if we are using depth cues.
            if (ArgParser.useDepthCues()) {
                if (!DepthManager.getInstance().get(0).validRegionAt(row_idx, col_idx)) {
                    continue;
                }
            }

            Point2d p = new Point2d(row_idx, col_idx);

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
     * Continues the existing trajectories.
     *
     * All trajectories, that are active in the previous frame are continued by applying the
     * forward flow to them.
     *
     * Activity masks are updated accordingly
     *
     * @param currentFrame index of frame we want to continue its trajectories.
     */
    private void continueTrackToNextFrame(int currentFrame) {
        FlowField fw_currentFrame = FlowFieldManager.getInstance().getForwardFlow(currentFrame);
        InvalidRegionsMask invalidRegions = InvalidRegionManager.getInstance().getInvalidRegionAt(currentFrame);

        for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(currentFrame)) {
            Point2d p = tra.getPointAtFrame(currentFrame);

            // Performs a consistency check
            if (invalidRegions.isInvalidAt(p)) {
                tra.markClosed();
                continue;
            }

            // skip at invalid depth regions
            if (ArgParser.useDepthCues()) {
                if (!DepthManager.getInstance().get(currentFrame).validRegionAt(p.x(), p.y())) {
                    tra.markClosed();
                    continue;
                }
            }

            double du = fw_currentFrame.u_valueAt(p.u(), p.v());
            double dv = fw_currentFrame.v_valueAt(p.u(), p.v());

            double next_u = p.u() + du;
            double next_v = p.v() + dv;

            // skip all tracked to points that are out of the image frame
            if (next_u < 0f || next_v < 0f || next_u > m-1 || next_v > n-1) {
                tra.markClosed();
                continue;
            }

            // position using the tracked to position using bilinear interpolation
            // and the backward flow field
            Point2d next_p = new Point2d(next_u, next_v);
            activity_next.markActiveAt((int)next_u, (int)next_v);
            TrajectoryManager.getInstance().appendPointTo(tra.getLabel(), next_p);
        }
    }

}
package similarity;

import datastructures.Point3d;
import datastructures.Trajectory;
import managers.CalibrationManager;

import java.util.Collection;

public class ProdDistAllEuclidTask extends ProdDistEuclidTask {

    private double scale;

    /**
     * @param a
     * @param trajectories
     */
    public ProdDistAllEuclidTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
        this.scale = 0.5d*(CalibrationManager.depth_focal_len().x() + CalibrationManager.depth_focal_len().y());
    }

    /**
     * Compute the normalized motion distance between two trajectory points having a certain frame distance.
     *
     * @param a trajectory
     * @param b trajectory
     * @param timestep sum with current frame yields the to-frame.
     * @param frame_idx starting frame index
     * @return the motion distance normalized by the flow variance.
     */
    @Override
    protected double d_motion(Trajectory a, Trajectory b, int timestep, int frame_idx) {
        Point3d dt_a = forward_difference3d(a, timestep, frame_idx);
        Point3d dt_b = forward_difference3d(b, timestep, frame_idx);

        if (!dt_a.isValid() || !dt_b.isValid()) return -1d;

        double dt_ab = dt_a.sub(dt_b).length_squared()/scale;
        double sigma_t = EPS_FLOW + getVariance(frame_idx, a, b);

        return dt_ab / sigma_t;
    }
}

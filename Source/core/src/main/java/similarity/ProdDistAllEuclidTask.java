package similarity;

import datastructures.Point2d;
import datastructures.Point3d;
import datastructures.Trajectory;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.DepthVarManager;
import managers.VarianceManager;

import java.util.Collection;

public class ProdDistAllEuclidTask extends ProdDistEuclidTask {

    private double scale;

    private final double f_x = CalibrationManager.depth_focal_len().x();
    private final double f_y = CalibrationManager.depth_focal_len().y();
    private final double p_x = CalibrationManager.depth_principal_point().x();
    private final double p_y = CalibrationManager.depth_principal_point().y();

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

        double dt_ab = dt_a.sub(dt_b).length_squared();///scale;
        double sigma_t = EPS_FLOW + getVariance(frame_idx, a, b);

        return dt_ab / sigma_t;
    }

    @Override
    protected double getVariance(int frame_idx, Trajectory a, Trajectory b) {
        Point2d pa = a.getPointAtFrame(frame_idx);
        Point2d pb = b.getPointAtFrame(frame_idx);

        // var_u = var_v since var = (var_u + var_v)/2 computed in `../initialization/init_data.m`
        double var_a = VarianceManager.getInstance().getVariance(frame_idx).valueAt(pa);
        double var_b = VarianceManager.getInstance().getVariance(frame_idx).valueAt(pb);

        double var_d_a = DepthVarManager.getInstance().get(frame_idx).valueAt(pa.x(), pa.y());
        double var_d_b = DepthVarManager.getInstance().get(frame_idx).valueAt(pb.x(), pb.y());

        double d_a = DepthManager.getInstance().get(frame_idx).valueAt(pa.x(), pa.y());
        double d_b = DepthManager.getInstance().get(frame_idx).valueAt(pb.x(), pb.y());

        var_a = var3d(pa, var_a, var_d_a, d_a);
        var_b = var3d(pb, var_b, var_d_b, d_b);

        return Math.min(var_a, var_b);
    }

    private double var3d(Point2d p, double var_uv, double var_d, double d) {
        double scale_u = (p.u() - p_x) / f_x;
        double scale_v = (p.v() - p_y) / f_y;
        double fx_2 = f_x*f_x;
        double fy_2 = f_y*f_y;

        double var_x = scale_u*scale_u*var_d + ((var_uv*d*d)/fx_2) + (1d/fx_2)*var_uv*var_d;
        double var_y = scale_v*scale_v*var_d + ((var_uv*d*d)/fy_2) + (1d/fy_2)*var_uv*var_d;

        return (var_x + var_y + var_d)/3d;
    }
}

package similarity;

import datastructures.Point3d;
import datastructures.Trajectory;
import java.util.Collection;

public class ProdDistEuclidTask extends ProdDistTask {
    /**
     * @param a
     * @param trajectories
     */
    public ProdDistEuclidTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
    }

    /**
     * Compute the spatial distance between two trajectory points overlapping at a certain given frame.
     *
     * @param a Trajectory
     * @param b Trajectory
     * @param frame_idx frame index where trajectory frames are overlapping.
     * @return spatial distance of overlapping trajectory points.
     */
    @Override
    protected double spatialDistBetween(Trajectory a, Trajectory b, int frame_idx) {
        Point3d pa = a.getEuclidPositionAtFrame(frame_idx);
        Point3d pb = b.getEuclidPositionAtFrame(frame_idx);
        return pa.copy().sub(pb).length();
    }

    @Override
    protected boolean exceededSpatialTol(double dist) {
        return dist > 2d;
    }
}

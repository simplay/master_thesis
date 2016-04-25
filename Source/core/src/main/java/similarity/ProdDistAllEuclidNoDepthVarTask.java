package similarity;

import datastructures.Trajectory;

import java.util.Collection;

public class ProdDistAllEuclidNoDepthVarTask extends ProdDistAllEuclidTask {
    /**
     * @param a
     * @param trajectories
     */
    public ProdDistAllEuclidNoDepthVarTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
    }

    @Override
    protected double getVariance(int frame_idx, Trajectory a, Trajectory b) {
        return 1d;
    }
}

package similarity;

import datastructures.Point2d;
import datastructures.Trajectory;
import java.util.Collection;

public class ProdDistTask extends SimilarityTask {

    /**
     * @param a
     * @param trajectories
     */
    public ProdDistTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
    }

    @Override
    protected double similarityBetween(Trajectory a, Trajectory b) {
        // handle eigen-similarity case
        if (a == b) {
            return EIGENSIMILARITY_VALUE;
        }

        int from_idx = getLowerFrameIndexBetween(a,b);
        int to_idx = getUpperFrameIndexBetween(a,b);

        return spatioTemporalDistances(a, b, from_idx, to_idx);
    }

    protected double spatioTemporalDistances(Trajectory a, Trajectory b, int from_idx, int to_idx) {
        int commonFrameCount = overlappingFrameCount(from_idx, to_idx);
        if (isTooShortOverlapping(commonFrameCount)) {
            return 0;
        }

        // is <= MIN_TIMESTEP_SIZE
        int timestep = timestepSize(commonFrameCount);
        int u = to_idx-timestep;

        // guard: in case there is no overlapping segment, skip computations
        if (u < from_idx || timestep == 0) {
            return 0;
        }

        double maxDistance = 0;
        double avgSpatialDist = 0;
        double len = u - from_idx + 1;

        for (int l = from_idx; l <= u; l++) {

            Point2d pa = a.getPointAtFrame(l);
            Point2d pb = b.getPointAtFrame(l);

            if (trajectoryPointsInvalid(pa, pb)) {
                len--;
                continue;
            }

            avgSpatialDist += spatialDistBetween(a, b, l);

            Point2d dt_a = forward_difference(a, timestep, l);
            Point2d dt_b = forward_difference(b, timestep, l);

            double dt_ab = dt_a.sub(dt_b).length_squared();
            double sigma_t = EPS_FLOW + getVariance(l, a, b);

            double dist = dt_ab / sigma_t;
            if (dist > maxDistance) {
                maxDistance = dist;
            }
        }

        // this check is required when using depth cues:
        // the points of overlapping trajectories may be all invalid,
        // resulting in len == 0, which would result in a NaN similarity assignment.
        if (len == 0) return 0d;

        avgSpatialDist = avgSpatialDist / len;
        appendAvgSpatialDistances(a, b, avgSpatialDist);
        double dist_st_a_b = avgSpatialDist*maxDistance;
        double w_ab = Math.exp(-LAMBDA*dist_st_a_b);
        return (w_ab < ZERO_THRESHOLD) ? 0d : w_ab;
    }

    /**
     * Compute the spatial distance between two trajectory points overlapping at a certain given frame.
     *
     * @param a Trajectory
     * @param b Trajectory
     * @param frame_idx frame index where trajectory frames are overlapping.
     * @return spatial distance of overlapping trajectory points.
     */
    protected double spatialDistBetween(Trajectory a, Trajectory b, int frame_idx) {
        Point2d pa = a.getSpatialPointAtFrame(frame_idx);
        Point2d pb = b.getSpatialPointAtFrame(frame_idx);
        return pa.copy().sub(pb).length();
    }
}

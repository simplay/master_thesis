package similarity;

import datastructures.Point2d;
import datastructures.Trajectory;
import pipeline_components.ArgParser;

import java.util.Collection;

public class ProdDistTask extends SimilarityTask {

    // Affinity scaling factor, acts as a sensivity parameter
    protected double lamdba_scale;

    /**
     * @param a
     * @param trajectories
     */
    public ProdDistTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
        this.lamdba_scale = ArgParser.getLambda();
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

            double dist = d_motion(a, b, timestep, l);
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
        double w_ab = Math.exp(-lamdba_scale*dist_st_a_b);
        if (exceededSpatialTol(avgSpatialDist)) return 0d;
        return (w_ab < ZERO_THRESHOLD) ? 0d : w_ab;
    }

    protected boolean exceededSpatialTol(double dist) {
        return false;
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
    protected double d_motion(Trajectory a, Trajectory b, int timestep, int frame_idx) {
        Point2d dt_a = forward_difference(a, timestep, frame_idx);
        Point2d dt_b = forward_difference(b, timestep, frame_idx);

        double dt_ab = dt_a.sub(dt_b).length_squared();
        double sigma_t = EPS_FLOW + getVariance(frame_idx, a, b);

        return dt_ab / sigma_t;
    }

}

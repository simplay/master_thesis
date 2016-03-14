import java.util.Collection;
import java.util.regex.Matcher;

public class SumDistTask extends SimilarityTask {


    /**
     * @param a
     * @param trajectories
     */
    public SumDistTask(Trajectory a, Collection<Trajectory> trajectories) {
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

        double dist_st_a_b = spatioTemporalDistances(a, b, from_idx, to_idx);


        return dist_st_a_b* Math.random();
    }

    protected double spatioTemporalDistances(Trajectory a, Trajectory b, int from_idx, int to_idx) {

        int commonFrameCount = overlappingFrameCount(from_idx, to_idx);
        if (isTooShortOverlapping(commonFrameCount)) {
            return 0;
        }

        int timestep = timestepSize(commonFrameCount);
        double dist_spatial = 0;

        int u = to_idx-timestep;

        if (u < from_idx) {
            return 0;
        }

        double max = 0;
        double avgDist = 0;
        double len = u - from_idx + 1;
        for (int l = from_idx; l <= u; l++) {

            Point2f pa = a.getPointAtFrame(l);
            Point2f pb = b.getPointAtFrame(l);

            double sp_len = pa.copy().sub(pb).length();
            avgDist += sp_len;

            Point2f dt_a = forward_difference(a, timestep, l);
            Point2f dt_b = forward_difference(b, timestep, l);

            double dt_ab = dt_a.sub(dt_b).length_squared();

            // TODO implement variance lookup and accumulate it here
            double sigma_t = EPS_FLOW;


            double dist = dt_ab / sigma_t;
            if (dist > max) {
                max = dist;
            }
        }
        avgDist = avgDist / len;
        return avgDist*max;
    }
}

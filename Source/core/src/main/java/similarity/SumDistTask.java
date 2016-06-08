package similarity;

import datastructures.ColorImage;
import datastructures.Point2d;
import datastructures.Point3d;
import datastructures.Trajectory;
import managers.ColorImgManager;
import pipeline_components.ArgParser;
import java.util.Collection;

/**
 * SumDistTask is a SimilarityTask that computes affinities by making
 * use of the motion- color- and spatial distances between trajectories.
 *
 * Only overlapping trajectory parts (their associated tracking points) are considered.
 *
 * The actual affinity value between two trajectories is computed by
 * calculating the a weighted sum of all the mentioned distances.
 *
 * The motion distance is the max. motion distance between the overlapping trajectory parts, whereas
 * the motion distance is the difference of the two flow forward differences.
 *
 * The color distance is the average l2 norm between the color values at the overlapping trajectory parts.
 *
 * The spatial distance is the average spatial distance between the overlapping trajectory parts.
 *
 * For further information please have a look into the following paper:
 * `Motion Trajectory Segmentation via Minimum Cost Multicuts - T. Brox et al`
 */
public class SumDistTask extends SimilarityTask {

    // Constants defined in Motion Trajectory Segmentation via Min. Cost Multicuts Used in formula (7)
    private final double BETA_0_TILDE = 6.0d;
    private final double BETA_0 = 2.0d;
    private final double BETA_1 = -0.02d; // when running euclid -0.2 works better
    private final double BETA_2 = -4d; // when running euclid -0.4 works better
    private final double BETA_3 = -0.02d;

    // cut probabilities
    private double P_BAR;
    private final double P = 0.5d;
    private double prior_probability;

    /**
     * @param a
     * @param trajectories
     */
    public SumDistTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
        P_BAR = ArgParser.getCutProbability();
        prior_probability = prior_probability();
    }

    @Override
    protected double similarityBetween(Trajectory a, Trajectory b) {

        // handle eigen-similarity case
        if (a == b) {
            return EIGENSIMILARITY_VALUE;
        }

        int from_idx = getLowerFrameIndexBetween(a,b);
        int to_idx = getUpperFrameIndexBetween(a,b);

        int commonFrameCount = overlappingFrameCount(from_idx, to_idx);
        if (isTooShortOverlapping(a, b, commonFrameCount)) {
            return 0;
        }

        double d_motion = motion_dist(a, b, from_idx, to_idx);
        double d_spatial = avg_spatial_dist(a, b, from_idx, to_idx);
        double d_color = color_dist(a, b, from_idx, to_idx);

        appendAvgSpatialDistances(a, b, d_spatial);

        double z_ab = z_ab(d_motion, d_spatial, d_color);
        return z_ab - prior_probability;
    }

    protected double motion_dist(Trajectory a, Trajectory b, int from_idx, int to_idx) {
        int commonFrameCount = overlappingFrameCount(from_idx, to_idx);

        // is <= MIN_TIMESTEP_SIZE
        int timestep = timestepSize(commonFrameCount);
        int u = to_idx-timestep;

        // guard: in case there is no overlapping segment, skip computations
        if (u < from_idx || timestep == 0) {
            return 0;
        }

        double maxDistance = 0;
        for (int l = from_idx; l <= u; l++) {

            Point2d pa = a.getPointAtFrame(l);
            Point2d pb = b.getPointAtFrame(l);
            if (trajectoryPointsInvalid(pa, pb)) {
                continue;
            }

            Point2d dt_a = forward_difference(a, timestep, l);
            Point2d dt_b = forward_difference(b, timestep, l);

            double dt_ab = dt_a.sub(dt_b).length_squared();
            double sigma_t = EPS_FLOW + getVariance(l, a, b);

            double dist = dt_ab / sigma_t;
            if (dist > maxDistance) {
                maxDistance = dist;
            }
        }
        return Math.sqrt(maxDistance);
    }

    /**
     * Computes the average spatial distance between the tracked points of the overlapping frames
     * of two given trajectories.
     *
     * @param a
     * @param b
     * @param from_idx
     * @param to_idx
     * @return
     */
    protected double avg_spatial_dist(Trajectory a, Trajectory b, int from_idx, int to_idx) {
        double len = 0.0d;
        int visitedTrackingPoints = overlappingFrameCount(from_idx, to_idx);
        for (int idx = from_idx; idx <= to_idx; idx++) {
            Point2d pa = a.getPointAtFrame(idx);
            Point2d pb = b.getPointAtFrame(idx);
            if (trajectoryPointsInvalid(pa, pb)) {
                visitedTrackingPoints--;
                continue;
            }
            len = len + spatialDistBetween(a, b, idx);
        }
        if (visitedTrackingPoints == 0) return 0d;
        return len / visitedTrackingPoints;
    }

    /**
     * Compute the average color distance between the overlapping frames of two given trajectories.
     * Note that the colorspace we use is cie l*a*b
     *
     * @param a trajectory
     * @param b trajectory
     * @param from_idx overlapping frames start index
     * @param to_idx overlapping frames end index
     * @return average color distance
     */
    protected double color_dist(Trajectory a, Trajectory b, int from_idx, int to_idx) {
        double len = 0d;
        int visitedTrackingPoints = overlappingFrameCount(from_idx, to_idx);

        for (int idx = from_idx; idx <= to_idx; idx++) {
            Point2d pa = a.getPointAtFrame(idx);
            Point2d pb = b.getPointAtFrame(idx);

            // Skip invalid Points
            if (trajectoryPointsInvalid(pa, pb)) {
                visitedTrackingPoints--;
                continue;
            }

            ColorImage img = ColorImgManager.getInstance().get(from_idx);

            Point3d rgb_pa = img.valueAt(pa);
            Point3d rgb_pb = img.valueAt(pb);

            len += rgb_pa.copy().sub(rgb_pb).length();
        }
        if (visitedTrackingPoints == 0) return 0d;
        return len / overlappingFrameCount(from_idx, to_idx);
    }

    protected double z_ab(double d_motion, double d_spatial, double d_color) {
        double msc_dist = BETA_0_TILDE + BETA_1 * d_motion + BETA_2 * d_spatial + BETA_3 * d_color;
        double m_dist = BETA_0 + BETA_1 * d_motion;
        return Math.max(msc_dist, m_dist);
    }

    protected double prior_probability() {
        return (Math.log(P_BAR / (1.0 - P_BAR)) - Math.log(P / (1.0 - P)));
    }
}

import datastructures.*;
import junit.framework.Assert;
import managers.*;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import similarity.SimilarityTask;
import similarity.SumDistTask;

import java.lang.reflect.Field;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

public class SumDistTaskTest {

    private SumDistTaskHelper nullProdDistTask;
    private final double eps = 1e-9;

    class SumDistTaskHelper extends SumDistTask {

        /**
         * @param a
         * @param trajectories
         */
        public SumDistTaskHelper(Trajectory a, Collection<Trajectory> trajectories) {
            super(a, trajectories);
        }

        public double p_z_ab(double d_motion, double d_spatial, double d_color) {
            return z_ab(d_motion, d_spatial, d_color);
        }

        public double p_prior_probability() {
            return prior_probability();
        }

        public double p_color_dist(Trajectory a, Trajectory b, int from_idx, int to_idx) {
            return color_dist(a, b, from_idx, to_idx);
        }

        public double p_avg_spatial_dist(Trajectory a, Trajectory b, int from_idx, int to_idx) {
            return avg_spatial_dist(a, b, from_idx, to_idx);
        }

        public double p_motion_dist(Trajectory a, Trajectory b, int from_idx, int to_idx) {
            return motion_dist(a, b, from_idx, to_idx);
        }

        public double p_similarityBetween(Trajectory a, Trajectory b) {
            return similarityBetween(a, b);
        }
    }

    private void setTimestep(int stepsize) {
        Field field = null;
        try {
            field = SimilarityTask.class.getDeclaredField("MIN_TIMESTEP_SIZE");
            field.setAccessible(true);
            try {
                field.set(nullProdDistTask, stepsize);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void initialize() {
        ArgParser.release();
        FlowFieldManager.release();
        CalibrationManager.release();
        InvalidRegionManager.release();
        TrackingCandidates.release();
        TrajectoryManager.release();
        VarianceManager.release();
        MetaDataManager.release();
        ColorImgManager.release();
        String[] args = {"-d", "dummy", "-task", "1", "-prob", "0.5"};
        ArgParser.getInstance(args);
        nullProdDistTask = new SumDistTaskHelper(null, null);
    }

    @Test
    public void testPrior_probability_prob_0_5_case_is_0() {
        ArgParser.release();
        String[] args = {"-d", "dummy", "-task", "1", "-prob", "0.5"};
        ArgParser.getInstance(args);
        nullProdDistTask = new SumDistTaskHelper(null, null);
        assertEquals(0, nullProdDistTask.p_prior_probability(), eps);
    }

    @Test
    public void testPrior_probability_prob_random_works() {
        int N = 10;
        for (int n = 0; n < N; n++) {
            ArgParser.release();
            double prob = Math.random();
            String[] args = {"-d", "dummy", "-task", "1", "-prob", String.valueOf(prob)};
            ArgParser.getInstance(args);
            nullProdDistTask = new SumDistTaskHelper(null, null);
            if (prob == 0) prob += eps;
            if (prob == 1) prob -= eps;
            double gtPriorProb = Math.log(prob / (1.0 - prob));
            assertEquals(gtPriorProb, nullProdDistTask.p_prior_probability(), eps);
        }
    }

    @Test
    public void testZ_ab_all_zero() {
        assertEquals(6, nullProdDistTask.p_z_ab(0, 0, 0), 0);
    }

    @Test
    public void testZ_ab_d_motion_1() {
        assertEquals(5.98d, nullProdDistTask.p_z_ab(1, 0, 0), 0);
    }

    @Test
    public void testZ_ab_d_spatial_1() {
        assertEquals(2, nullProdDistTask.p_z_ab(0, 1, 0), 0);
    }

    @Test
    public void testZ_ab_d_color_1() {
        assertEquals(5.98d, nullProdDistTask.p_z_ab(0, 0, 1), 0);
    }

    @Test
    public void testZ_ab_d() {
        double BETA_0_TILDE = 6.0d;
        double BETA_0 = 2.0d;
        double BETA_1 = -0.02d;
        double BETA_2 = -4d;
        double BETA_3 = -0.02d;

        int N = 10;
        for (int n = 0; n < N; n++) {
            double d_motion = Math.random();
            double d_spatial = Math.random();
            double d_color = Math.random();

            double msc_dist = BETA_0_TILDE + BETA_1 * d_motion + BETA_2 * d_spatial + BETA_3 * d_color;
            double m_dist = BETA_0 + BETA_1 * d_motion;
            double z_gt = Math.max(msc_dist, m_dist);

            assertEquals(z_gt, nullProdDistTask.p_z_ab(d_motion, d_spatial, d_color), eps);
        }
    }

    @Test
    public void testColor_dist() {
        Trajectory a = new Trajectory(0);
        Trajectory b = new Trajectory(0);

        int N = 3;
        int colImgCount = 3;
        ColorImage[] imgs = new ColorImage[colImgCount];
        for (int idx = 0; idx < colImgCount; idx++) {
            ColorImage img = new ColorImage(3, 3);
            for (int m = 0; m < N; m++) {
                for (int n = 0; n < N; n++) {
                    Point3d p = new Point3d(Math.random(), Math.random(), Math.random());
                    img.setElement(p, m, n);
                }
            }
            imgs[idx] = img;
            ColorImgManager.getInstance().add(img);
        }

        for (int idx = 0; idx < colImgCount; idx++) {
            a.addPoint(new Point2d(1, 1));
            b.addPoint(new Point2d(1 + Math.random(), 1 + Math.random()));
        }

        a.markClosed();
        b.markClosed();

        double d = 0;
        for (int idx = 0; idx < colImgCount; idx++) {
            ColorImage img = imgs[idx];
            Point3d colA = img.valueAt(a.getPointAtFrame(idx));
            Point3d colB = img.valueAt(b.getPointAtFrame(idx));
            d += colA.copy().sub(colB).length();
        }

        double gtColDist = d / 3;
        assertEquals(gtColDist, nullProdDistTask.p_color_dist(a, b, 0, 2), eps);
    }


    @Test
    public void testColor_dist_usingTestScenario() {
        TestScenarioBuilder.build();
        Object[] trajectories = TrajectoryManager.getTrajectories().toArray();
        Trajectory a = (Trajectory) trajectories[0];
        Trajectory b = (Trajectory) trajectories[1];

        double[] colDiff = {0.07, 0.1, 0.13, 0.16, 0.19, 0.22, 0.25};

        double partialSum = 0d;
        for (int k = 0; k < colDiff.length; k++) {
            partialSum += colDiff[k];
            double gt = partialSum / (k + 1);
            assertEquals(gt, nullProdDistTask.p_color_dist(a, b, 0, k), eps);
        }
    }

    @Test
    public void testAvg_spatial_dist() {
        TestScenarioBuilder.build();
        Object[] trajectories = TrajectoryManager.getTrajectories().toArray();
        Trajectory a = (Trajectory) trajectories[0];
        Trajectory b = (Trajectory) trajectories[1];

        double[] elementwiseSpDist = {Math.sqrt(2), Math.sqrt(2), 2, 2, Math.sqrt(5), Math.sqrt(10), Math.sqrt(17)};

        double partialSum = 0d;
        for (int k = 0; k < elementwiseSpDist.length; k++) {
            partialSum += elementwiseSpDist[k];
            double gt = partialSum / (k + 1);
            assertEquals(gt, nullProdDistTask.p_avg_spatial_dist(a, b, 0, k), eps);
        }
    }

    @Test
    public void testMotion_dist() {
        setTimestep(1);
        TestScenarioBuilder.build();
        Object[] trajectories = TrajectoryManager.getTrajectories().toArray();
        Trajectory a = (Trajectory) trajectories[0];
        Trajectory b = (Trajectory) trajectories[1];

        double[] elementwiseSpDist = {0, 2, 0, 1, 1, 1};

        for (int k = 1; k < elementwiseSpDist.length; k++) {
            double max = -1;
            for (int t = 0; t < k; t++) {
                double val = elementwiseSpDist[t];
                if (val > max) {
                    max = val;
                }
            }
            double gt = Math.sqrt(max / 2);
            assertEquals(gt, nullProdDistTask.p_motion_dist(a, b, 0, k), eps);
        }
    }

    @Test
    public void testSimilarityBetween() {
        setTimestep(1);
        TestScenarioBuilder.build();
        Object[] trajectories = TrajectoryManager.getTrajectories().toArray();
        Trajectory a = (Trajectory) trajectories[0];
        Trajectory b = (Trajectory) trajectories[1];

        double[] elementwiseMoDist = {0, 2, 0, 1, 1, 1};
        double[] elementwiseSpDist = {Math.sqrt(2), Math.sqrt(2), 2, 2, Math.sqrt(5), Math.sqrt(10), Math.sqrt(17)};
        double[] colDiff = {0.07, 0.1, 0.13, 0.16, 0.19, 0.22, 0.25};

        double d_color = 0d;
        for (int k = 0; k < colDiff.length; k++) {
            d_color += colDiff[k];
        }
        d_color = d_color / 7;


        double d_sp = 0;
        for (int k = 0; k < elementwiseSpDist.length; k++) {
            d_sp += elementwiseSpDist[k];
        }
        d_sp = d_sp / 7;

        double d_motion = 0;
        for (int k = 1; k < elementwiseSpDist.length; k++) {
            double max = -1;
            for (int t = 0; t < k; t++) {
                double val = elementwiseMoDist[t];
                if (val > max) {
                    max = val;
                }
            }
            d_motion = Math.sqrt(max / 2);
        }

        double gt = nullProdDistTask.p_z_ab(d_motion, d_sp, d_color);
        assertEquals(gt, nullProdDistTask.p_similarityBetween(a, b), eps);
    }

}

import datastructures.TrackingCandidates;
import datastructures.Trajectory;
import managers.*;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import similarity.SumDistTask;

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


}

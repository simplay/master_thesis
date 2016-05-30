import datastructures.*;
import managers.*;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import similarity.ProdDistTask;
import java.util.Collection;
import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;

public class ProdDistTaskTest {

    private ProdDistTaskHelper nullProdDistTask;
    private final double eps = 1e-9;

    class ProdDistTaskHelper extends ProdDistTask {

        /**
         * @param a
         * @param trajectories
         */
        public ProdDistTaskHelper(Trajectory a, Collection<Trajectory> trajectories) {
            super(a, trajectories);
        }

        public double p_d_motion(Trajectory a, Trajectory b, int timestep, int frame_idx) {
            return d_motion(a, b, timestep, frame_idx);
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

        int N = 10;
        double[][] blur = new double[3][3];
        blur[0][0] = 0.125d; blur[0][1] = 0.25d; blur[0][2] = 0.125d;
        blur[1][0] = 0.25d; blur[1][1] = 0.5d; blur[1][2] = 0.25d;
        blur[2][0] = 0.125d; blur[2][1] = 0.25d; blur[2][2] = 0.125d;

        for (int k = 0; k < blur.length; k++) {
            for (int l = 0; l < blur[0].length; l++) {
                blur[k][l] *= 4;
            }
        }

        // Generate N-3 flow fields
        for (int i = 0; i < N - 3; i++) {
            FlowField fw = new FlowField(N, N, FlowField.FORWARD_FLOW);
            double row1[] = new double[N];
            double row2[] = new double[N];
            double row3[] = new double[N];

            double[] zeroRow = new double[N];
            for (int k = 0; k < N; k++) {
                zeroRow[k] = 0;
            }

            for (int k = 0; k < N; k++) {
                fw.setRow(k, zeroRow, zeroRow);
            }

            for (int k = 0; k < blur.length; k++) {
                row1[k + i] = blur[0][k];
                row2[k + i] = blur[1][k];
                row3[k + i] = blur[2][k];
            }
            fw.setRow(i, row1, row1);
            fw.setRow(i + 1, row2, row2);
            fw.setRow(i + 2, row3, row3);

            FlowFieldManager.getInstance().addForwardFlow(fw);
        }

        // Generate N-3 flow var fields
        for (int i = 0; i < N - 3; i++) {
            FlowVarField fvf = new FlowVarField(N, N);

            double row1[] = new double[N];
            double row2[] = new double[N];
            double row3[] = new double[N];

            double[] zeroRow = new double[N];
            for (int k = 0; k < N; k++) {
                zeroRow[k] = 0;
            }

            for (int k = 0; k < N; k++) {
                fvf.setRow(k, zeroRow);
            }

            for (int k = 0; k < blur.length; k++) {
                row1[k + i] = 0.25;
                row2[k + i] = 0.25;
                row3[k + i] = 0.25;
            }
            fvf.setRow(i, row1);
            fvf.setRow(i + 1, row2);
            fvf.setRow(i + 2, row3);

            VarianceManager.getInstance().add(fvf);
        }

        // Generate N - 3 Tracking candidates: Only use candidates that have a valid flow
        for (int i = 0; i < N - 3; i++) {
            LinkedList<String> tmpRows = new LinkedList<>();
            LinkedList<String> tmpCols = new LinkedList<>();
            FlowField fwf = FlowFieldManager.getInstance().getForwardFlow(i);
            for (int k = 0; k < N; k++) {
                for (int l = 0; l < N; l++) {
                    if (fwf.v_valueAt(k, l) > 0d) {
                        tmpRows.add(String.valueOf(k + 1));
                        tmpCols.add(String.valueOf(l + 1));
                    }
                }
            }

            String rows[] = new String[tmpRows.size()];
            String columns[] = new String[tmpCols.size()];
            for (int k = 0; k < tmpCols.size(); k++) {
                rows[k] = tmpRows.get(k);
                columns[k] = tmpCols.get(k);
            }
            TrackingCandidates.getInstance().addCandidates(rows, columns);
        }

        for (int i = 0; i < N - 3; i++) {
            InvalidRegionsMask mask = new InvalidRegionsMask(N, N);
            FlowField fwf = FlowFieldManager.getInstance().getForwardFlow(i);
            for (int k = 0; k < N; k++) {
                for (int l = 0; l < N; l++) {
                    if (fwf.v_valueAt(k, l) == 0d) {
                        mask.setAt(k, l, 1);
                    }
                }
            }

            InvalidRegionManager.getInstance().addInvalidRegion(mask);
        }

        String[] args = {"-d", "foobar", "-task", "2", "-var", "1"};
        ArgParser.getInstance(args);

        new Tracker(7, 1);
        nullProdDistTask = new ProdDistTaskHelper(null, null);
    }

    @Test
    public void testD_motionSimple() {
        Collection<Trajectory> trajectories = TrajectoryManager.getTrajectories();

        // s = 0, l = 1
        // a = [(0, 0), (0.5, 0.5)]
        Trajectory a = (Trajectory) trajectories.toArray()[0];

        // s = 0, l = 2
        //        0        1       2       3       4       5       6      7
        // b = [(0, 1), (1, 2), (2, 3), (3, 4), (4, 5), (5, 6), (6, 7), (7, 8)]
        Trajectory b = (Trajectory) trajectories.toArray()[1];

        // a => d_a0 = (p1 - p0) / dt = (0.5, 0.5)
        // b => d_b0 = (1, 2) - (0, 1) = (1, 1)
        // d_ab = ||d_a0 - d_b0||^2 =
        // 0.4 = 1/2 * 4/5 = 0.5 / 1.25 = (2*(1 - 0.5)**2 ) / (EPS + 0.25)
        double gt_d_motion_ab_s_1_f_0 = 0.4;
        assertEquals(gt_d_motion_ab_s_1_f_0, nullProdDistTask.p_d_motion(a, b, 1, 0), eps);
    }

    @Test
    public void testD_motionFullExample() {
        Collection<Trajectory> trajectories = TrajectoryManager.getTrajectories();

        // s = 0, l = 2
        //        0        1       2       3       4       5       6      7
        // b = [(0, 1), (1, 2), (2, 3), (3, 4), (4, 5), (5, 6), (6, 7), (7, 8)]
        Trajectory b = (Trajectory) trajectories.toArray()[1];

        // s = 3, l = 19
        //        3         4             5                  6                                7
        // c = [(5, 5), (5.5, 5.5), (6.625, 6.625), (7.5703125, 7.5703125), (8.592315673828125, 8.592315673828125)]
        Trajectory c = (Trajectory) trajectories.toArray()[18];

        // (4, 5) - (3, 4) = (1, 1)
        // (5.5, 5.5) - (5, 5) = (0.5, 0.5) => d_bc = d_ab
        double gt_d_motion_bc_s_1_f_3 = 0.4;
        assertEquals(gt_d_motion_bc_s_1_f_3, nullProdDistTask.p_d_motion(b, c, 1, 3), eps);

        // (6.625, 6.625) - (5.5, 5.5) = (1.125, 1.125)
        // ... = (1, 1)
        // 2*0.125**2 / 1.25 = 1/32 * 4/5 = 1/40
        double gt_d_motion_bc_s_1_f_4 = 0.025d;
        assertEquals(gt_d_motion_bc_s_1_f_4, nullProdDistTask.p_d_motion(b, c, 1, 4), eps);

        // (7.5703125, 7.5703125) - (6.625, 6.625) = (0.9453125, 0.9453125)
        // diff = (1-0.9453125) = 0.0546875 => 2*diff**2 / 1.25 =  (0.0059814453125 / 1.25) = 0.00478515625
        double gt_d_motion_bc_s_1_f_5 = 0.00478515625d;
        assertEquals(gt_d_motion_bc_s_1_f_5, nullProdDistTask.p_d_motion(b, c, 1, 5), eps);

        double gt_d_motion_bc_s_1_f_6 = 7.746234536170959E-4;
        assertEquals(gt_d_motion_bc_s_1_f_6, nullProdDistTask.p_d_motion(b, c, 1, 6), eps);
    }

}

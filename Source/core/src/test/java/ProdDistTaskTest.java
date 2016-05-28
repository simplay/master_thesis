import datastructures.*;
import managers.*;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import similarity.ProdDistTask;
import java.util.Collection;

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
                row1[k + i] = 0.1;
                row2[k + i] = 0.1;
                row3[k + i] = 0.1;
            }
            fvf.setRow(i, row1);
            fvf.setRow(i + 1, row2);
            fvf.setRow(i + 2, row3);

            VarianceManager.getInstance().add(fvf);
        }

        // Generate N - 3 Tracking candidates
        for (int i = 0; i < N - 3; i++) {
            String rows[] = new String[N * N];
            String columns[] = new String[N * N];
            int count = 0;
            for (int k = 0; k < N; k++) {
                for (int l = 0; l < N; l++) {
                    rows[count] = String.valueOf(k + 1);
                    columns[count] = String.valueOf(l + 1);
                    count++;
                }
            }
            TrackingCandidates.getInstance().addCandidates(rows, columns);
        }

        for (int i = 0; i < N - 3; i++) {
            InvalidRegionsMask mask = new InvalidRegionsMask(N, N);
            InvalidRegionManager.getInstance().addInvalidRegion(mask);
        }

        String[] args = {"-d", "foobar", "-task", "2", "-var", "1"};
        ArgParser.getInstance(args);

        new Tracker(7, 1);

        nullProdDistTask = new ProdDistTaskHelper(null, null);
    }

    @Test
    public void testD_motion() {
        Collection<Trajectory> trajectories = TrajectoryManager.getTrajectories();
        Trajectory a = (Trajectory) trajectories.toArray()[0];
        Trajectory b = (Trajectory) trajectories.toArray()[1];

        // (0.125*0.125*2)/1.1 = (2*(0.25 - 0.125)**2 ) / (EPS + 0.1)
        double gtFlow = (0.125 * 0.125 * 2) / 1.1;
        assertEquals(gtFlow, nullProdDistTask.p_d_motion(a, b, 1, 0), eps);
    }

}

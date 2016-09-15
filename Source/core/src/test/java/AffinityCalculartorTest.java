import datastructures.*;
import managers.*;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.AffinityCalculator;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import java.util.Collection;
import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;

public class AffinityCalculartorTest {

    private final double eps = 1e-9;
    private double lambda = 1.1;

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
                blur[k][l] = 1d/(k + l);
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

        String[] args = {"-d", "foobar", "-task", "2", "-var", "1", "-lambda", String.valueOf(lambda)};
        ArgParser.getInstance(args);

        new Tracker(7, 1);
    }

    @Test
    public void testCanRunSuccessfullyAffinityCalculator() {
        Collection<Trajectory> trajectories = TrajectoryManager.getTrajectories();
        Trajectory a = (Trajectory) trajectories.toArray()[3];
        Trajectory b = (Trajectory) trajectories.toArray()[5];

        assertEquals(false, a.hasSimilarityValues());
        assertEquals(false, b.hasSimilarityValues());

        new AffinityCalculator();

        assertEquals(true, a.hasSimilarityValues());
        assertEquals(true, b.hasSimilarityValues());
    }
}

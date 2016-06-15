import datastructures.*;
import managers.*;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import similarity.SimilarityTask;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TestScenarioBuilder {

    // Number of fields in managers.
    private static int N;

    /**
     * Currently, only a test scene for an SD similarity task can be built.
     */
    public static void build() {
        releaseManagers();
        buildSDScene();
        new Tracker(N - 1, 1);
    }

    /**
     * Release all available managers to have a clean setup
     * used to build a proper, new test scenario.
     */
    private static void releaseManagers() {
        ArgParser.release();
        FlowFieldManager.release();
        CalibrationManager.release();
        InvalidRegionManager.release();
        TrackingCandidates.release();
        TrajectoryManager.release();
        VarianceManager.release();
        MetaDataManager.release();
        DepthManager.release();
        DepthVarManager.release();
        ColorImgManager.release();
    }

    /**
     * Given a 10 x 10 grid, with first index 0, last equals 9
     * The following trajectories are defined, starting at frame 0
     * a = ( (1,1), (2,1), (3,3), (4,4), (4,5), (5,6), (6,8) )
     * b = ( (2,0), (3,0), (3,1), (4,2), (5,3), (6,3), (7,4) )
     *
     * d_sp = (sqrt(2), sqrt(2), 2, 2, sqrt(5), sqrt(10), sqrt(17) )
     *
     * All have a flow variance value equals 1
     */
    private static void buildSDScene() {
        String[] args = {"-d", "dummy", "-task", "1", "-var", "1"};
        ArgParser.getInstance(args);

        N = 7;
        int m = 10; int n = 10;

        String[] rowCandidates = {"2", "3"};
        String[] colCandidates = {"2", "1"};
        TrackingCandidates.getInstance().addCandidates(rowCandidates, colCandidates);

        for (int k = 0; k < N - 1; k++) {
            String[] rCandidates = {};
            String[] cCandidates = {};
            TrackingCandidates.getInstance().addCandidates(rCandidates, cCandidates);
        }

        ArrayList<double[]> usAdaptions = new ArrayList<>();
        ArrayList<double[]> vsAdaptions = new ArrayList<>();
        ArrayList<int[]> is = new ArrayList<>();
        ArrayList<int[]> js = new ArrayList<>();

        double[] us0 = {1, 1}; double[] vs0 = {0, 0};
        int[] is0 = {1, 2}; int[] js0 = {1, 0};
        usAdaptions.add(us0); vsAdaptions.add(vs0);
        is.add(is0); js.add(js0);

        double[] us1 = {1, 0}; double[] vs1 = {2, 1};
        int[] is1 = {2, 3}; int[] js1 = {1, 0};
        usAdaptions.add(us1); vsAdaptions.add(vs1);
        is.add(is1); js.add(js1);

        double[] us2 = {1, 1}; double[] vs2 = {1, 1};
        int[] is2 = {3, 3}; int[] js2 = {3, 1};
        usAdaptions.add(us2); vsAdaptions.add(vs2);
        is.add(is2); js.add(js2);

        double[] us3 = {0, 1}; double[] vs3 = {1, 1};
        int[] is3 = {4, 4}; int[] js3 = {4, 2};
        usAdaptions.add(us3); vsAdaptions.add(vs3);
        is.add(is3); js.add(js3);

        double[] us4 = {1, 1}; double[] vs4 = {1, 0};
        int[] is4 = {4, 5}; int[] js4 = {5, 3};
        usAdaptions.add(us4); vsAdaptions.add(vs4);
        is.add(is4); js.add(js4);

        double[] us5 = {1, 1}; double[] vs5 = {2, 1};
        int[] is5 = {5, 6}; int[] js5 = {6, 3};
        usAdaptions.add(us5); vsAdaptions.add(vs5);
        is.add(is5); js.add(js5);

        // build forward flow field
        for (int t = 0; t < N - 1; t++) {

            // define empty flow field
            FlowField ff = new FlowField(m, n, FlowField.FORWARD_FLOW);
            double[][] us = new double[m][n];
            double[][] vs = new double[m][n];
            for (int i = 0; i < m; i++) {
                double[] u = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                double[] v = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                us[i] = u;
                vs[i] = v;
            }

            // update target flow indices with predefined values
            int[] ii = is.get(t);
            int[] jj = js.get(t);
            for (int k = 0; k < ii.length; k++) {
                int rowIdx = ii[k];
                int colIdx = jj[k];
                double uVal = usAdaptions.get(t)[k];
                double vVal = vsAdaptions.get(t)[k];

                us[rowIdx][colIdx] = uVal;
                vs[rowIdx][colIdx] = vVal;
            }

            // write forward flows into manager
            for (int i = 0; i < m; i++) {
                ff.setRow(i, us[i], vs[i]);
            }
            FlowFieldManager.getInstance().addForwardFlow(ff);
        }

        for (int idx = 0; idx < N; idx++) {
            InvalidRegionsMask invalideRegions = new InvalidRegionsMask(m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    invalideRegions.setAt(i, j, 0);
                }
            }
            InvalidRegionManager.getInstance().addInvalidRegion(invalideRegions);
        }

        // Generate Flow variances: Values are equals 1
        for (int idx = 0; idx < N; idx++) {
            FlowVarField var = new FlowVarField(m, n);
            for (int k = 0; k < m; k++) {
                double[] vars = {1,1,1,1,1,1,1,1,1,1};
                var.setRow(k, vars);
            }
            VarianceManager.getInstance().add(var);
        }

        // double[] colDiff = {0.07, 0.1, 0.13, 0.16, 0.19, 0.22, 0.25};
        ArrayList<Point3d[]> colorAdaptions = new ArrayList<>();
        Point3d[] colors0 = {new Point3d(0.1,0,0), new Point3d(0.03,0,0)};
        Point3d[] colors1 = {new Point3d(0.2,0,0), new Point3d(0.1,0,0)};
        Point3d[] colors2 = {new Point3d(0.3,0,0), new Point3d(0.17,0,0)};
        Point3d[] colors3 = {new Point3d(0.4,0,0), new Point3d(0.24,0,0)};
        Point3d[] colors4 = {new Point3d(0.5,0,0), new Point3d(0.31,0,0)};
        Point3d[] colors5 = {new Point3d(0.6,0,0), new Point3d(0.38,0,0)};
        Point3d[] colors6 = {new Point3d(0.7,0,0), new Point3d(0.45,0,0)};

        colorAdaptions.add(colors0);
        colorAdaptions.add(colors1);
        colorAdaptions.add(colors2);
        colorAdaptions.add(colors3);
        colorAdaptions.add(colors4);
        colorAdaptions.add(colors5);
        colorAdaptions.add(colors6);

        int[] is6 = {6, 7}; int[] js6 = {8, 4};
        is.add(is6); js.add(js6);

        for (int idx = 0; idx < N; idx++) {
            ColorImage img = new ColorImage(m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    Point3d rgb = new Point3d(0, 0, 0);
                    img.setElement(rgb, i, j);
                }
            }

            int[] ii = is.get(idx);
            int[] jj = js.get(idx);
            for (int k = 0; k < ii.length; k++) {
                img.setElement(colorAdaptions.get(idx)[k], ii[k], jj[k]);
            }

            ColorImgManager.getInstance().add(img);
        }

    }


}

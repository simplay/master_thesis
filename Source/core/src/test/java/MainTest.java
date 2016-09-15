import datastructures.TrackingCandidates;
import datastructures.Trajectory;
import managers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.LargeFileWriter;

import java.lang.reflect.Field;
import java.util.TreeMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class MainTest {

    private final LargeFileWriter filewriter = new LargeFileWriter();

    private Trajectory getTrajectoryByLabel(int labelId) {
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            if (tra.getLabel() == labelId) {
                return tra;
            }
        }
        return null;
    }

    private double similarityValueTo(Trajectory tra, int otherLabel) {
        Field field = null;
        try {
            field = Trajectory.class.getDeclaredField("similarities");
            field.setAccessible(true);
            try {
                TreeMap<Integer, Double> similarities = (TreeMap<Integer, Double>)field.get(tra);
                return similarities.get(otherLabel);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    @Before
    public void init() {
        ArgParser.release();
        TrajectoryManager.release();
        FlowFieldManager.release();
        VarianceManager.release();
        MetaDataManager.release();
        TrackingCandidates.release();
        InvalidRegionManager.release();
    }

    @After
    public void cleanup() {
        filewriter.deleteFile("../output/trajectory_label_frame/example/pd_top_5_core_test_run_active_tra_f_1.txt");
        filewriter.deleteFile("../output/trajectory_label_frame/example/pd_top_5_core_test_run_active_tra_f_2.txt");
        filewriter.deleteFile("../output/trajectory_label_frame/example/pd_top_5_core_test_run_active_tra_f_3.txt");
        filewriter.deleteFile("../output/trajectory_label_frame/example/pd_top_5_core_test_run_active_tra_f_4.txt");
        filewriter.deleteFile("../output/trajectory_label_frame/example/pd_top_5_core_test_run_active_tra_f_5.txt");
        filewriter.deleteFile("../output/similarities/example_pd_top_5_core_test_run_labels.txt");
        filewriter.deleteFile("../output/similarities/example_pd_top_5_core_test_run_spnn.txt");
        filewriter.deleteFile("../output/similarities/example_pd_top_5_core_test_run_spnn.txt");
    }

    @Test
    public void RunningMainWorks() throws InterruptedException {
        String[] args = {"-d", "example", "-task", "2", "-var", "1", "-prefix", "core_test_run", "-nnm", "top", "-nn", "5"};
        new Main().main(args);

        // tra 80 and 65 are both in the front most car.
        Trajectory tra80 = getTrajectoryByLabel(80);
        Trajectory tra65 = getTrajectoryByLabel(65);

        // small car in the background
        Trajectory tra14 = getTrajectoryByLabel(14);
        Trajectory tra22 = getTrajectoryByLabel(22);

        // background trees
        Trajectory tra69 = getTrajectoryByLabel(69);
        Trajectory tra76 = getTrajectoryByLabel(76);

        assertEquals(similarityValueTo(tra65, tra80.getLabel()) , similarityValueTo(tra80, tra65.getLabel()), 0);

        // front care has large sim to other trajectory on front care but small to others
        assertTrue(similarityValueTo(tra80, tra65.getLabel()) > 0.95d);
        assertTrue(similarityValueTo(tra80, tra14.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra80, tra22.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra80, tra69.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra80, tra76.getLabel()) < 0.15d);

        // front care has large sim to other trajectory on front care but small to others
        assertTrue(similarityValueTo(tra65, tra80.getLabel()) > 0.95d);
        assertTrue(similarityValueTo(tra65, tra22.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra65, tra14.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra65, tra69.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra65, tra76.getLabel()) < 0.15d);

        // tree background has large sim to other trajectory on tree background but small to others
        assertTrue(similarityValueTo(tra22, tra14.getLabel()) > 0.95d);
        assertTrue(similarityValueTo(tra22, tra80.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra22, tra65.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra22, tra69.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra22, tra76.getLabel()) < 0.15d);

        // tree background has large sim to other trajectory on tree background but small to others
        assertTrue(similarityValueTo(tra14, tra22.getLabel()) > 0.95d);
        assertTrue(similarityValueTo(tra14, tra80.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra14, tra65.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra14, tra69.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra14, tra76.getLabel()) < 0.15d);

        // background car has large sim to other trajectory on background car but small to others
        assertTrue(similarityValueTo(tra69, tra76.getLabel()) > 0.95d);
        assertTrue(similarityValueTo(tra69, tra80.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra69, tra65.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra69, tra22.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra69, tra14.getLabel()) < 0.15d);

        // background car has large sim to other trajectory on background car but small to others
        assertTrue(similarityValueTo(tra76, tra69.getLabel()) > 0.95d);
        assertTrue(similarityValueTo(tra76, tra80.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra76, tra65.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra76, tra22.getLabel()) < 0.15d);
        assertTrue(similarityValueTo(tra76, tra14.getLabel()) < 0.15d);
    }

    @Test
    public void testAffinityMatrixIsSymmetric() {
        String[] args = {"-d", "example", "-task", "2", "-var", "1", "-prefix", "core_test_run", "-nnm", "top", "-nn", "5"};
        new Main().main(args);

        for (Trajectory a : TrajectoryManager.getTrajectories()) {
            for (Trajectory b : TrajectoryManager.getTrajectories()) {
                double abSim = similarityValueTo(a, b.getLabel());
                double baSim = similarityValueTo(b, a.getLabel());
                assertEquals(abSim, baSim, 0);
            }
        }
    }

    @Test
    public void testDiagonalAffinityMatrixIsZero() {
        String[] args = {"-d", "example", "-task", "2", "-var", "1", "-prefix", "core_test_run", "-nnm", "top", "-nn", "5"};
        new Main().main(args);

        for (Trajectory a : TrajectoryManager.getTrajectories()) {
            double eigenSim = similarityValueTo(a, a.getLabel());
            assertEquals(0, eigenSim, 0);
        }
    }

}

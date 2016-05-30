import datastructures.TrackingCandidates;
import managers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.LargeFileWriter;


public class MainTest {

    private final LargeFileWriter filewriter = new LargeFileWriter();

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
    }
}

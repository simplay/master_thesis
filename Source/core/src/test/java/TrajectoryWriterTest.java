import datastructures.Point2d;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.TrajectoryWriter;

import java.io.*;
import java.util.LinkedList;

public class TrajectoryWriterTest {

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);
    }

    @Test
    public void testCanWriteFileCorrectly() {
        LinkedList<String> gtStrings = new LinkedList<>();
        gtStrings.add("### L:1 S:1 C:3");
        gtStrings.add("1.1 1.2");
        gtStrings.add("1.3 1.4");
        gtStrings.add("1.5 1.6");
        gtStrings.add("1.7 1.8");

        TrajectoryManager.getInstance().startNewTrajectoryAt(new Point2d(1.1, 1.2), 0);
        TrajectoryManager.getInstance().appendPointTo(getLastTrajectory().getLabel(), new Point2d(1.3, 1.4));
        TrajectoryManager.getInstance().appendPointTo(getLastTrajectory().getLabel(), new Point2d(1.5, 1.6));
        TrajectoryManager.getInstance().appendPointTo(getLastTrajectory().getLabel(), new Point2d(1.7, 1.8));
        getLastTrajectory().markClosed();

        new TrajectoryWriter("foobar", 3, "./testdata/");

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("./testdata/traj_out_foobar_sd_all_1_fc_3.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        try {
            int counter = 0;
            while ((strLine = br.readLine()) != null) {
                String gt = gtStrings.get(counter);
                Assert.assertEquals(gt.trim(), strLine.trim());
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Trajectory getLastTrajectory() {
        Object[] tras = TrajectoryManager.getTrajectories().toArray();
        return (Trajectory)tras[tras.length-1];
    }
}

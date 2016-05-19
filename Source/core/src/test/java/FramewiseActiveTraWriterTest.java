import datastructures.Point2d;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.FramewiseActiveTraWriter;
import java.io.*;
import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;

public class FramewiseActiveTraWriterTest {

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);
    }

    @Test
    public void testCanWriteFileCorrectly() {
        int N = 2;

        LinkedList<LinkedList<Point2d>> traPoints = new LinkedList<>();
        LinkedList<Point2d> traPoints0 = new LinkedList<>();
        LinkedList<Point2d> traPoints1 = new LinkedList<>();
        traPoints.add(traPoints0);
        traPoints.add(traPoints1);
        traPoints0.add(new Point2d(0, 1));
        traPoints0.add(new Point2d(2, 3));
        traPoints1.add(new Point2d(4, 5));

        TrajectoryManager.getInstance().startNewTrajectoryAt(traPoints0.get(0), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(traPoints0.get(1), 0);
        TrajectoryManager.getInstance().startNewTrajectoryAt(traPoints1.get(0), 1);

        LinkedList<LinkedList<String>> traLabelsList = new LinkedList<>();

        // active trajectories in frame
        for (int n = 0; n < N; n++) {
            LinkedList<String> traLabels = new LinkedList<>();
            for (Trajectory tra : TrajectoryManager.getInstance().getActivesForFrame(n)) {
                traLabels.add(String.valueOf(tra.getLabel()));
            }
            traLabelsList.add(traLabels);
        }
        TrajectoryManager.getInstance().toFramewiseOutputString(0);

        assertEquals("1 0.0 1.0\n2 2.0 3.0", TrajectoryManager.getInstance().toFramewiseOutputString(0));
        assertEquals("3 4.0 5.0", TrajectoryManager.getInstance().toFramewiseOutputString(1));

        new FramewiseActiveTraWriter("foobar", 2, "./testdata/");

        for (int n = 0; n < N; n++) {
            FileInputStream fstream = null;
            try {
                fstream = new FileInputStream("./testdata/foobar/sd_all_3_active_tra_f_" + (n+1) + ".txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;
            try {
                int counter = 0;
                while ((strLine = br.readLine()) != null) {
                    Point2d p = traPoints.get(n).get(counter);
                    String gtP = p.x() + " " + p.y();
                    String gt = traLabelsList.get(n).get(counter) + " " + gtP;
                    Assert.assertEquals(gt.trim(), strLine);
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
    }
}

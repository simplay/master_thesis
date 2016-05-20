import datastructures.Point2d;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.Logger;
import similarity.ProgressBar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class ProgressBarTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void initObjects() {
        Logger.release();
        ProgressBar.release();
        TrajectoryManager.release();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testReportStatus() throws IOException {
        int N = 1000;

        // Start N trajectories
        for (int n = 0; n < N; n++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(Point2d.one(), 0);
        }

        int finishedTasks = 0;
        for (int n = 0; n < N; n++) {
            if (n % 200 == 0 && n > 0) {
                finishedTasks = fetchedFinishTask();
                double percentage = ((double)finishedTasks / (double)N) * 100d;
                String report = "+ Progress: " + percentage + "%\n";
                String fetched = new String(outContent.toString());
                String[] items = fetched.split("Progress: ");
                fetched = "+ Progress: " + items[items.length-1];
                assertEquals(true, fetched.equals(report));
            }
            ProgressBar.reportStatus();
        }
        Logger.getInstance(false);
    }

    private int fetchedFinishTask() {
        Field field = null;
        try {
            field = ProgressBar.class.getDeclaredField("finishedTaskCount");
            field.setAccessible(true);
            try {
                return (int)field.get(ProgressBar.getInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

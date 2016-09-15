import datastructures.Point2d;
import datastructures.Trajectory;
import datastructures.TrajectoryExtender;
import managers.MetaDataManager;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrajectoryExtenderTest {

    @Before
    public void prepare() {
        TrajectoryManager.release();
        ArgParser.release();
        MetaDataManager.release();
        ArrayList<String> metaData = new ArrayList<String>();
        metaData.add("10");
        metaData.add("10");
        metaData.add("1");
        MetaDataManager.getInstance(metaData);
    }

    @Test
    public void testGetRightPointContinuationNIs3() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        ArrayList<Point2d> points = new ArrayList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(3);
        int N = 3;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(points.size() - 1).x() + (k+1)*step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(points.size() - 1).y() + (k+1)*step, eps);
        }
    }

    @Test
    public void testGetRightPointContinuationNIs2() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(2);
        int N = 2;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(points.size() - 1).x() + (k+1)*step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(points.size() - 1).y() + (k+1)*step, eps);
        }
    }

    @Test
    public void testGetRightPointContinuationNIs1() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(1);
        int N = 1;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(points.size() - 1).x() + (k+1)*step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(points.size() - 1).y() + (k+1)*step, eps);
        }
    }

    @Test
    public void testGetRightPointContinuationNIs0() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(0);
        assertTrue(leftAdds.isEmpty());
    }

    @Test
    public void testGetRightPointContinuationCanOnlyAppend2() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 2;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(3);
        assertEquals(2, leftAdds.size());
    }

    @Test
    public void testGetRightPointContinuationCanOnlyAppendOne() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 1;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(3);
        assertEquals(1, leftAdds.size());
    }

    @Test
    public void testGetRightPointContinuationCannotAppend() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size();
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getRightPointContinuation(3);
        assertEquals(0, leftAdds.size());
    }

    @Test
    public void testGetLeftPointContinuationNIs3() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(3);
        int N = 3;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(0).x() - (N-k)*step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(0).y() - (N-k)*step, eps);
        }

    }

    @Test
    public void testGetLeftPointContinuationNIs4() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(4);
        int N = 3;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(0).x() - (N-k)*step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(0).y() - (N-k)*step, eps);
        }

    }

    @Test
    public void testGetLeftPointContinuationNIs2() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(2);
        int N = 2;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(0).x() - (N - k) * step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(0).y() - (N - k) * step, eps);
        }
    }

    @Test
    public void testGetLeftPointContinuationNIs1() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(1);
        int N = 1;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(leftAdds.get(k).x(), points.get(0).x() - (N - k) * step, eps);
            assertEquals(leftAdds.get(k).y(), points.get(0).y() - (N - k) * step, eps);
        }
    }

    @Test
    public void testGetLeftPointContinuationNIs0() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(0);
        assertTrue(leftAdds.isEmpty());
    }

    @Test
    public void testGetLeftPointContinuationCanOnlyPrepend2() {
        int startFrame = 2;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(3);
        assertEquals(2, leftAdds.size());
    }

    @Test
    public void testGetLeftPointContinuationCanOnlyPrepend1() {
        int startFrame = 1;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(3);
        assertEquals(1, leftAdds.size());
    }

    @Test
    public void testGetLeftPointContinuationCanNotPrepend() {
        int startFrame = 0;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random();
        double ySeed = Math.random();
        double step = 0.1;
        double del = 0.0;
        for (int n = 0; n < 5; n++) {
            points.add(new Point2d(xSeed + del, ySeed + del));
            del += step;

        }

        for (Point2d p : points) {
            tra.addPoint(p);
        }
        tra.markClosed();

        int totalNumOfFrames = startFrame + points.size() + 3;
        MetaDataManager.getInstance().setFrameCount(totalNumOfFrames - 1);

        TrajectoryExtender te = new TrajectoryExtender(points, tra.getStartFrame(), tra.getEndFrame());
        ArrayList<Point2d> leftAdds = te.getLeftPointContinuation(3);
        assertEquals(0, leftAdds.size());
    }

    @Test
    public void testGenerateAdditions() {
        double eps = 1e-9;
        int N = 10;
        for (int k = 0; k < N; k++) {
            Point2d start = new Point2d(Math.random(), Math.random());
            Point2d dir = new Point2d(Math.random(), Math.random());

            TrajectoryExtender te = new TrajectoryExtender(null, 0, 0);
            ArrayList<Point2d> additions = te.generateAdditions(start, dir, 8);

            int n = 1;
            for (Point2d addition : additions) {
                assertEquals(start.copy().x() + n * dir.x(), addition.x(), eps);
                assertEquals(start.copy().y() + n * dir.y(), addition.y(), eps);
                n++;
            }
        }
    }

    @Test
    public void testGetSelectablePointCount() {
        int N = 10;
        ArrayList<Point2d> points = new ArrayList<>();
        for (int n = 1; n <= N; n++) {
            points.add(Point2d.one());
        }

        TrajectoryExtender te = new TrajectoryExtender(points, 0, 0);
        for (int k = 1; k <= N; k++) {
            assertEquals(k, te.getSelectablePointCount(k));
        }

        for (int k = N + 1; k < 2 * N; k++) {
            assertEquals(N, te.getSelectablePointCount(k));
        }
    }

    @Test
    public void testAvgDirection() {
        int N = 10;
        double eps = 1e-9;

        int K = 10;
        for (int k = 0; k < K; k++) {
            LinkedList<Point2d> points = new LinkedList<>();

            double[] xComponents = new double[N];
            double[] yComponents = new double[N];
            for (int n = 0; n < N; n++) {
                xComponents[n] = Math.random();
                yComponents[n] = Math.random();
                points.add(new Point2d(xComponents[n], yComponents[n]));
            }

            double avgX = 0;
            double avgY = 0;
            for (int n = 0; n < N - 1; n++) {
                avgX += xComponents[n + 1] - xComponents[n];
                avgY += yComponents[n + 1] - yComponents[n];
            }
            avgX /= N - 1;
            avgY /= N - 1;

            TrajectoryExtender te = new TrajectoryExtender(points, 0, 0);
            Point2d avgDir = te.avgDirection(points);

            assertEquals(avgX, avgDir.x(), eps);
            assertEquals(avgY, avgDir.y(), eps);
        }
    }

    @Test
    public void testSelectFirstNPoints() {
        int N = 10;
        ArrayList<Point2d> points = new ArrayList<>();
        for (int n = 0; n < N; n++) {
            points.add(new Point2d(Math.random(), Math.random()));
        }

        TrajectoryExtender te = new TrajectoryExtender(points, 0, 0);

        for (int n = 0; n <= 10; n++) {
            LinkedList<Point2d> selection = te.selectFirstNPoints(n);

            LinkedList<Point2d> gtSelection = new LinkedList<>();
            for (int k = 0; k < n; k++) {
                gtSelection.add(points.get(k));
            }

            for (int k = 0; k < selection.size(); k++) {
                assertEquals(gtSelection.get(k).x(), selection.get(k).x(), 0);
                assertEquals(gtSelection.get(k).y(), selection.get(k).y(), 0);
            }
        }
    }

    @Test
    public void testSelectLastNPoints() {
        int N = 10;
        ArrayList<Point2d> points = new ArrayList<>();
        for (int n = 0; n < N; n++) {
            points.add(new Point2d(Math.random(), Math.random()));
        }

        TrajectoryExtender te = new TrajectoryExtender(points, 0, 0);

        for (int n = 0; n <= 10; n++) {
            LinkedList<Point2d> selection = te.selectLastNPoints(n);

            LinkedList<Point2d> gtSelection = new LinkedList<>();
            for (int k = 0; k < n; k++) {
                gtSelection.add(points.get((N - 1) - k));
            }
            Collections.reverse(gtSelection);

            for (int k = 0; k < selection.size(); k++) {
                assertEquals(gtSelection.get(k).x(), selection.get(k).x(), 0);
                assertEquals(gtSelection.get(k).y(), selection.get(k).y(), 0);
            }
        }
    }
}

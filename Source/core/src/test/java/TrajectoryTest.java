import datastructures.*;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.MetaDataManager;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrajectoryTest {

    @Before
    public void initObjects() {
        TrajectoryManager.release();
        ArgParser.release();
        MetaDataManager.release();
        ArrayList<String> metaData = new ArrayList<String>();
        metaData.add("100");
        metaData.add("100");
        metaData.add("1");
        MetaDataManager.getInstance(metaData);
        MetaDataManager.getInstance().setFrameCount(100);
    }

    @Test
    public void testCreationWorks() {
        for (int k = 0; k < 10; k++) {
            int startFrame = ((int)Math.random()*10);
            Trajectory tra = new Trajectory(startFrame);
            assertEquals(tra.getLabel(), k+1);
            assertEquals(tra.getStartFrame(), startFrame);
        }
    }

    /**
     * Helper method to set manually the trajectory manager trajectories.
     *
     * @param trajectories trajectories to be assigned to the TrajectoryManager singleton.
     */
    private void setTrajectoryManagerTrajectories(HashMap<Integer, Trajectory> trajectories ) {
        Field field = null;
        try {
            field = TrajectoryManager.class.getDeclaredField("trajectories");
            field.setAccessible(true);
            try {
                field.set(TrajectoryManager.getInstance(), trajectories);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTrajectoriesAreWellEnumerated() {
        int N = 10;
        for (int n = 1; n <= N; n++) {
            TrajectoryManager.getInstance().startNewTrajectoryAt(Point2d.one(), 0);
            assertEquals(n, ((Trajectory)TrajectoryManager.getTrajectories().toArray()[n-1]).getLabel());
        }
    }

    @Test
    public void testFirstLabelValueIsEqualsOne() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(Point2d.one(), 0);
        assertEquals(1, ((Trajectory)TrajectoryManager.getTrajectories().toArray()[0]).getLabel());
    }

    @Test
    public void testReleaseResetsTrajectoryLabelEnumeration() {
        TrajectoryManager.getInstance().startNewTrajectoryAt(Point2d.one(), 0);
        assertEquals(1, ((Trajectory)TrajectoryManager.getTrajectories().toArray()[0]).getLabel());
        TrajectoryManager.release();
        TrajectoryManager.getInstance().startNewTrajectoryAt(Point2d.one(), 0);
        assertEquals(1, ((Trajectory)TrajectoryManager.getTrajectories().toArray()[0]).getLabel());
    }

    @Test
    public void testClosable() {
        Trajectory tra = new Trajectory(0);
        assertEquals(false, tra.isClosed());
        tra.markClosed();
        assertEquals(true, tra.isClosed());
    }

    @Test
    public void testDeletable() {
        Trajectory tra = new Trajectory(0);
        assertEquals(false, tra.isDeletable());
        tra.markAsDeletable();
        assertEquals(true, tra.isDeletable());
    }

    @Test
    public void testTrajectoryFrameBoundaries() {
        int startFrame = (int)(Math.random()*10);
        int tilIdx = (int)(Math.random()*10);

        Trajectory tra = new Trajectory(startFrame);

        for (int k = 0; k < tilIdx; k++) {
            tra.addPoint(new Point2d(0, 6));
        }
        tra.markClosed();
        int endFrameIdx = tra.length() + startFrame;

        assertEquals(startFrame, tra.getStartFrame());
        assertEquals(endFrameIdx, tra.getEndFrame());
    }

    @Test
    public void testHasSimilarityValues() {
        Trajectory tra = new Trajectory(0);
        assertEquals(false, tra.hasSimilarityValues());
        tra.assignSimilarityValueTo(tra.getLabel(), 1);
        assertEquals(true, tra.hasSimilarityValues());
    }

    @Test
    public void testNearestAvgSpatialNeighborsTopMode() {
        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);
        Trajectory tra = new Trajectory(0);

        Trajectory other1 = new Trajectory(0);
        Trajectory other2 = new Trajectory(0);
        Trajectory other3 = new Trajectory(0);
        Trajectory other4 = new Trajectory(0);
        Trajectory other5 = new Trajectory(0);

        tra.appendAvgSpatialDist(other1.getLabel(), 2);
        tra.appendAvgSpatialDist(other2.getLabel(), 1);
        tra.appendAvgSpatialDist(other3.getLabel(), 1.5);
        tra.appendAvgSpatialDist(other4.getLabel(), 0.5);
        tra.appendAvgSpatialDist(other5.getLabel(), 1.6);

        LinkedList<Trajectory> topNeighbors = new LinkedList<>();
        topNeighbors.add(other4);
        topNeighbors.add(other2);
        topNeighbors.add(other3);
        topNeighbors.add(other5);
        topNeighbors.add(other1);

        for (int k = 0; k <= topNeighbors.size(); k++) {
            List<Integer> nn = tra.nearestAvgSpatialNeighbors(k);
            assertEquals(k, nn.size());
            for (int idx = 0; idx < k; idx++) {
                assertEquals(topNeighbors.get(idx).getLabel(), nn.get(idx).intValue());
            }
        }
    }

    @Test
    public void testNearestAvgSpatialNeighborsBothMode() {
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "1", "-nnm", "both", "-nn", "5"};
        ArgParser.getInstance(args);
        Trajectory tra = new Trajectory(0);

        Trajectory other1 = new Trajectory(0);
        Trajectory other2 = new Trajectory(0);
        Trajectory other3 = new Trajectory(0);
        Trajectory other4 = new Trajectory(0);
        Trajectory other5 = new Trajectory(0);


        HashMap<Integer, Trajectory> trajectories = new HashMap<>();
        trajectories.put(tra.getLabel(), tra);
        trajectories.put(other1.getLabel(), other1);
        trajectories.put(other2.getLabel(), other2);
        trajectories.put(other3.getLabel(), other3);
        trajectories.put(other4.getLabel(), other4);
        trajectories.put(other5.getLabel(), other5);

        // Manually assign the trajectories in the TrajectoryManager
        setTrajectoryManagerTrajectories(trajectories);

        tra.appendAvgSpatialDist(other1.getLabel(), 2);
        tra.appendAvgSpatialDist(other2.getLabel(), 1);
        tra.appendAvgSpatialDist(other3.getLabel(), 1.5);
        tra.appendAvgSpatialDist(other4.getLabel(), 0.5);
        tra.appendAvgSpatialDist(other5.getLabel(), 1.6);

        // top n/2 neighbors, top best first
        List<Integer> nn = tra.nearestAvgSpatialNeighbors(4);
        assertEquals(other4.getLabel(), nn.get(0).intValue());
        assertEquals(other2.getLabel(), nn.get(1).intValue());

        // worst n/2 neighbors, top worst first
        assertEquals(other1.getLabel(), nn.get(2).intValue());
        assertEquals(other5.getLabel(), nn.get(3).intValue());
    }

    @Test
    public void testAllNeighborsBothMode() {
        String[] args = {"-d", "foobar", "-task", "1", "-nnm", "all"};
        ArgParser.getInstance(args);
        Trajectory tra = new Trajectory(0);

        Trajectory other1 = new Trajectory(0);
        Trajectory other2 = new Trajectory(0);
        Trajectory other3 = new Trajectory(0);
        Trajectory other4 = new Trajectory(0);
        Trajectory other5 = new Trajectory(0);


        tra.appendAvgSpatialDist(other1.getLabel(), 2);
        tra.appendAvgSpatialDist(other2.getLabel(), 1);
        tra.appendAvgSpatialDist(other3.getLabel(), 1.5);
        tra.appendAvgSpatialDist(other4.getLabel(), 0.5);
        tra.appendAvgSpatialDist(other5.getLabel(), 1.6);

        assertEquals(5, tra.allNearestNeighbors().size());
        assertTrue(tra.allNearestNeighbors().contains(other1.getLabel()));
        assertTrue(tra.allNearestNeighbors().contains(other2.getLabel()));
        assertTrue(tra.allNearestNeighbors().contains(other3.getLabel()));
        assertTrue(tra.allNearestNeighbors().contains(other4.getLabel()));
        assertTrue(tra.allNearestNeighbors().contains(other5.getLabel()));
    }

    @Test
    public void testAssignSimilarityValueTo() {
        Trajectory tra = new Trajectory(0);
        Trajectory o1 = new Trajectory(0);
        Trajectory o2 = new Trajectory(0);
        Trajectory o3 = new Trajectory(0);
        Trajectory o4 = new Trajectory(0);
        Trajectory o5 = new Trajectory(0);

        assertFalse(tra.hasSimilarityValues());
        assertFalse(o1.hasSimilarityValues());
        assertFalse(o2.hasSimilarityValues());
        assertFalse(o3.hasSimilarityValues());
        assertFalse(o4.hasSimilarityValues());
        assertFalse(o5.hasSimilarityValues());

        tra.assignSimilarityValueTo(o3.getLabel(), 3.3);
        tra.assignSimilarityValueTo(o5.getLabel(), 5.5);
        tra.assignSimilarityValueTo(o1.getLabel(), 1.1);
        tra.assignSimilarityValueTo(o2.getLabel(), 2.2);
        tra.assignSimilarityValueTo(o4.getLabel(), 4.4);

        String gtSimString = "[1.1, 2.2, 3.3, 4.4, 5.5]";

        assertTrue(tra.hasSimilarityValues());
        assertEquals(gtSimString, tra.toSimilarityString());
        assertFalse(o1.hasSimilarityValues());
        assertFalse(o2.hasSimilarityValues());
        assertFalse(o3.hasSimilarityValues());
        assertFalse(o4.hasSimilarityValues());
        assertFalse(o5.hasSimilarityValues());
    }

    @Test
    public void testCompareTo() {
        Trajectory first = new Trajectory(0);
        Trajectory second = new Trajectory(0);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertTrue(first.compareTo(first) == 0);
        assertTrue(second.compareTo(second) == 0);
    }

    @Test(expected=NullPointerException.class)
    public void testGetEuclidPositionAtFrameWhenNoEuclidPointsAssigned() {
        Trajectory tra = new Trajectory(0);
        Point2d p = new Point2d(1,1);
        tra.addPoint(p);
        tra.getEuclidPositionAtFrame(0);
    }

    @Test
    public void testTransformTrackedPoints() {
        CalibrationManager.release();
        DepthManager.release();

        // Assign random focal lengths for the depth camera
        double f_x = 200d*Math.random();
        double f_y = 200d*Math.random();

        // Assign a random principal point for the depth camera
        double p_x = 150d*Math.random();
        double p_y = 150d*Math.random();

        ArrayList<LabeledFileLine> lfl = new ArrayList<>();

        // Set Calibration data
        lfl.add(new LabeledFileLine("f_d", f_x + " " + f_y));
        lfl.add(new LabeledFileLine("p_d", p_x + " " + p_y));
        LabeledFile lf = new LabeledFile(lfl);
        CalibrationManager.getInstance(lf);

        // All trajectories have length K and there are K depth fields
        int K = 3;

        // Each depth field is of resolution [X x Y].
        int X = 2; int Y = 2;

        // Save depth fields
        LinkedList<DepthField> dfs = new LinkedList<>();
        for (int k = 0; k < K; k++) {
            dfs.add(new DepthField(X, Y));
        }

        // Init important storage datastructures.
        LinkedList<Double> xs = new LinkedList<>();
        LinkedList<Double> ys = new LinkedList<>();
        LinkedList<Double> ds = new LinkedList<>();
        LinkedList<Trajectory> tras = new LinkedList<>();

        for (int k = 0; k < X*Y; k++) {
            tras.add(new Trajectory(0));
        }

        for (double x = 0.0; x < X; x++) {
            for (double y = 0.0; y < Y; y++) {
                xs.add(x);
                ys.add(y);
                double depth = 100 * Math.random();
                ds.add(depth);
            }
        }

        // assign depth fields and trajectories
        // Iterate over grid resolution
        for (int k = 0; k < K; k++) {
            int idx = 0;
            for (double x = 0.0; x < X; x++) {
                for (double y = 0.0; y < Y; y++) {
                    Trajectory tra = tras.get(idx);

                    // Each position in the depth map has a random depth value assigned
                    double depth = ds.get(idx);

                    // Iterate over all trajectories
                    Point2d p = new Point2d(x, y);
                    tra.addPoint(p);
                    dfs.get(k).setAt((int) x, (int) y, depth);
                    idx++;
                }
            }
        }

        // Save depth maps in managers
        for (DepthField df : dfs) {
            DepthManager.getInstance().add(df);
        }

        // Transform trajectory points to euclidian space
        for (Trajectory tra : tras) {
            tra.transformTrackedPoints();
        }

        // Test if transformed points are computed correctly,
        // by making use of depth cues and camera calibration data.
        for (int idx = 0; idx < xs.size(); idx++) {
            double x = xs.get(idx);
            double y = ys.get(idx);
            double depth = ds.get(idx);
            for (int k = 0; k < K; k++) {
                Point3d p3 = tras.get(idx).getEuclidPositionAtFrame(k);
                assertEquals(depth*((y-p_x)/f_x), p3.y(), 0);
                assertEquals(depth*((x-p_y)/f_y), p3.x(), 0);
                assertEquals(depth, p3.z(), 0);
            }
        }

    }

    @Test
    public void testCurrentActiveFrame() {
        // random starting frame within range [0,19]
        int K = (int)(Math.random()*20);
        int steps = 10;

        // Assign a random start frame idx
        Trajectory tra = new Trajectory(K);

        // iterate steps times and check the active trajectory
        for (int k = K; k < steps+K; k++) {
            tra.addPoint(new Point2d(0,0));
            assertEquals(k, tra.currentActiveFrame());
        }
    }

    @Test
    public void testGetPointAtFrame() {
        int startFrameIdx = (int)(Math.random()*20);
        Trajectory tra = new Trajectory(startFrameIdx);

        int pointsCount = 10;

        for (int k = 0; k < pointsCount; k++) {
            tra.addPoint(new Point2d(k,k+1));
        }

        for (int k = 0; k < pointsCount; k++) {
            Point2d currentPoint = tra.getPointAtFrame(startFrameIdx+k);
            assertEquals(currentPoint.x(), k, 0);
            assertEquals(currentPoint.y(), k+1, 0);
        }
    }

    @Test
    public void testLivesInFrame() {
        int startFrameIdx = (int)(Math.random()*20);
        Trajectory tra = new Trajectory(startFrameIdx);

        int pointsCount = 10;
        int steps = 10;

        for (int k = 0; k < pointsCount; k++) {
            tra.addPoint(new Point2d(k,k+1));
        }

        for (int frameIdx = startFrameIdx; frameIdx < pointsCount + startFrameIdx; frameIdx++) {
            assertTrue(tra.livesInFrame(frameIdx));
        }

        for (int frameIdx = startFrameIdx-steps; frameIdx < startFrameIdx; frameIdx++) {
            assertFalse(tra.livesInFrame(frameIdx));
        }

        for (int frameIdx = pointsCount + startFrameIdx; frameIdx < pointsCount + startFrameIdx + steps; frameIdx++) {
            assertFalse(tra.livesInFrame(frameIdx));
        }
    }

    @Test
    public void testToFramewiseString() {
        Trajectory tra = new Trajectory(0);
        double x1 = 1; double y1 = 2;
        Point2d p1 = new Point2d(x1, y1);

        double x2 = 3; double y2 = 4;
        Point2d p2 = new Point2d(x2, y2);

        double x3 = 5; double y3 = 6;
        Point2d p3 = new Point2d(x3, y3);

        tra.addPoint(p1);
        tra.addPoint(p2);
        tra.addPoint(p3);

        assertEquals(tra.getLabel() + " " + x1 + " " + y1, tra.toFramewiseString(0));
        assertEquals(tra.getLabel() + " " + x2 + " " + y2, tra.toFramewiseString(1));
        assertEquals(tra.getLabel() + " " + x3 + " " + y3, tra.toFramewiseString(2));
    }

    @Test
    public void testMarkInvalidPoints() {
        CalibrationManager.release();
        DepthManager.release();
        TrajectoryManager.release();

        // Assign random focal lengths for the depth camera
        double f_x = 200d*Math.random();
        double f_y = 200d*Math.random();

        // Assign a random principal point for the depth camera
        double p_x = 150d*Math.random();
        double p_y = 150d*Math.random();

        ArrayList<LabeledFileLine> lfl = new ArrayList<>();

        // Set Calibration data
        lfl.add(new LabeledFileLine("f_d", f_x + " " + f_y));
        lfl.add(new LabeledFileLine("p_d", p_x + " " + p_y));
        LabeledFile lf = new LabeledFile(lfl);
        CalibrationManager.getInstance(lf);


        DepthField df1 = new DepthField(3, 3);
        df1.setAt(0, 0, 1); df1.setAt(0, 1, 1); df1.setAt(0, 2, 1);
        df1.setAt(1, 0, 1); df1.setAt(1, 1, 1); df1.setAt(1, 2, 1);
        df1.setAt(2, 0, 1); df1.setAt(2, 1, 1); df1.setAt(2, 2, 0);

        DepthField df2 = new DepthField(3, 3);
        df2.setAt(0, 0, 1); df2.setAt(0, 1, 1); df2.setAt(0, 2, 1);
        df2.setAt(1, 0, 1); df2.setAt(1, 1, 1); df2.setAt(1, 2, 1);
        df2.setAt(2, 0, 1); df2.setAt(2, 1, 1); df2.setAt(2, 2, 0);

        DepthManager.getInstance().add(df1);
        DepthManager.getInstance().add(df2);

        Trajectory tra1 = new Trajectory(0);
        tra1.addPoint(new Point2d(0,0));
        tra1.addPoint(new Point2d(0,0));

        Trajectory tra2 = new Trajectory(0);
        tra2.addPoint(new Point2d(1,1));
        tra2.addPoint(new Point2d(1,1));

        Trajectory tra3 = new Trajectory(0);
        tra3.addPoint(new Point2d(0,0));
        tra3.addPoint(new Point2d(1,1));

        Trajectory tra4 = new Trajectory(1);
        tra4.addPoint(new Point2d(0,0));

        // Trajectory having a invalid interpolation point (p=(2,2))
        Trajectory tra5 = new Trajectory(1);
        tra5.addPoint(new Point2d(1,1));

        assertEquals(false, tra1.markInvalidPoints());
        assertEquals(true, tra2.markInvalidPoints());
        assertEquals(false, tra3.markInvalidPoints());
        assertEquals(false, tra4.markInvalidPoints());
        assertEquals(true, tra5.markInvalidPoints());
    }

    @Test
    public void testGetOutputString() {
        Trajectory tra = new Trajectory(0);

        Point2d p1 = new Point2d(4, 1);
        Point2d p2 = new Point2d(5, 2);
        Point2d p3 = new Point2d(6, 3);
        tra.addPoint(p1);
        tra.addPoint(p2);
        tra.addPoint(p3);

        tra.markClosed();

        String header = "### L:" + tra.getLabel() + " S:" + (tra.getStartFrame()+1) + " C:" + tra.length();
        String content = "";
        for (Point2d p : tra) {
            content = content + p.toOutputString() + "\n";
        }

        String gtString = header + "\n" + content;
        String fetchedString = tra.getOutputString();
        assertTrue(gtString.equals(fetchedString));
    }

    @Test
    public void testGetOutputStringRequiresTrajectoriesBeingMarkedAsClosed() {
        Trajectory tra = new Trajectory(0);

        Point2d p1 = new Point2d(4, 1);
        Point2d p2 = new Point2d(5, 2);
        Point2d p3 = new Point2d(6, 3);
        tra.addPoint(p1);
        tra.addPoint(p2);
        tra.addPoint(p3);

        String header = "### L:" + tra.getLabel() + " S:" + (tra.getStartFrame()+1) + " C:" + tra.length();
        String content = "";
        for (Point2d p : tra) {
            content = content + p.toOutputString() + "\n";
        }
        String gtString = header + "\n" + content;

        String fetchedString = tra.getOutputString();
        assertEquals(null, fetchedString);

        tra.markClosed();
        fetchedString = tra.getOutputString();
        assertTrue(gtString.equals(fetchedString));
    }

    @Test
    public void testFilterSimilarityOfTrajectory() {
        Trajectory tra = new Trajectory(0);
        Trajectory o1 = new Trajectory(0);
        Trajectory o2 = new Trajectory(0);
        Trajectory o3 = new Trajectory(0);

        tra.assignSimilarityValueTo(o1.getLabel(), 1);
        tra.assignSimilarityValueTo(o2.getLabel(), 2);
        tra.assignSimilarityValueTo(o3.getLabel(), 3);

        String gtSimString = "[1.0, 2.0, 3.0]";
        assertEquals(gtSimString, tra.toSimilarityString());
        tra.filterSimilarityOfTrajectory(o2.getLabel());
        gtSimString = "[1.0, 3.0]";
        assertEquals(gtSimString, tra.toSimilarityString());
    }

    @Test
    public void testFilterInvalidSpatialNeighbors() {
        String[] args = {"-d", "foobar", "-task", "1", "-nnm", "top"};
        ArgParser.getInstance(args);

        Trajectory tra = new Trajectory(0);
        Trajectory o1 = new Trajectory(0);
        Trajectory o2 = new Trajectory(0);
        Trajectory o3 = new Trajectory(0);

        tra.assignSimilarityValueTo(o1.getLabel(), 1);
        tra.assignSimilarityValueTo(o2.getLabel(), 2);

        tra.appendAvgSpatialDist(o1.getLabel(), 2);
        tra.appendAvgSpatialDist(o2.getLabel(), 2);
        tra.appendAvgSpatialDist(o3.getLabel(), 2);

        LinkedList<Trajectory> tras = new LinkedList<>();
        tras.add(o1);
        tras.add(o2);
        tras.add(o3);

        assertEquals(3, tra.nearestAvgSpatialNeighbors(3).size());
        int count = 0;
        for (int idx : tra.nearestAvgSpatialNeighbors(3)) {
            assertEquals(tras.get(count).getLabel(), idx);
            count++;
        }
        int invCount = tra.filterInvalidSpatialNeighbors();
        assertEquals(1, invCount);

        count = 0;
        for (int idx : tra.nearestAvgSpatialNeighbors(3)) {
            assertEquals(tras.get(count).getLabel(), idx);
            count++;
        }
        assertEquals(2, tra.nearestAvgSpatialNeighbors(3).size());
    }

    @Test
    public void testInitSimilarityDatastructures() {
        Trajectory tra = new Trajectory(0);
        tra.initSimilarityDatastructures();
    }

    @Test
    public void testToString() {
        Trajectory tra = new Trajectory(0);
        Point2d p = new Point2d(1,2);
        tra.addPoint(p);

        String header = "l="+tra.getLabel()+" s="+tra.getStartFrame();
        String content = "";
        content += p.toString() + " ";
        String gtString =  header + " " + content;

        assertEquals(gtString, tra.toString());
    }



    @Test
    public void testExtendPointTrackingAppendLeftAndRightFull() {
        int startFrame = 3;
        Trajectory tra = new Trajectory(startFrame);

        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = Math.random() + 1;
        double ySeed = Math.random() + 1;
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

        Point2d avg = new Point2d(step, step);

        tra.extendPointTracking();
        System.out.println();

        Point2d lastP = points.getLast();
        double esp = 1.e-9;
        for (int n = 0; n < 3; n++) {
            Point2d gtP = lastP.copy().add(avg);
            int idx = points.size() + n + 3;
            Point2d fetchedP = tra.getPointAtFrame(idx);
            assertEquals(gtP.x(), fetchedP.x(), esp);
            assertEquals(gtP.y(), fetchedP.y(), esp);
            lastP = gtP;
        }

        for (int n = 1; n <= 3; n++) {
            assertEquals(points.get(0).x() - n * 0.1, tra.getPointAtFrame(3-n).x(), esp);
            assertEquals(points.get(0).y() - n * 0.1, tra.getPointAtFrame(3-n).y(), esp);
        }
    }

    @Test
    public void testExtendPointTrackingAppendOnlyRight() {
        int startFrame = 3;

        Trajectory tra = new Trajectory(startFrame);
        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = 0.91;
        double ySeed = 0.86;
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

        tra.extendPointTracking();

        int N = 3;
        double eps = 1.e-9;
        for (int k = 0; k < N; k++) {
            assertEquals(tra.getPointAtFrame(k).x(), points.get(0).x() - (N-k)*step, eps);
            assertEquals(tra.getPointAtFrame(k).y(), points.get(0).y() - (N-k)*step, eps);

            assertEquals(tra.getPointAtFrame(k+points.size() + 3).x(), points.get(points.size() - 1).x() + (k+1)*step, eps);
            assertEquals(tra.getPointAtFrame(k+points.size() + 3).y(), points.get(points.size() - 1).y() + (k+1)*step, eps);
        }
    }

    @Test
    public void testExtendPointTrackingDoesNotChangeOriginalTrackedPoints() {
        int startFrame = 3;

        Trajectory tra = new Trajectory(startFrame);
        LinkedList<Point2d> points = new LinkedList<>();
        double xSeed = 0.91;
        double ySeed = 0.86;
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

        // Make sure that we have exact value copies of original point values.
        LinkedList<Point2d> copiedPoints = new LinkedList<>();
        for (Point2d p : points) {
            copiedPoints.add(p.copy());
        }

        tra.extendPointTracking();

        // ensure that original tracking point values were not modified
        // by the point extension
        for (int n = 0; n < copiedPoints.size(); n++) {
            assertEquals(copiedPoints.get(n).x(), tra.getPointAtFrame(startFrame + n).x(), 0);
            assertEquals(copiedPoints.get(n).y(), tra.getPointAtFrame(startFrame + n).y(), 0);
        }
    }

    @Test
    public void testSelectInRangeAllValidReturnsSameList() {
        ArrayList<Point2d> additions = new ArrayList<>();

        int N = 8;
        for (int n = 0; n < N; n++) {
            double x = Math.random() * 9;
            double y = Math.random() * 9;
            additions.add(new Point2d(x, y));
        }

        Trajectory tra = new Trajectory(0);

        int idx = 0;
        for (Point2d p : tra.selectInRange(additions)) {
            assertEquals(additions.get(idx).x(), p.x(), 0);
            assertEquals(additions.get(idx).y(), p.y(), 0);
            idx++;
        }
    }

    @Test
    public void testSelectInRangeFromINvalidOnTheyAreFiltered() {
        ArrayList<Point2d> additions = new ArrayList<>();

        int N = 8;
        for (int n = 0; n < N; n++) {
            double x = Math.random() * 9;
            double y = Math.random() * 9;
            additions.add(new Point2d(x, y));
        }
        additions.add(new Point2d(-1, 5));
        for (int n = 0; n < N; n++) {
            double x = Math.random() * 9;
            double y = Math.random() * 9;
            additions.add(new Point2d(x, y));
        }
        assertEquals(17, additions.size());
        Trajectory tra = new Trajectory(0);

        int idx = 0;
        ArrayList<Point2d> pointsInRange = tra.selectInRange(additions);

        for (int k = 0; k < N; k++) {
            assertEquals(additions.get(k).x(), pointsInRange.get(k).x(), 0);
            assertEquals(additions.get(k).y(), pointsInRange.get(k).y(), 0);
        }

        for (int k = N; k < 2*N + 1; k++) {
            assertFalse(pointsInRange.contains(additions.get(k)));
        }

        for (Point2d p : tra.selectInRange(additions)) {
            assertEquals(additions.get(idx).x(), p.x(), 0);
            assertEquals(additions.get(idx).y(), p.y(), 0);
            idx++;
        }
    }

    @Test
    public void testStartFrameWithoutLeftAdditionsNoPointTrackingExtend() {
        int N = 10;
        int start = (int)(Math.random()*10) + 3;
        Trajectory tra = new Trajectory(start);
        for (int n = 0; n < N; n++) {
            tra.addPoint(Point2d.one());
        }
        tra.markClosed();
        assertEquals(start, tra.startFrameWithoutLeftAdditions());
        assertEquals(start, tra.getStartFrame());
    }

    @Test
    public void testEndFrameWithoutLeftAdditionsNoPointTrackingExtend() {
        int N = 10;
        int start = (int)(Math.random()*10) + 3;
        Trajectory tra = new Trajectory(start);
        for (int n = 0; n < N; n++) {
            tra.addPoint(Point2d.one());
        }
        tra.markClosed();
        assertEquals(start + N - 1, tra.endFrameWithoutRightAdditions());
        assertEquals(start + N - 1, tra.getEndFrame());
    }

    @Test
    public void testStartFrameWithoutLeftAdditionsWithPointTrackingExtend() {
        int N = 10;
        int start = (int)(Math.random()*10) + 3;
        Trajectory tra = new Trajectory(start);
        Point2d startP = new Point2d(Math.random() + 10, Math.random() + 10);
        double scale = 0.1d;
        for (int n = 0; n < N; n++) {
            tra.addPoint(startP.copy().add(new Point2d(scale * n, scale * n)));
        }
        tra.markClosed();
        tra.extendPointTracking();

        int startWithAdditions = (start - 3 >= 0) ? (start - 3) : 0;

        tra.startFrameWithoutLeftAdditions();
        assertEquals(start, tra.startFrameWithoutLeftAdditions());
        assertEquals(startWithAdditions, tra.getStartFrame());
    }

    @Test
    public void testEndFrameWithoutLeftAdditionsWithPointTrackingExtend() {
        int N = 10;
        MetaDataManager.getInstance().setFrameCount(100);
        int start = (int)(Math.random() * 10) + 3;
        Trajectory tra = new Trajectory(start);
        Point2d startP = new Point2d(Math.random(), Math.random());
        double scale = 0.1d;
        for (int n = 0; n < N; n++) {
            tra.addPoint(startP.copy().add(new Point2d(scale * n, scale * n)));
        }
        tra.markClosed();
        tra.extendPointTracking();

        int endWithAdditions = (start + N - 1) + 3;

        assertEquals(start + N - 1, tra.endFrameWithoutRightAdditions());
        assertEquals(endWithAdditions, tra.getEndFrame());
    }

    @Test
    public void testGetOverlappingStartFrameIndexBetweenWithoutPointTrackingExtend() {
        int N = 10;
        int startA = (int)(Math.random() * 10) + 3;
        Trajectory a = new Trajectory(startA);
        Point2d startPA = new Point2d(Math.random() + 10, Math.random() + 10);
        double scale = 0.1d;
        for (int n = 0; n < N; n++) {
            a.addPoint(startPA.copy().add(new Point2d(scale * n, scale * n)));
        }
        a.markClosed();

        int startB = (int)(Math.random() * 10) + 3;
        Trajectory b = new Trajectory(startB);
        Point2d startPB = new Point2d(Math.random() + 10, Math.random() + 10);
        for (int n = 0; n < N; n++) {
            b.addPoint(startPB.copy().add(new Point2d(scale * n, scale * n)));
        }
        b.markClosed();

        int gtStartIdx = Math.max(startA, startB);
        assertEquals(gtStartIdx, a.getOverlappingStartFrameIndexBetween(b));
    }

    @Test
    public void testGetOverlappingEndFrameIndexBetweenWithoutPointTrackingExtend() {
        int N = 10;
        int startA = (int)(Math.random() * 10) + 3;
        Trajectory a = new Trajectory(startA);
        Point2d startPA = new Point2d(Math.random() + 10, Math.random() + 10);
        double scale = 0.1d;
        for (int n = 0; n < N; n++) {
            a.addPoint(startPA.copy().add(new Point2d(scale * n, scale * n)));
        }
        a.markClosed();

        int startB = (int)(Math.random() * 10) + 3;
        Trajectory b = new Trajectory(startB);
        Point2d startPB = new Point2d(Math.random() + 10, Math.random() + 10);
        for (int n = 0; n < N; n++) {
            b.addPoint(startPB.copy().add(new Point2d(scale * n, scale * n)));
        }
        b.markClosed();

        int gtEndIdx = Math.min(startA, startB) + N - 1;
        assertEquals(gtEndIdx, a.getOverlappingEndFrameIndexBetween(b));
    }

    @Test
    public void testGetOverlappingStartFrameIndexBetweenWithPointTrackingExtendNotOverlapping() {
        int N = 10;
        int startA = (int)(Math.random() * 10) + 3;
        Trajectory a = new Trajectory(startA);
        Point2d startPA = new Point2d(Math.random() + 10, Math.random() + 10);
        double scale = 0.1d;
        for (int n = 0; n < N; n++) {
            a.addPoint(startPA.copy().add(new Point2d(scale * n, scale * n)));
        }
        a.markClosed();

        int startB = (int)(Math.random() * 10) + 3 + startA + N;
        Trajectory b = new Trajectory(startB);
        Point2d startPB = new Point2d(Math.random() + 10, Math.random() + 10);
        for (int n = 0; n < N; n++) {
            b.addPoint(startPB.copy().add(new Point2d(scale * n, scale * n)));
        }
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        int gtStartA = (startA - 3 >= 0) ? (startA - 3) : 0;
        int gtStartB = (startB - 3 >= 0) ? (startB - 3) : 0;
        int gtStartIdx = Math.max(gtStartA, gtStartB);

        a.getOverlappingStartFrameIndexBetween(b);
        assertEquals(gtStartIdx, a.getOverlappingStartFrameIndexBetween(b));
    }

    @Test
    public void testGetOverlappingStartFrameIndexBetweenWithPointTrackingExtendOverlapping() {
        int N = 10;
        int startA = (int)(Math.random() * 10) + 3;
        Trajectory a = new Trajectory(startA);
        Point2d startPA = new Point2d(Math.random() + 10, Math.random() + 10);
        double scale = 0.1d;
        for (int n = 0; n < N; n++) {
            a.addPoint(startPA.copy().add(new Point2d(scale * n, scale * n)));
        }
        a.markClosed();

        int startB = (int)(Math.random() * 10) + 3;
        Trajectory b = new Trajectory(startB);
        Point2d startPB = new Point2d(Math.random() + 10, Math.random() + 10);
        for (int n = 0; n < N; n++) {
            b.addPoint(startPB.copy().add(new Point2d(scale * n, scale * n)));
        }
        b.markClosed();

        a.extendPointTracking();
        b.extendPointTracking();

        int gtStartA = (startA - 3 >= 0) ? startA : 0;
        int gtStartB = (startB - 3 >= 0) ? startB : 0;
        int gtStartIdx = Math.max(gtStartA, gtStartB);

        a.getOverlappingStartFrameIndexBetween(b);
        assertEquals(gtStartIdx, a.getOverlappingStartFrameIndexBetween(b));
    }

    @Test
    public void testGetOverlappingEndFrameIndexBetweenWithPointTrackingExtendNotOverlapping() {
        int T = 10;
        for (int t = 0; t < T; t++) {
            int N = 10;
            int startA = (int) (Math.random() * 10) + 3;
            Trajectory a = new Trajectory(startA);
            Point2d startPA = new Point2d(Math.random() + 10, Math.random() + 10);
            double scale = 0.1d;
            for (int n = 0; n < N; n++) {
                a.addPoint(startPA.copy().add(new Point2d(scale * n, scale * n)));
            }
            a.markClosed();

            int startB = (int) (Math.random() * 10) + 3 + startA + N;
            Trajectory b = new Trajectory(startB);
            Point2d startPB = new Point2d(Math.random() + 10, Math.random() + 10);
            for (int n = 0; n < N; n++) {
                b.addPoint(startPB.copy().add(new Point2d(scale * n, scale * n)));
            }
            b.markClosed();

            a.extendPointTracking();
            b.extendPointTracking();

            int endWithAdditionsA = (startA + N - 1) + 3;
            int endWithAdditionsB = (startB + N - 1) + 3;
            int gtEndIdx = Math.min(endWithAdditionsA, endWithAdditionsB);
            a.getOverlappingEndFrameIndexBetween(b);
            assertEquals(gtEndIdx, a.getOverlappingEndFrameIndexBetween(b));
        }
    }

    @Test
    public void testGetOverlappingEndFrameIndexBetweenWithPointTrackingExtendOverlapping() {
        int T = 10;
        for (int t = 0; t < T; t++) {
            int N = 10;
            int startA = (int) (Math.random() * 10) + 3;
            Trajectory a = new Trajectory(startA);
            Point2d startPA = new Point2d(Math.random() + 10, Math.random() + 10);
            double scale = 0.1d;
            for (int n = 0; n < N; n++) {
                a.addPoint(startPA.copy().add(new Point2d(scale * n, scale * n)));
            }
            a.markClosed();

            int startB = (int) (Math.random() * 10) + 3;
            Trajectory b = new Trajectory(startB);
            Point2d startPB = new Point2d(Math.random() + 10, Math.random() + 10);
            for (int n = 0; n < N; n++) {
                b.addPoint(startPB.copy().add(new Point2d(scale * n, scale * n)));
            }
            b.markClosed();

            a.extendPointTracking();
            b.extendPointTracking();

            int endWithAdditionsA = (startA + N - 1);
            int endWithAdditionsB = (startB + N - 1);
            int gtEndIdx = Math.min(endWithAdditionsA, endWithAdditionsB);

            a.getOverlappingEndFrameIndexBetween(b);
            assertEquals(gtEndIdx, a.getOverlappingEndFrameIndexBetween(b));
        }
    }

}

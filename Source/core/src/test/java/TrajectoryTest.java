import datastructures.*;
import managers.CalibrationManager;
import managers.DepthManager;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;

import java.util.ArrayList;
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
        String[] args = {"-d", "foobar", "-task", "1", "-nnm", "both"};
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

        // top n/2 neighbors, top best first
        List<Integer> nn = tra.nearestAvgSpatialNeighbors(4);
        assertEquals(other4.getLabel(), nn.get(0).intValue());
        assertEquals(other2.getLabel(), nn.get(1).intValue());

        // worst n/2 neighbors, top worst first
        assertEquals(other1.getLabel(), nn.get(2).intValue());
        assertEquals(other5.getLabel(), nn.get(3).intValue());
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
                assertEquals(depth*((y-p_x)/f_x), p3.y(), idx);
                assertEquals(depth*((x-p_y)/f_y), p3.x(), idx);
                assertEquals(depth, p3.z(), idx);
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
}

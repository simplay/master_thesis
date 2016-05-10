import datastructures.*;
import managers.CalibrationManager;
import managers.DepthManager;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class Point2dTest {

    @Test
    public void testConstructorSetsValues() {
        double x = Math.random();
        double y = Math.random();
        Point2d p1 = new Point2d(x, y);
        String[] args = {String.valueOf(x), String.valueOf(y)};
        Point2d p2 = new Point2d(args);
        assertEquals(p1.toString(), new Point2d(x,y).toString());
        assertEquals(p2.toString(), new Point2d(x,y).toString());
    }

    @Test
    public void testValuesSetCorrectly() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);

        assertEquals(x, p.x(), 0d);
        assertEquals(x, p.u(), 0d);
        assertEquals(y, p.y(), 0d);
        assertEquals(y, p.v(), 0d);
    }

    @Test
    public void testSwapComponents() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);

        assertEquals(x, p.x(), 0d);
        assertEquals(y, p.y(), 0d);
        p.swapComponents();
        assertEquals(y, p.x(), 0d);
        assertEquals(x, p.y(), 0d);
    }

    @Test
    public void testIsValid() {
        Point2d p = new Point2d(0, 0);
        assertEquals(true, p.isValid());
        p.markAsInvalid();
        assertEquals(false, p.isValid());
    }

    @Test
    public void testCopy() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);
        Point2d p_copy = p.copy();
        assertEquals(p.x(), p_copy.x(), 0);
        assertEquals(p.y(), p_copy.y(), 0);
    }

    @Test
    public void testSub() {
        double x1 = Math.random();
        double y1 = Math.random();
        double x2 = Math.random();
        double y2 = Math.random();

        Point2d p1 = new Point2d(x1, y1);
        Point2d p2 = new Point2d(x2, y2);

        assertEquals(x1, p1.x(), 0);
        assertEquals(y1, p1.y(), 0);
        assertEquals(x2, p2.x(), 0);
        assertEquals(y2, p2.y(), 0);

        p1.sub(p2);

        assertEquals(x1-x2, p1.x(), 0);
        assertEquals(y1-y2, p1.y(), 0);
        assertEquals(x2, p2.x(), 0);
        assertEquals(y2, p2.y(), 0);
    }

    @Test
    public void testDivBy() {
        double x = Math.random();
        double y = Math.random();
        double f = Math.random();

        Point2d p = new Point2d(x, y);
        assertEquals(x, p.x(), 0);
        assertEquals(y, p.y(), 0);

        p.div_by(f);

        assertEquals(x/f, p.x(), 0);
        assertEquals(y/f, p.y(), 0);
    }

    @Test
    public void testCompute3dTrackedPosition() {
        CalibrationManager.release();
        double x = 0.0;
        double y = 0.0;
        Point2d p = new Point2d(x, y);

        double depth = 100*Math.random();

        double f_x = 200d*Math.random();
        double f_y = 200d*Math.random();

        double p_x = 150d*Math.random();
        double p_y = 150d*Math.random();

        DepthField df = new DepthField((int)x+1, (int)y+1);
        df.setAt((int)x, (int)y, depth);

        DepthManager.getInstance().add(df);

        ArrayList<LabeledFileLine> lfl = new ArrayList<>();

        lfl.add(new LabeledFileLine("f_d", f_x + " " + f_y));
        lfl.add(new LabeledFileLine("p_d", p_x + " " + p_y));
        LabeledFile lf = new LabeledFile(lfl);
        CalibrationManager.getInstance(lf);

        p.compute3dTrackedPosition(0);
        Point3d p3 = p.getEuclidPos();

        // Note that we have to swap x and y here since the calibration loader
        // does swap the x,y components of read input since we work with column/row indices instead
        // of cartesian coordinates. Once again, implementing this order manually
        // is only necessary within testing.
        assertEquals(depth*((y-p_x)/f_x), p3.y(), 0);
        assertEquals(depth*((x-p_y)/f_y), p3.x(), 0);
        assertEquals(depth, p3.z(), 0);
    }

    @Test
    public void testTruncatedComponents() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);

        assertEquals(p.iU(), (int)x);
        assertEquals(p.iV(), (int)y);
    }


    @Test
    public void testRounded() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);
        Point2d p_rounded = p.rounded();
        assertEquals(p_rounded.x(), (int)Math.round(p.x()), 0);
        assertEquals(p_rounded.y(), (int)Math.round(p.y()), 0);
    }

    @Test
    public void testLengthFunctions() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);

        double lenSq = x*x + y*y;

        assertEquals(lenSq, p.length_squared(), 0);
        assertEquals(Math.sqrt(lenSq), p.length(), 0);
    }

    @Test
    public void testPrettyStringMethods() {
        double x = Math.random();
        double y = Math.random();
        Point2d p = new Point2d(x, y);

        String serializedPoint = x + " " + y;
        String debugRepresentationPoint = "(x,y)=("+ x +","+y+")";

        assertEquals(serializedPoint, p.toOutputString());
        assertEquals(debugRepresentationPoint, p.toString());
    }

}

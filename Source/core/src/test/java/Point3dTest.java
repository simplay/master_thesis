import datastructures.Mat3x4;
import datastructures.Point2d;
import datastructures.Point3d;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Point3dTest {

    @Test
    public void testTransformBy() {
        double s_x = Math.random(); double s_y = Math.random(); double s_z = Math.random();
        double d_x = Math.random(); double d_y = Math.random(); double d_z = Math.random();

        double[] row1 = {s_x, 0, 0, d_x};
        double[] row2 = {0, s_y, 0, d_y};
        double[] row3 = {0, 0, s_z, d_z};

        Mat3x4 E = new Mat3x4(row1, row2, row3);

        double x = Math.random(); double y = Math.random(); double z = Math.random();;
        Point3d p = new Point3d(x, y, z);

        Point3d transformedP = p.transformBy(E);

        assertEquals(s_x*x + d_x, transformedP.x(), 0);
        assertEquals(s_y*y + d_y, transformedP.y(), 0);
        assertEquals(s_z*z + d_z, transformedP.z(), 0);
    }

    @Test
    public void testPrettyStringMethods() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        Point3d p = new Point3d(x, y, z);

        String debugRepresentationPoint = "(x,y,z)=("+ x + "," + y + ","  + z + ")";
        assertEquals(debugRepresentationPoint, p.toString());
    }

    @Test
    public void testLengthFunctions() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        Point3d p = new Point3d(x, y, z);

        double lenSq = x*x + y*y + z*z;

        assertEquals(lenSq, p.length_squared(), 0);
        assertEquals(Math.sqrt(lenSq), p.length(), 0);
    }

    @Test
    public void testDivBy() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        double f = Math.random();

        Point3d p = new Point3d(x, y, z);
        assertEquals(x, p.x(), 0);
        assertEquals(y, p.y(), 0);
        assertEquals(z, p.z(), 0);

        p.div_by(f);

        assertEquals(x/f, p.x(), 0);
        assertEquals(y/f, p.y(), 0);
        assertEquals(z/f, p.z(), 0);
    }

    @Test
    public void testScaleBy() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        double f = Math.random();

        Point3d p = new Point3d(x, y, z);
        assertEquals(x, p.x(), 0);
        assertEquals(y, p.y(), 0);
        assertEquals(z, p.z(), 0);

        p.scaleBy(f);

        assertEquals(x*f, p.x(), 0);
        assertEquals(y*f, p.y(), 0);
        assertEquals(z*f, p.z(), 0);
    }

    @Test
    public void testSub() {
        double x1 = Math.random();
        double y1 = Math.random();
        double z1 = Math.random();
        double x2 = Math.random();
        double y2 = Math.random();
        double z2 = Math.random();

        Point3d p1 = new Point3d(x1, y1, z1);
        Point3d p2 = new Point3d(x2, y2, z2);

        assertEquals(x1, p1.x(), 0);
        assertEquals(y1, p1.y(), 0);
        assertEquals(z1, p1.z(), 0);
        assertEquals(x2, p2.x(), 0);
        assertEquals(y2, p2.y(), 0);
        assertEquals(z2, p2.z(), 0);

        p1.sub(p2);

        assertEquals(x1-x2, p1.x(), 0);
        assertEquals(y1-y2, p1.y(), 0);
        assertEquals(z1-z2, p1.z(), 0);
        assertEquals(x2, p2.x(), 0);
        assertEquals(y2, p2.y(), 0);
        assertEquals(z2, p2.z(), 0);
    }

    @Test
    public void testCopy() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        Point3d p = new Point3d(x, y, z);
        Point3d p_copy = p.copy();
        assertEquals(p.x(), p_copy.x(), 0);
        assertEquals(p.y(), p_copy.y(), 0);
        assertEquals(p.z(), p_copy.z(), 0);
    }

    @Test
    public void testIsValid() {
        Point3d p = new Point3d(0, 0, 0);
        assertEquals(true, p.isValid());
        p.markInvalid();
        assertEquals(false, p.isValid());
    }

    @Test
    public void testConstructorSetsValues() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        Point3d p1 = new Point3d(x, y, z);
        assertEquals(p1.toString(), new Point3d(x, y, z).toString());
    }

    @Test
    public void testValuesSetCorrectly() {
        double x = Math.random();
        double y = Math.random();
        double z = Math.random();
        Point3d p = new Point3d(x, y, z);

        assertEquals(x, p.x(), 0d);
        assertEquals(y, p.y(), 0d);
        assertEquals(z, p.z(), 0d);
    }

}

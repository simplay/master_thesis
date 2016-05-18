import datastructures.ColorImage;
import datastructures.FlowField;
import datastructures.Point2d;
import datastructures.Point3d;
import managers.ColorImgManager;
import managers.FlowFieldManager;
import org.junit.Before;
import org.junit.Test;
import readers.ColorImageReader;

import static org.junit.Assert.assertEquals;

public class ColorImageReaderTest {

    @Before
    public void initObjects() {
        ColorImgManager.release();
        FlowFieldManager.release();
        FlowFieldManager.getInstance().addForwardFlow(new FlowField(2, 2, "fw"));
    }

    @Test
    public void testReadingAFileWorks() {
        new ColorImageReader("foobar", "1", "./testdata/");

        ColorImage img = ColorImgManager.getInstance().get(0);

        Point3d p00 = new Point3d(200, 128, 128);
        Point3d p01 = new Point3d(214, 228, 128);
        Point3d p10 = new Point3d(44, 200, 128);
        Point3d p11 = new Point3d(34, 64, 128);

        assertEquals(p00.x()/255, img.valueAt(new Point2d(0,0)).x(), 0);
        assertEquals(p00.y()/255, img.valueAt(new Point2d(0,0)).y(), 0);
        assertEquals(p00.z()/255, img.valueAt(new Point2d(0,0)).z(), 0);

        assertEquals(p01.x()/255, img.valueAt(new Point2d(0,1)).x(), 0);
        assertEquals(p01.y()/255, img.valueAt(new Point2d(0,1)).y(), 0);
        assertEquals(p01.z()/255, img.valueAt(new Point2d(0,1)).z(), 0);

        assertEquals(p10.x()/255, img.valueAt(new Point2d(1,0)).x(), 0);
        assertEquals(p10.y()/255, img.valueAt(new Point2d(1,0)).y(), 0);
        assertEquals(p10.z()/255, img.valueAt(new Point2d(1,0)).z(), 0);

        assertEquals(p11.x()/255, img.valueAt(new Point2d(1,1)).x(), 0);
        assertEquals(p11.y()/255, img.valueAt(new Point2d(1,1)).y(), 0);
        assertEquals(p11.z()/255, img.valueAt(new Point2d(1,1)).z(), 0);
    }
}

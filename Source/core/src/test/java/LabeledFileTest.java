import datastructures.LabeledFile;
import datastructures.LabeledFileLine;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LabeledFileTest {

    @Test
    public void testCreate() {
        LabeledFileLine f_d = new LabeledFileLine("f_d", "1 1");
        LabeledFileLine f_rgb = new LabeledFileLine("f_rgb", "1 1");
        LabeledFileLine e_1 = new LabeledFileLine("e_1", "1 1 1 1");

        ArrayList<LabeledFileLine> labels = new ArrayList<>();
        labels.add(f_d);
        labels.add(f_rgb);
        labels.add(e_1);
        LabeledFile lf = new LabeledFile(labels);

        assertEquals(true, lf.hasDepth());
        assertEquals(true, lf.hasRGB());
        assertEquals(true, lf.hasHasExtrinsicMat());

        labels = new ArrayList<>();
        labels.add(f_d);
        labels.add(f_rgb);
        lf = new LabeledFile(labels);

        assertEquals(true, lf.hasDepth());
        assertEquals(true, lf.hasRGB());
        assertEquals(false, lf.hasHasExtrinsicMat());

        labels = new ArrayList<>();
        labels.add(f_d);
        lf = new LabeledFile(labels);

        assertEquals(true, lf.hasDepth());
        assertEquals(false, lf.hasRGB());
        assertEquals(false, lf.hasHasExtrinsicMat());

        labels = new ArrayList<>();
        lf = new LabeledFile(labels);

        assertEquals(false, lf.hasDepth());
        assertEquals(false, lf.hasRGB());
        assertEquals(false, lf.hasHasExtrinsicMat());
    }

    @Test
    public void testGetLineByLabel() {
        String fd_id = "" + Math.random();
        String content = "" + Math.random();
        LabeledFileLine f_d = new LabeledFileLine(fd_id, content);

        ArrayList<LabeledFileLine> labels = new ArrayList<>();
        labels.add(f_d);
        LabeledFile lf = new LabeledFile(labels);

        assertEquals(f_d, lf.getLineByLabel(fd_id));
    }
}

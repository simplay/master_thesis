import datastructures.LabeledFileLine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LabeledFileLineTest {

    @Test
    public void testGetters() {
        String label = "foo" + Math.random();
        String content = "bar" + Math.random();

        LabeledFileLine lf = new LabeledFileLine(label, content);
        assertEquals(label, lf.getLabel());
        assertEquals(content, lf.getContent());
    }
}

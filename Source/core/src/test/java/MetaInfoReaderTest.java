import managers.MetaDataManager;
import org.junit.Before;
import org.junit.Test;
import readers.MetaInfoReader;

import static org.junit.Assert.assertEquals;

public class MetaInfoReaderTest {

    @Before
    public void initObjects() {
        MetaDataManager.release();
    }

    @Test
    public void testReadingAFileWorks() {
        new MetaInfoReader("foobar", "./testdata/");
        assertEquals(490, MetaDataManager.m());
        assertEquals(630, MetaDataManager.n());
        assertEquals(7, MetaDataManager.samplingRate());
    }
}

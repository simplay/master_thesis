import managers.VarianceManager;
import org.junit.Before;
import org.junit.Test;
import readers.GlobalVarFileReader;

import static org.junit.Assert.assertEquals;

public class GlobalVarFileReaderTest {

    @Before
    public void initObjects() {
        VarianceManager.release();
    }

    @Test
    public void testReadingAFileWorks() {
        new GlobalVarFileReader("foobar", "./testdata/");
        assertEquals(133.7941, VarianceManager.getInstance().getGlobalVarianceValue(0), 0);
        assertEquals(116.3283, VarianceManager.getInstance().getGlobalVarianceValue(1), 0);
        assertEquals(81.4805, VarianceManager.getInstance().getGlobalVarianceValue(2), 0);
        assertEquals(58.2076, VarianceManager.getInstance().getGlobalVarianceValue(3), 0);
    }

}

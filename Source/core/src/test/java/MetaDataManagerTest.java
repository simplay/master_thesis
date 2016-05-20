import managers.MetaDataManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MetaDataManagerTest {

    @Before
    public void initObjects() {
        MetaDataManager.release();
        ArgParser.release();
        String[] args = {"-d", "foobar", "-task", "1"};
        ArgParser.getInstance(args);
    }

    @After
    public void cleanup() {
        ArgParser.release();
        MetaDataManager.release();
    }

    @Test
    public void testGetters() {
        int m = ((int)Math.random()*100);
        int n = ((int)Math.random()*100);
        int samplingRate = ((int)Math.random()*100);

        ArrayList<String> metaData = new ArrayList<>();
        metaData.add(String.valueOf(m));
        metaData.add(String.valueOf(n));
        metaData.add(String.valueOf(samplingRate));
        MetaDataManager.getInstance(metaData);

        assertEquals(m, MetaDataManager.m());
        assertEquals(m, MetaDataManager.getInstance().getHeight());

        assertEquals(n, MetaDataManager.m());
        assertEquals(n, MetaDataManager.getInstance().getWidth());

        assertEquals(samplingRate, MetaDataManager.samplingRate());
        assertEquals(samplingRate, MetaDataManager.getInstance().getSamplingRate());
    }

    @Test
    public void testReportStatus() {
        int m = ((int)Math.random()*100);
        int n = ((int)Math.random()*100);
        int samplingRate = ((int)Math.random()*100);

        ArrayList<String> metaData = new ArrayList<>();
        metaData.add(String.valueOf(m));
        metaData.add(String.valueOf(n));
        metaData.add(String.valueOf(samplingRate));
        MetaDataManager.getInstance(metaData);
        MetaDataManager.reportStatus();
    }
}

import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import similarity.*;

import static org.junit.Assert.assertEquals;

public class SimilarityTaskTypeTest {

    @Before
    public void initObjects() {
        ArgParser.release();
    }

    @Test
    public void testNumberOfAvailableTaskTypes() {
        assertEquals(6, SimilarityTaskType.values().length);
    }

    @Test
    public void testGetName() {
        assertEquals("Sum of Distances", SimilarityTaskType.SD.getName());
        assertEquals("Product of Distances", SimilarityTaskType.PD.getName());
        assertEquals("Product of Euclidian Distances", SimilarityTaskType.PED.getName());
        assertEquals("Sum of Euclidian Distances", SimilarityTaskType.SED.getName());
        assertEquals("Product of Distances all 3d", SimilarityTaskType.PAED.getName());
        assertEquals("Product of Distances all 3d without depth variance", SimilarityTaskType.PAENDD.getName());
    }

    @Test
    public void testGetIdName() {
        assertEquals("sd", SimilarityTaskType.SD.getIdName());
        assertEquals("pd", SimilarityTaskType.PD.getIdName());
        assertEquals("ped", SimilarityTaskType.PED.getIdName());
        assertEquals("sed", SimilarityTaskType.SED.getIdName());
        assertEquals("paed", SimilarityTaskType.PAED.getIdName());
        assertEquals("paendd", SimilarityTaskType.PAENDD.getIdName());
    }

    @Test
    public void testGetUsesDepthCues() {
        assertEquals(false, SimilarityTaskType.SD.getUsesDepthCues());
        assertEquals(false, SimilarityTaskType.PD.getUsesDepthCues());
        assertEquals(true, SimilarityTaskType.PED.getUsesDepthCues());
        assertEquals(true, SimilarityTaskType.SED.getUsesDepthCues());
        assertEquals(true, SimilarityTaskType.PAED.getUsesDepthCues());
        assertEquals(true, SimilarityTaskType.PAENDD.getUsesDepthCues());
    }

    @Test
    public void testUsesColorCues() {
        assertEquals(true, SimilarityTaskType.SD.usesColorCues());
        assertEquals(false, SimilarityTaskType.PD.usesColorCues());
        assertEquals(false, SimilarityTaskType.PED.usesColorCues());
        assertEquals(true, SimilarityTaskType.SED.usesColorCues());
        assertEquals(false, SimilarityTaskType.PAED.usesColorCues());
        assertEquals(false, SimilarityTaskType.PAENDD.usesColorCues());
    }

    @Test
    public void testUsesDepthVariance() {
        assertEquals(false, SimilarityTaskType.SD.usesDepthVariance());
        assertEquals(false, SimilarityTaskType.PD.usesDepthVariance());
        assertEquals(false, SimilarityTaskType.PED.usesDepthVariance());
        assertEquals(false, SimilarityTaskType.SED.usesDepthVariance());
        assertEquals(true, SimilarityTaskType.PAED.usesDepthVariance());
        assertEquals(false, SimilarityTaskType.PAENDD.usesDepthVariance());
    }

    @Test
    public void testGetTaskClass() {
        assertEquals(SumDistTask.class, SimilarityTaskType.SD.getTaskClass());
        assertEquals(ProdDistTask.class, SimilarityTaskType.PD.getTaskClass());
        assertEquals(ProdDistEuclidTask.class, SimilarityTaskType.PED.getTaskClass());
        assertEquals(SumDistEuclidTask.class, SimilarityTaskType.SED.getTaskClass());
        assertEquals(ProdDistAllEuclidTask.class, SimilarityTaskType.PAED.getTaskClass());
        assertEquals(ProdDistAllEuclidNoDepthVarTask.class, SimilarityTaskType.PAENDD.getTaskClass());
    }

    @Test
    public void testTypeById() {
        assertEquals(SimilarityTaskType.SD, SimilarityTaskType.TypeById(1));
        assertEquals(SimilarityTaskType.PD, SimilarityTaskType.TypeById(2));
        assertEquals(SimilarityTaskType.PED, SimilarityTaskType.TypeById(3));
        assertEquals(SimilarityTaskType.SED, SimilarityTaskType.TypeById(4));
        assertEquals(SimilarityTaskType.PAED, SimilarityTaskType.TypeById(5));
        assertEquals(SimilarityTaskType.PAENDD, SimilarityTaskType.TypeById(6));
        assertEquals(null, SimilarityTaskType.TypeById(0));
        assertEquals(null, SimilarityTaskType.TypeById(SimilarityTaskType.values().length+1));
    }

}

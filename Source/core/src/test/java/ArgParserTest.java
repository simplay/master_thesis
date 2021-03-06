import datastructures.NearestNeighborMode;
import datastructures.NearestNeighborsHeap;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import pipeline_components.Logger;
import similarity.SimilarityTask;
import similarity.SimilarityTaskType;

import static org.junit.Assert.assertEquals;

public class ArgParserTest {

    @Before
    public void initObjects() {
        ArgParser.release();
        Logger.getInstance(true);
    }

    @Test
    public void testArgumentAssignment() {
        String dataset = "foobar";
        String[] args = {"-d", dataset, "-task", "2"};
        ArgParser.getInstance(args);
        assertEquals("-task 2 -d foobar", ArgParser.getInstance().toString());
        assertEquals(ArgParser.getDatasetName(), dataset);
        assertEquals(ArgParser.getSimTask(), SimilarityTaskType.PD);
    }

    @Test
    public void testDefaultValues() {
        String dataset = "baz";
        String[] args = {"-d", dataset, "-task", "2"};
        ArgParser.getInstance(args);

        assertEquals(dataset, ArgParser.getDatasetName());
        assertEquals(SimilarityTaskType.PD, ArgParser.getSimTask());
        assertEquals(100, ArgParser.getNearestNeighborhoodCount());
        assertEquals(false, ArgParser.useDepthCues());
        assertEquals(false, ArgParser.useColorCues());
        assertEquals(false, ArgParser.useLocalVariance());
        assertEquals(0.1, ArgParser.getLambda(), 0);
        assertEquals(1, ArgParser.getDepthFieldScale(), 0);
        assertEquals(false, ArgParser.runInDebugMode());
        assertEquals(0.5, ArgParser.getCutProbability(), 0);
        assertEquals("", ArgParser.getCustomFileNamePrefix());
        assertEquals(false, ArgParser.hasCustomFileNamePrefix());
        assertEquals(NearestNeighborMode.ALL, ArgParser.getNNMode());
        assertEquals(false, ArgParser.shouldContinueTrajectories());
        assertEquals(SimilarityTask.minExpectedTrajectoryLength(), ArgParser.getMinExpectedTrajectoryLength());
        assertEquals(20 ,ArgParser.getCountSimilaritiesNotZero());
    }

    @Test
    public void testShouldSkipNNCount() {
        String[] args = {"-d", "foobar", "-task", "2", "-nnm", "top"};
        ArgParser.getInstance(args);
        assertEquals(false, ArgParser.shouldSkipNNCount());

        ArgParser.release();
        String[] args2 = {"-d", "foobar", "-task", "2", "-nnm", "both"};
        ArgParser.getInstance(args2);
        assertEquals(false, ArgParser.shouldSkipNNCount());

        ArgParser.release();
        String[] args3 = {"-d", "foobar", "-task", "2", "-nnm", "all"};
        ArgParser.getInstance(args3);
        assertEquals(true, ArgParser.shouldSkipNNCount());
    }

    @Test
    public void testRelease() {
        String dataset = "foobar";
        String[] args = {"-d", dataset, "-task", "2"};
        ArgParser.getInstance(args);
        assertEquals(ArgParser.getDatasetName(), dataset);
        assertEquals(ArgParser.getSimTask(), SimilarityTaskType.PD);

        String[] args2 = {"-d", "pew", "-task", "2"};
        ArgParser.getInstance(args2);
        assertEquals(ArgParser.getDatasetName(), dataset);
        assertEquals(ArgParser.getSimTask(), SimilarityTaskType.PD);

        ArgParser.release();

        String[] args3 = {"-d", "pew", "-task", "2"};
        ArgParser.getInstance(args3);
        assertEquals(ArgParser.getDatasetName(), "pew");
        assertEquals(ArgParser.getSimTask(), SimilarityTaskType.PD);
    }

    @Test
    public void testCustomArgAssignmentWorks() {
        String dataset = "foobarbaz";
        String[] args = {
                "-d", dataset,
                "-task", "4",
                "-nn", "2000",
                "-var", "1",
                "-lambda", "10.4",
                "-prefix", "foobar",
                "-nnm", "both",
                "-prob", "0.93",
                "-debug", "1",
                "-dscale", "12",
                "-ct", "1",
                "-metl", "6",
                "-snz", "15"
        };
        ArgParser.getInstance(args);

        assertEquals(dataset, ArgParser.getDatasetName());
        assertEquals(SimilarityTaskType.SED, ArgParser.getSimTask());
        assertEquals(2000, ArgParser.getNearestNeighborhoodCount());
        assertEquals(true, ArgParser.useDepthCues());
        assertEquals(true, ArgParser.useColorCues());
        assertEquals(true, ArgParser.useLocalVariance());
        assertEquals(10.4, ArgParser.getLambda(), 0);
        assertEquals(12, ArgParser.getDepthFieldScale(), 0);
        assertEquals(true, ArgParser.runInDebugMode());
        assertEquals(0.93, ArgParser.getCutProbability(), 0);
        assertEquals("foobar", ArgParser.getCustomFileNamePrefix());
        assertEquals(true, ArgParser.hasCustomFileNamePrefix());
        assertEquals(NearestNeighborMode.TOP_AND_WORST_N, ArgParser.getNNMode());
        assertEquals(true, ArgParser.shouldContinueTrajectories());
        assertEquals(6, ArgParser.getMinExpectedTrajectoryLength());
        assertEquals(15 ,ArgParser.getCountSimilaritiesNotZero());
    }

    @Test
    public void testGetHashValue() {
        String dataset = "pew";
        String taskNr = "2";
        String random = "kaboom";

        String[] args = {"-d", dataset, "-task", taskNr, "-knorke", random};
        ArgParser.getInstance(args);

        assertEquals(dataset, ArgParser.getInstance().getHashValue("d"));
        assertEquals(taskNr, ArgParser.getInstance().getHashValue("task"));
        assertEquals(random, ArgParser.getInstance().getHashValue("knorke"));
    }

    @Test
    public void testUseDepthVarianceFalseCase() {
        String dataset = "foobarbaz";
        String[] args = {
                "-d", dataset,
                "-task", "3",
        };
        ArgParser.getInstance(args);
        assertEquals(false, ArgParser.useDepthVariances());
    }

    @Test
    public void testUseDepthVarianceTrueCase() {
        String dataset = "foobarbaz";
        String[] args = {
                "-d", dataset,
                "-task", "5",
        };
        ArgParser.getInstance(args);
        assertEquals(true, ArgParser.useDepthVariances());
    }

    @Test
    public void testCanCallReportUsedParameters() {
        String dataset = "foobarbaz";
        String[] args = {
                "-d", dataset,
                "-task", "4",
                "-nn", "2000",
                "-var", "1",
                "-lambda", "10.4",
                "-prefix", "foobar",
                "-nnm", "both",
                "-prob", "0.93",
                "-debug", "1",
                "-dscale", "12"
        };
        ArgParser.getInstance(args);
        ArgParser.reportUsedParameters();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testThrowsExceptionIfInvalidFormatUsed() {
        String[] args = {"-d"};
        ArgParser.getInstance(args);
    }
}

import com.ma.*;
import org.junit.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class GraphTests {
    private Graph g;

    @Before
    public void setUp() {
        String dataset = "c14";
        g = new Graph();
        new VertexReader(dataset + "_sim.dat", g);
        new TrajectoryLabelReader(dataset + "_labels.txt", g);
        new NeighborhoodReader(dataset + "_spnn.txt", g);
    }

    @Test
    public void testEachVetexHasAssignedSimilarityToOtherVertex() {
        for (Vertex v : g.vertices) {
            assert(v.similarities.length == g.vertexCount());
        }
    }

    @Test
    public void testSimilaritiesAreSymmetric() {
        for (Vertex v : g.vertices) {
            int idx = 0;
            for (Float sim : v.similarities) {
                Vertex other = g.vertices.get(idx);
                float other_sim = other.similarities[v.getId()];
                assert(other_sim == sim);
                idx++;
            }
        }
    }

    @Test
    public void testDiagIsZero() {
        for (Vertex v : g.vertices) {
            assert (v.similarities[v.getId()] == 0.0f);
        }
    }

}
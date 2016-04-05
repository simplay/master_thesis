import com.ma.*;
import org.junit.*;

import static junit.framework.TestCase.assertEquals;


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
            assertEquals(v.similarities.length, g.vertexCount());
        }
    }

    @Test
    public void testSimilaritiesAreSymmetric() {
        for (Vertex v : g.vertices) {
            int idx = 0;
            for (Float sim : v.similarities) {
                Vertex other = g.vertices.get(idx);
                float other_sim = other.similarities[v.getId()];
                assertEquals(other_sim, sim);
                idx++;
            }
        }
    }

    @Test
    public void testDiagIsZero() {
        for (Vertex v : g.vertices) {
            assertEquals(v.similarities[v.getId()], 0.0f);
        }
    }

    @Test
    public void testhashNegatives() {
        float min = 1000.0f;
        for (Vertex v : g.vertices) {
            for (Float sim : v.similarities) {
                if ( sim < min) {
                    min = sim;
                }
            }
        }
        assertEquals(true, min < 0);
    }

}
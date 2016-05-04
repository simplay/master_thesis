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
        for (Vertex v : g.getVertices()) {
            assertEquals(v.similarities.length, g.vertexCount());
        }
    }

    @Test
    public void testSimilaritiesAreSymmetric() {
        for (Vertex v : g.getVertices()) {
            int idx = 0;
            for (Double sim : v.similarities) {
                Vertex other = g.getVertex(idx);
                double other_sim = other.similarities[v.getId()];
                assertEquals(other_sim, sim);
                idx++;
            }
        }
    }

    @Test
    public void testDiagIsZero() {
        for (Vertex v : g.getVertices()) {
            assertEquals(v.similarities[v.getId()], 0.0d);
        }
    }

    @Test
    public void testhashNegatives() {
        double min = 1000.0d;
        for (Vertex v : g.getVertices()) {
            for (Double sim : v.similarities) {
                if ( sim < min) {
                    min = sim;
                }
            }
        }
        assertEquals(true, min < 0);
    }

}
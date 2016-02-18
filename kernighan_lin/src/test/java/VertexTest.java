import com.ma.*;
import org.junit.*;

import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;

public class VertexTest {

    private Graph g;
    Vertex v1;
    Vertex v2;
    Vertex v3;

    HashSet<Vertex> setA;
    HashSet<Vertex> setB;

    @Before
    public void setUp() {
        g = new Graph();
        int vCount = 3;
        setA = new HashSet<>();
        setB = new HashSet<>();

        v1 = new Vertex(0, vCount);
        float[] sim1 = {0.0f, 2.0f, 1.0f};
        v1.setSimilarities(sim1);

        v2 = new Vertex(1, vCount);
        float[] sim2 = {2.0f, 0.0f, 3.0f};
        v2.setSimilarities(sim2);

        v3 = new Vertex(2, vCount);
        float[] sim3 = {1.0f, 3.0f, 0.0f};
        v3.setSimilarities(sim3);

        v1.appendNearestNeighbord(v2);
        v1.appendNearestNeighbord(v3);

        v2.appendNearestNeighbord(v1);
        v2.appendNearestNeighbord(v3);

        v3.appendNearestNeighbord(v1);
        v3.appendNearestNeighbord(v2);

        g.appendVertex(v1);
        g.appendVertex(v2);
        g.appendVertex(v3);

        setA.add(v1);
        setB.add(v2);
        setB.add(v3);

        v1.setPartitionSetLabel(0);
        v2.setPartitionSetLabel(1);
        v3.setPartitionSetLabel(1);
    }

    @Test
    public void testDValue() {
        v1.computeD();

        float I_a1 = 0.0f;
        float E_a1 = 0.0f;

        for(Vertex v : v1.neighbors) {
            if (v.getPartitionSetLabel() != v1.getPartitionSetLabel() ) {
                E_a1 += v.similarities[v1.getId()];
            }
        }

        for(Vertex v : v1.neighbors) {
            if (v.getPartitionSetLabel() == v1.getPartitionSetLabel() ) {
                I_a1 += v.similarities[v1.getId()];
            }
        }
        assertEquals(E_a1-I_a1, v1.getDValue());

        v2.computeD();

        float I_a2 = 0.0f;
        float E_a2 = 0.0f;

        for(Vertex v : v2.neighbors) {
            if (v.getPartitionSetLabel() != v2.getPartitionSetLabel() ) {
                E_a2 += v.similarities[v2.getId()];
            }
        }

        for(Vertex v : v2.neighbors) {
            if (v.getPartitionSetLabel() == v2.getPartitionSetLabel() ) {
                I_a2 += v.similarities[v2.getId()];
            }
        }
        assertEquals(E_a2-I_a2, v2.getDValue());

        v3.computeD();

        float I_a3 = 0.0f;
        float E_a3 = 0.0f;

        for(Vertex v : v3.neighbors) {
            if (v.getPartitionSetLabel() != v3.getPartitionSetLabel() ) {
                E_a3 += v.similarities[v3.getId()];
            }
        }

        for(Vertex v : v3.neighbors) {
            if (v.getPartitionSetLabel() == v3.getPartitionSetLabel() ) {
                I_a3 += v.similarities[v3.getId()];
            }
        }
        assertEquals(E_a3-I_a3, v3.getDValue());
    }
}

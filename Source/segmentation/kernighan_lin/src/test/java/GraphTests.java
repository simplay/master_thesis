import com.ma.*;
import junit.framework.*;
import org.junit.*;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;


public class GraphTests {
    private Graph g;

    @Before
    public void setUp() {
        String dataset = "testdata/dummy";
        g = new Graph();
        new VertexReader(dataset + "_sim.dat", g, false);
        new TrajectoryLabelReader(dataset + "_labels.txt", g, false);
        new NeighborhoodReader(dataset + "_spnn.txt", g, false);
    }

    @Test
    public void testSimValuesAreCorrectlyAssigned() {
        Object[] vertices = g.getVertices().toArray();

        double[][] W = new double[3][3];

        double[] assignedSims1 = {0.0d, 0.8d, 0.1d};
        double[] assignedSims2 = {0.8d, 0.0d, -0.3d};
        double[] assignedSims3 = {0.1d, -0.3d, 0.0d};

        W[0] = assignedSims1;
        W[1] = assignedSims2;
        W[2] = assignedSims3;

        int idx = 0;
        for (int k = 0; k < 3; k++) {
            for (double d : ((Vertex)vertices[k]).similarities) {
                assertEquals(W[k][idx], d);
                idx++;
            }
            idx = 0;
        }
    }

    @Test
    public void testNeighborValuesAreCorrectlyAssigned() {
        Object[] vertices = g.getVertices().toArray();

        int[][] nn = new int[3][2];

        int[] n1 = {2, 3};
        int[] n2 = {1, 3};
        int[] n3 = {1, 2};

        nn[0] = n1;
        nn[1] = n2;
        nn[2] = n3;

        int idx = 0;
        for (int k = 0; k < 3; k++) {
            LinkedList<Vertex> neighbors = ((Vertex) vertices[k]).getNeighbors();
            Vertex nn1 = neighbors.get(0);
            Vertex nn2 = neighbors.get(1);

            if (nn1.getId() > nn2.getId()) {
                neighbors.set(0, nn2);
                neighbors.set(1, nn1);

            } else {
                neighbors.set(0, nn1);
                neighbors.set(1, nn2);
            }

            for (Vertex n : neighbors) {
                System.out.println("k="+ k + " " + vertices[k].toString());
                Vertex gtNeighbor = g.findVertexByTrajectoryId(nn[k][idx]);
                System.out.println("gt=" + gtNeighbor.toString() + " n=" + n.toString());
                assertEquals(gtNeighbor, n);
                idx++;
            }
            idx = 0;
        }
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
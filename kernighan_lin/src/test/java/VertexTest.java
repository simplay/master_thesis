import com.ma.*;
import junit.framework.*;
import org.junit.*;
import org.junit.Test;

import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;

public class VertexTest {

    private Graph g;
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Vertex v4;


    PartitionSet setA;
    PartitionSet setB;

    /**
    @Before
    public void setUp() {
        g = new Graph();
        int vCount = 3;
        setA = new PartitionSet();
        setB = new PartitionSet();

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
    }
     **/

    @Test
    public void testFoo() {
        g = new Graph();
        int vCount = 4;

        v1 = new Vertex(0, vCount);
        float[] sim1 = {0.0f, -1.0f, 1.0f, 0.0f};
        v1.setSimilarities(sim1);

        v2 = new Vertex(1, vCount);
        float[] sim2 = {-1.0f, 0.0f, 0.0f, 1.0f};
        v2.setSimilarities(sim2);

        v3 = new Vertex(2, vCount);
        float[] sim3 = {1.0f, 0.0f, 0.0f, -1.0f};
        v3.setSimilarities(sim3);

        v4 = new Vertex(3, vCount);
        float[] sim4 = {0.0f, 1.0f, -1.0f, 0.0f};
        v4.setSimilarities(sim4);

        v1.appendNearestNeighbord(v2);
        v1.appendNearestNeighbord(v3);

        v2.appendNearestNeighbord(v1);
        v2.appendNearestNeighbord(v4);

        v3.appendNearestNeighbord(v1);
        v3.appendNearestNeighbord(v4);

        v4.appendNearestNeighbord(v2);
        v4.appendNearestNeighbord(v3);

        g.appendVertex(v1);
        g.appendVertex(v2);
        g.appendVertex(v3);
        g.appendVertex(v4);

        new GraphPartitioner(g).runKernighanLin();

        for (Vertex v : g.vertices) {
            System.out.println(v.getId() +  " " + v.getPartitionLabel());
        }

    }


}

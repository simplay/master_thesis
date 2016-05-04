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
    /**
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
     **/


    @Test
    public void testFoo() {
        g = new Graph();
        int vCount = 4;
        int negEdge = 4;
        int negEdge_j = 4;
        int M = 16;
        int N = 16;
        int idx = 0;
        Vertex[][] vertices = new Vertex[M][N];
        for (int i=0; i < M; i++) {
            for (int j=0; j < N; j++) {
                vertices[i][j] = new Vertex(idx, M*N);
                idx++;
            }
        }

        for (int i=0; i < M; i++) {

            for (int j=0; j < N; j++) {
                double[] sims = new double[M*N];

                if (j+1 < N) {
                    vertices[i][j].appendNearestNeighbord(vertices[i][j+1]);
                    sims[vertices[i][j+1].getId()] = 1;
                    if (j+1 == negEdge_j) {
                        sims[vertices[i][j+1].getId()] = -1;
                    }
                }

                if (i+1 < M) {
                    vertices[i][j].appendNearestNeighbord(vertices[i+1][j]);
                    sims[vertices[i+1][j].getId()] = 1;

                    if (i+1 == negEdge) {
                        sims[vertices[i+1][j].getId()] = -1;
                    }
                }

                if (i > 0) {
                    vertices[i][j].appendNearestNeighbord(vertices[i-1][j]);
                    sims[vertices[i-1][j].getId()] = 1;

                    if (i == negEdge) {
                        sims[vertices[i-1][j].getId()] = -1;
                    }
                }

                if (j > 0) {
                    vertices[i][j].appendNearestNeighbord(vertices[i][j-1]);
                    sims[vertices[i][j-1].getId()] = 1;


                    if (j == negEdge_j) {
                        sims[vertices[i][j-1].getId()] = -1;
                    }

                }
                vertices[i][j].setSimilarities(sims);

            }

        }
        int counter = 0;
        for (int i=0; i < M; i++) {
            for (int j=0; j < N; j++) {
                g.appendVertex(counter, vertices[i][j]);
                counter++;
            }
        }

        GraphPartitioner gp = new GraphPartitioner(g,2);


        for (Vertex v : gp.getSetA()) {
            for (Vertex other : gp.getSetB()) {
                assertEquals(v == other, false);
            }
        }

        for (Vertex v : gp.getSetB()) {
            for (Vertex other : gp.getSetA()) {
                assertEquals(v == other, false);
            }
        }

        gp.runKernighanLin(1);
        printGraph(M, N, vertices);
        gp.runKernighanLin(1);
        printGraph(M, N, vertices);
        gp.runKernighanLin(1);
        printGraph(M, N, vertices);

        for (Vertex v : g.getVertices()) {
            idx = 0;
            for (Double sim : v.similarities) {
                Vertex other = g.getVertex(idx);
                double other_sim = other.similarities[v.getId()];
                assertEquals(other_sim, sim);
                idx++;
            }
        }

    }

    private void printGraph(int M, int N, Vertex[][] vertices) {
        for (int i=0; i < M; i++) {
            for (int j=0; j < N; j++) {
                System.out.print(vertices[i][j].getPartitionLabel());
                if (j+1 < N && i+1 <M) {
                    Vertex v = vertices[i][j];
                    Vertex other = vertices[i][j+1];
                    Vertex other_ = vertices[i + 1][j];

                    if (v.getSimValue(other.getId()) < 0 && v.getSimValue(other_.getId()) < 0 ) {
                        System.out.print("+");
                    }
                    else if(v.getSimValue(other.getId()) < 0 ){
                        System.out.print("|");
                    }
                    else if(v.getSimValue(other_.getId()) < 0){
                        System.out.print("_");
                    }
                    else {
                        System.out.print(" ");
                    }
                }
                else if (i+1 < M) {
                    Vertex v = vertices[i][j];
                    Vertex other = vertices[i + 1][j];
                    if (v.getSimValue(other.getId()) < 0 ) {
                        System.out.print("_");
                    } else {
                        System.out.print(" ");
                    }
                }
                else if (j+ 1 < N) {
                    Vertex v = vertices[i][j];
                    Vertex other = vertices[i][j+1];
                    if (v.getSimValue(other.getId()) < 0 ) {
                        System.out.print("|");
                    } else {
                        System.out.print(" ");
                    }
                }
            }
            System.out.println();
        }
    }

}
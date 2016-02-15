package com.ma;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);

        Graph g = new Graph();

        System.out.println("Loading relevant input data...");
        System.out.println();
        new VertexReader("c14_sim.dat", g);
        new TrajectoryLabelReader("c14_labels.txt", g);
        new NeighborhoodReader("c14_spnn.txt", g);
        System.out.println("Computing the graph partitioning...");
        System.out.println();
        new GraphPartitioner(g).runKernighanLin();
    }
}

package com.ma;


import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static boolean DUMP_DATA = false;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        String dataset = "c14";
        String baseOutputPath = "../output/graph_part/";
        int clusterCount = 3;
        int dummyCount = 500;
        int max_iterations = 10;
        int repetitionCount = 6;

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);

        System.out.println("Loading relevant input data...");
        System.out.println();
        Graph g = new Graph(dataset);
      
        long tillLoadTime = System.currentTimeMillis();
        System.out.println("Loading data took " + (tillLoadTime - startTime) / 1000.0 + " seconds");
        System.out.println("Computing the graph partitioning...");
        System.out.println();

        new GraphPartitioner(g, clusterCount, dummyCount, repetitionCount).runKernighanLin(max_iterations);
        long tillPartitioningTime = System.currentTimeMillis();
        System.out.println("Graph partitioning took " + (tillPartitioningTime - tillLoadTime) / 1000.0 + " seconds");

        // TODO: output computed graph partitioning
        System.out.println("Storing computed partition...");
        g.savePartitionToFile(baseOutputPath + dataset + "_part.txt");
        long totalTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + (totalTime - startTime) / 1000.0 + " seconds");
    }
}

package com.ma;


import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static boolean DUMP_DATA = false;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        String baseOutputPath = "../../output/graph_part/";
        ArgParser.getInstance(args);
        ArgParser.reportUsedParameters();

        String dataset = ArgParser.getDatasetName();
        String customPrefix = ArgParser.getCustomFileNamePrefix();
        int clusterCount = ArgParser.getClusterCount();
        int dummyCount = ArgParser.getDummyCount();
        int max_iterations = ArgParser.getMaxIterCountPerCluster();
        int repetitionCount = ArgParser.getRepetitionCount();

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

        System.out.println("Storing computed partition...");
        if (!customPrefix.isEmpty()) customPrefix = "_" + customPrefix;
        g.savePartitionToFile(baseOutputPath + dataset + customPrefix + "_part.txt");
        long totalTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + (totalTime - startTime) / 1000.0 + " seconds");
    }
}

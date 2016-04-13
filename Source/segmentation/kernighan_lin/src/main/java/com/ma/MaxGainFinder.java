package com.ma;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MaxGainFinder {

    private static MaxGainFinder instance = null;
    private ExecutorService executor;

    public static MaxGainFinder getInstance() {
        if (instance == null) {
            instance = new MaxGainFinder();
        }
        return instance;
    }

    public MaxGainFinder() {
        initializeThreadPool();
    }

    public void initializeThreadPool() {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Assigning neighborhood using " + availableThreads + " threads...");
        executor = Executors.newFixedThreadPool(availableThreads);
    }

    public static MaxGainContainer getMaxGain(Graph graph, List<Vertex> sortedByDvalueA,
                                              List<Vertex> sortedByDvalueB, Vertex topA, Vertex topB) {
        return getInstance().findMaxGain(graph, sortedByDvalueA, sortedByDvalueB, topA, topB);
    }

    public MaxGainContainer findMaxGain(Graph graph, List<Vertex> sortedByDvalueA,
                                        List<Vertex> sortedByDvalueB, Vertex topA, Vertex topB) {
        float maxgain = graph.gain(topA, topB);
        MaxGainContainer mgc = new MaxGainContainer(maxgain, topA, topB);
        LinkedList<MaxGainTask> tasks = new LinkedList<>();

        int step = 1;
        int N = sortedByDvalueA.size();
        for (int k = 0; k <= N; k = k + step) {
            if (k + step > N) break;
            List<Vertex> sublistA = sortedByDvalueA.subList(k, k + step);
            tasks.add(new MaxGainTask(graph, mgc, sublistA, sortedByDvalueB, topA, topB, maxgain));
        }
        int diff = (N % step);
        if (diff != 0) {
            List<Vertex> lastListA = sortedByDvalueA.subList(N - diff, N);
            tasks.add(new MaxGainTask(graph, mgc, lastListA, sortedByDvalueB, topA, topB, maxgain));
        }

        for (MaxGainTask task : tasks) {
            executor.execute(task);
        }

        return mgc;
    }

    public static void start() {
        getInstance().initializeThreadPool();
    }

    public static void close() {
        getInstance().shutDown();
    }

    public void shutDown() {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}

    }
}

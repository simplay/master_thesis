package com.ma;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

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
        Logger.println("Finding max gain by using " + availableThreads + " threads...");
        if (availableThreads < 1) availableThreads = 1;
        executor = Executors.newFixedThreadPool(availableThreads);
    }

    public static MaxGainContainer getMaxGain(Graph graph, List<Vertex> sortedByDvalueA,
                                              List<Vertex> sortedByDvalueB, Vertex topA, Vertex topB) {
        return getInstance().findMaxGain(graph, sortedByDvalueA, sortedByDvalueB, topA, topB);
    }

    public MaxGainContainer findMaxGain(Graph graph, List<Vertex> sortedByDvalueA,
                                        List<Vertex> sortedByDvalueB, Vertex topA, Vertex topB) {

        double maxgain = graph.gain(topA, topB);
        MaxGainContainer mgc = new MaxGainContainer(maxgain, topA, topB);
        LinkedList<MaxGainTask> tasks = new LinkedList<>();

        int step = 50;
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

        Collection<Future<MaxGainTask>> futures = new LinkedList<>();
        for (MaxGainTask task : tasks) {
            futures.add((Future<MaxGainTask>) executor.submit(task));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return mgc;
    }

    public static void close() {
        getInstance().shutDown();
    }

    public void shutDown() {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}
        instance = null;
    }
}

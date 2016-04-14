package com.ma;


import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * Created by simplay on 15/02/16.
 */
public class NeighborhoodReader extends GraphFileReader {

    private int lineCounter = 0;
    private LinkedList<NeighborAssignmentTask> tasks;

    public NeighborhoodReader(String fname, Graph graph) {
        super(fname, graph);
    }

    @Override
    protected void stepsBeforeFileProcessing() {
        tasks = new LinkedList<>();
    }

    @Override
    protected void stepsAfterFileProcessing() {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Assigning neighborhood using " + availableThreads + " threads...");

        ExecutorService executor = Executors.newFixedThreadPool(availableThreads);

        Collection<Future<NeighborAssignmentTask>> futures = new LinkedList<>();
        for (NeighborAssignmentTask task : tasks) {
            futures.add((Future<NeighborAssignmentTask>) executor.submit(task));
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

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}

        System.out.println("Assigned neighborhood for " + lineCounter + " trajectories.");
    }

    @Override
    protected void processFileLine(String fline) {
        String[] tokens = fline.split(", ");
        //graph.assignNearestNeighborsForVertex(lineCounter, tokens);
        tasks.add(new NeighborAssignmentTask(graph, tokens, lineCounter));
        lineCounter++;
    }
}

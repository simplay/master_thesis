package com.ma;

/**
 * Created by simplay on 15/02/16.
 */
public class NeighborhoodReader extends GraphFileReader {

    private int lineCounter;

    public NeighborhoodReader(String fname, Graph graph) {
        super(fname, graph);
    }

    @Override
    protected void stepsBeforeFileProcessing() {

    }

    @Override
    protected void stepsAfterFileProcessing() {
        System.out.println("Assigned neighborhood for " + lineCounter + " trajectories.");
    }

    @Override
    protected void processFileLine(String fline) {
        String[] tokens = fline.split(", ");
        graph.assignNearestNeighborsForVertex(lineCounter, tokens);
        lineCounter++;
    }
}

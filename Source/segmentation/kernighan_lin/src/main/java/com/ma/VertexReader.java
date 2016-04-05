package com.ma;


/**
 * Created by simplay on 15/02/16.
 */
public class VertexReader extends GraphFileReader {
    private int processedLineCount = 0;

    public VertexReader(String fname, Graph graph) {
        super(fname, graph);
    }

    @Override
    protected void stepsBeforeFileProcessing() {
        for (int id = 0; id < fileLineCount; id++) {
            graph.appendVertex(new Vertex(id, fileLineCount));
        }
    }

    @Override
    protected void stepsAfterFileProcessing() {
        System.out.println("Similarity Reading done: Processed " + processedLineCount + " vertices.");
    }

    @Override
    protected void processFileLine(String fline) {
        String[] tokens = fline.split(",");
        graph.updateSimilaritiesForVertex(processedLineCount, tokens);
        processedLineCount++;
    }
}

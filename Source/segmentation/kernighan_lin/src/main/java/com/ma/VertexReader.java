package com.ma;

public class VertexReader extends GraphFileReader {
    private int processedLineCount = 0;

    public VertexReader(String fname, Graph graph, boolean usePipelinePathAssumption) {
        super(fname, graph, usePipelinePathAssumption);
    }

    public VertexReader(String fname, Graph graph) {
        this(fname, graph, true);
    }

    @Override
    protected void stepsBeforeFileProcessing() {
        for (int id = 0; id < fileLineCount; id++) {
            Vertex v = new Vertex(id, fileLineCount);
            graph.appendVertex(id, v);
        }
    }

    @Override
    protected void stepsAfterFileProcessing() {
        Logger.println("Similarity Reading done: Processed " + processedLineCount + " vertices.");
    }

    @Override
    protected void processFileLine(String fline) {
        String[] tokens = fline.split(",");
        graph.updateSimilaritiesForVertex(processedLineCount, tokens);
        processedLineCount++;
    }
}

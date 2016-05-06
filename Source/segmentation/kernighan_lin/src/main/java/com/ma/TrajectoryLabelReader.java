package com.ma;

public class TrajectoryLabelReader extends GraphFileReader {

    public TrajectoryLabelReader(String fname, Graph graph, boolean usePipelinePathAssumption) {
        super(fname, graph, usePipelinePathAssumption);
    }

    public TrajectoryLabelReader(String fname, Graph graph) {
        this(fname, graph, true);
    }

    @Override
    protected void stepsBeforeFileProcessing() {
    }

    @Override
    protected void stepsAfterFileProcessing() {
        Logger.println("Trajectory label identifier reading done");
    }

    @Override
    protected void processFileLine(String fline) {
        String[] tokens = fline.split(" ");
        int idx = 0;
        for (String token : tokens) {
            Vertex v = graph.getVertex(idx);
            v.setTrajectoryId(Integer.parseInt(token));
            idx++;
        }
    }
}

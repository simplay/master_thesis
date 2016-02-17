package com.ma;


/**
 * Created by simplay on 15/02/16.
 */
public class TrajectoryLabelReader extends GraphFileReader {

    public TrajectoryLabelReader(String fname, Graph graph) {
        super(fname, graph);
    }

    @Override
    protected void stepsBeforeFileProcessing() {
    }

    @Override
    protected void stepsAfterFileProcessing() {
        System.out.println("Trajectory label identifier reading done");
    }

    @Override
    protected void processFileLine(String fline) {
        String[] tokens = fline.split(" ");
        int idx = 0;
        for (String token : tokens) {
            Vertex v = graph.vertices.get(idx);
            v.setTrajectoryId(Integer.parseInt(token));
            idx++;
        }
    }
}

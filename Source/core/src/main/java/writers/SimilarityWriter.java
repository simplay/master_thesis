package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;
import pipeline_components.ArgParser;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SimilarityWriter extends LargeFileWriter{

    public SimilarityWriter(String dataset) {
        String outputPath = "../output/similarities/" + getOutputFilenamePrefix(dataset) + "_sim.dat";
        reportFilePath(outputPath, "Writing similarity matrix to output file:");

        int n = TrajectoryManager.getTrajectories().size();
        int counter = 0;
        List<String> strLines = new LinkedList<>();
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            String tmp = tra.toSimilarityString();
            tmp = tmp.split("\\[|\\]")[1];
            strLines.add(tmp + ((counter < n-1)? "\n" : ""));
            counter++;
        }

        try {
            writeFile(strLines, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

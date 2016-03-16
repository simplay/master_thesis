package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SimilarityWriter extends LargeFileWriter{

    public SimilarityWriter(String dataset) throws IOException {
        String outputPath = "../output/similarities/"+dataset+"_foobar1337.dat";
        int n = TrajectoryManager.getTrajectories().size();
        int counter = 0;
        List<String> strLines = new LinkedList<>();
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            String tmp = tra.toSimilarityString();
            tmp = tmp.split("\\[|\\]")[1];
            strLines.add(tmp + ((counter < n-1)? "\n" : ""));
            counter++;
        }
        writeFile(strLines, outputPath);
    }
}

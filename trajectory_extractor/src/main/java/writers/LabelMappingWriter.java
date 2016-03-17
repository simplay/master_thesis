package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;

import java.io.IOException;

public class LabelMappingWriter extends LargeFileWriter {

    public LabelMappingWriter(String dataset) {
        String outputPath = "../output/similarities/"+dataset + "_labels.txt";
        reportFilePath(outputPath, "Writing label mappings to output file:");
        String line = "";
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            line += tra.getLabel() + " ";
        }
        line.trim();

        try {
            writeFile(line, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class NearestSpatialNeighborsWriter extends LargeFileWriter {

    public NearestSpatialNeighborsWriter(String dataset, int nn_count) {
        String outputPath = "../output/similarities/"+dataset + "_spnn.txt";
        reportFilePath(outputPath, "Writing the " + nn_count + " nearest avg spatial dist neighbors to output file:");


        int n = TrajectoryManager.getTrajectories().size();
        int counter = 0;
        List<String> strLines = new LinkedList<>();
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            List<Integer> nn = tra.nearestAvgSpatialNeighbors(nn_count);
            String nn_as_string = nn.toString().split("\\[|\\]")[1].trim();
            strLines.add(nn_as_string + ((counter < n-1)? "\n" : ""));
            counter++;
        }

        try {
            writeFile(strLines, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

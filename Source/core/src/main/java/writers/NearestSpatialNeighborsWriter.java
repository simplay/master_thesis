package writers;

import datastructures.NearestNeighborMode;
import datastructures.Trajectory;
import managers.TrajectoryManager;
import pipeline_components.ArgParser;
import pipeline_components.Logger;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Writes the spatially nearest neighbors per trajectory into a text file.
 * The nearest neighbors are selected according to the used neighbor selection mode
 * (that is either the best n neighbors or the best and worst n neighbors). Also the number
 * of neighbors per trajectory that should be returned is given as a runtime argument.
 *
 * File naming convention: all generated files are stored at `"../output/similarities/"`.
 * A label file is a .txt file with the filename suffix `_spnn`. It's filename prefix is determined
 * by the function LargeFileWriter#getOutputFilenamePrefix()
 */
public class NearestSpatialNeighborsWriter extends LargeFileWriter {

    /**
     * Save the nearest neighbors file at a given location for a selected dataset.
     *
     * @param dataset name of the dataset we are running.
     * @param fileStoreAtPath directory where we want to store the generated labels file.
     */
    public NearestSpatialNeighborsWriter(String dataset, String fileStoreAtPath) {

        // determine amount of nearest neighbors
        int nn_count = ArgParser.getNearestNeighborhoodCount();
        if (ArgParser.shouldSkipNNCount()) {
            Logger.println(" => Info: Extracting the complete neighborhood.");
            nn_count = TrajectoryManager.getInstance().trajectoryCount();
        }

        String outputPath = fileStoreAtPath + getOutputFilenamePrefix(dataset) + "_spnn.txt";
        reportFilePath(outputPath, "Writing the " + nn_count + " nearest avg spatial dist neighbors to output file:");

        List<String> strLines = getSerializedTrajectoryNeighbors();

        try {
            writeFile(strLines, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the nearest neighbors file for a selected dataset at `../output/similarities/`
     *
     * @param dataset name of the dataset we are running.
     */
    public NearestSpatialNeighborsWriter(String dataset) {
        this(dataset, "../output/similarities/");
    }

    /**
     * Returns a list (ordered w.r.t. to trajectory labels)
     * indices of all nearest neighbors for every trajectory.
     *
     * @return list of all trajectory nearest neighbors.
     */
    public List<String> getSerializedTrajectoryNeighbors() {
        int nn_count = ArgParser.getNearestNeighborhoodCount();
        int n = TrajectoryManager.getTrajectories().size();
        int counter = 0;
        List<String> strLines = new LinkedList<>();
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            List<Integer> nn;

            if (ArgParser.getNNMode() == NearestNeighborMode.ALL) {
                nn = tra.allNearestNeighbors();
            } else {
                nn = tra.nearestAvgSpatialNeighbors(nn_count);
            }

            String nn_as_string = nn.toString().split("\\[|\\]")[1].trim();
            strLines.add(nn_as_string + ((counter < n-1)? "\n" : ""));
            counter++;
        }
        return strLines;
    }
}

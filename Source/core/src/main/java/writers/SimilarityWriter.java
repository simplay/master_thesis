package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates the trajectory affinity matrix, stored as a - by Matlab readable - .dat file.
 * It can be loaded by using `load('affinitymatrix_name.dat')`
 * the value at position (i,j) encodes the similarity between the two trajectories i,j.
 * w.r.t a selected affinity task.
 *
 * File naming convention: all generated files are stored at `"../output/similarities/"`.
 * A label file is a .dat file with the filename suffix `_sim`. It's filename prefix is determined
 * by the function LargeFileWriter#getOutputFilenamePrefix()
 */
public class SimilarityWriter extends LargeFileWriter {

    /**
     * Save the affinity matrix at a given location for a selected dataset.
     *
     * @param dataset name of the dataset we are running.
     * @param fileStoreAtPath directory where we want to store the generated labels file.
     */
    public SimilarityWriter(String dataset, String fileStoreAtPath) {
        String outputPath = fileStoreAtPath + getOutputFilenamePrefix(dataset) + "_sim.dat";

        int traCount = TrajectoryManager.getTrajectories().size();
        String matDim = "(" + traCount + " x " + traCount + ")";
        reportFilePath(outputPath, "Writing " + matDim + " similarity matrix:");

        List<String> rows = getMatrixRows();

        try {
            writeFile(rows, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the affinity matrix for a selected dataset at `../output/similarities/`
     *
     * @param dataset name of the dataset we are running.
     */
    public SimilarityWriter(String dataset) {
        this(dataset, "../output/similarities/");
    }

    /**
     * Returns the affinity row (in order) as a string list
     * @return list of row strings
     */
    public List<String> getMatrixRows() {
        int n = TrajectoryManager.getTrajectories().size();
        int counter = 0;
        List<String> strLines = new LinkedList<>();
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            String tmp = tra.toSimilarityString();
            tmp = tmp.split("\\[|\\]")[1];
            strLines.add(tmp + ((counter < n-1)? "\n" : ""));
            counter++;
        }
        return strLines;
    }
}

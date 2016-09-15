package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;
import java.io.IOException;

/**
 * Writes a text file that contains a series of numbers.
 * Each number corresponds to a trajectory label. The position of
 * a label in the written file corresponds to the row/column index of the trajectory in the affinity matrix.
 * Thus, this file is denotes a mapping between the trajectory label and the affinity matrix indices.
 * It is used to fetch an affinity matrix column/row by a trajectory label.
 *
 * File naming convention: all generated files are stored at `"../output/similarities/"`.
 * A label file is a .txt file with the filename suffix `_labels`. It's filename prefix is determined
 * by the function LargeFileWriter#getOutputFilenamePrefix()
 */
public class LabelMappingWriter extends LargeFileWriter {

    /**
     * Save the LabelMapping file at a given location for a selected dataset.
     *
     * @param dataset name of the dataset we are running.
     * @param fileStoreAtPath directory where we want to store the generated labels file.
     */
    public LabelMappingWriter(String dataset, String fileStoreAtPath) {
        String outputPath = fileStoreAtPath + getOutputFilenamePrefix(dataset) + "_labels.txt";
        reportFilePath(outputPath, "Writing label mappings to output file:");

        String line = getSerializedTrajectoryLabels();

        try {
            writeFile(line, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the labelMapping file for a selected dataset at `../output/similarities/`
     *
     * @param dataset name of the dataset we are running.
     */
    public LabelMappingWriter(String dataset) {
        this(dataset, "../output/similarities/");
    }

    /**
     * Get the file representation of the trajectory labels
     *
     * @return file content as string.
     */
    public String getSerializedTrajectoryLabels() {
        String content = "";
        for (Trajectory tra : TrajectoryManager.getTrajectories()) {
            content += tra.getLabel() + " ";
        }
        content = content.trim();
        return content;
    }
}

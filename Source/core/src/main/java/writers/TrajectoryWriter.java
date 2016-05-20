package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * TrajectoryWriter dumps all valid trajectories to a file.
 * The trajectories are serialized and sequentially written into the file.
 *
 * The file is stored at `./output/trajectories/` and named `traj_out_PREFIX_fc_FRAME_COUNT.txt`.
 *
 * The format of a serialized, dumped trajectory looks like the following:
 *  ### L:LABEL_ID S:START_FRAME_IDX C:TRACKING_POINT_COUNT
 *  ROW_POS_1 COL_POS_1
 *  ...
 *  ROW_POS_n COL_POS_n
 *
 * Example: Trajectory with label value 89, starting in frame 1, containing 4 tracking points.
 *  ### L:89 S:1 C:4
 *  28.0 36.0
 *  24.30163431168 21.076683998100002
 *  22.61896187125924 10.278041176550083
 *  20.759067112609863 4.8662892429490565
 *  18.450482857235027 5.685143127580147
 *
 * @info: Note that we are reporting java-ish coordinates,
 *  i.e. indices are starting at 0. This implies
 *  that we have to perform an index shift when using the extracted coordinates
 *  in any language that starts index counting at 1 (such as Matlab or Ruby).
 *
 */
public class TrajectoryWriter extends LargeFileWriter {

    /**
     * Save the serialized trajectories at a given location for a chosen dataset.
     *
     * @param dataset name of the dataset we are running.
     * @param till_index last index of frames in sequence we want to store.
     * @param outputBasePath directory where we want to store the serialized trajectories.
     */
    public TrajectoryWriter(String dataset, int till_index, String outputBasePath) {
        String outputPath = outputBasePath + "traj_out_" + getOutputFilenamePrefix(dataset) + "_fc_" + till_index + ".txt";
        reportFilePath(outputPath, "Writing trajectories to output file:");

        LinkedList<Trajectory> sortedByLabel = new LinkedList<>(TrajectoryManager.getTrajectories());
        Collections.sort(sortedByLabel);
        List<String> strLines = new LinkedList<>();
        for (Trajectory tra : sortedByLabel) {
            strLines.add(tra.getOutputString());
        }

        try {
            writeFile(strLines, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the serialized trajectories for a chosen dataset using the pipeline path conventions.
     *
     * @param dataset name of the dataset we are running.
     * @param till_index last index of frames in sequence we want to store.
     */
    public TrajectoryWriter(String dataset, int till_index) {
        this(dataset, till_index, "../output/trajectories/");
    }
}

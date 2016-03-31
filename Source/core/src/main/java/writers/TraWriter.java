package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Saves all trajectories to a file, using the old format,
 * readable by the ruby code basis.
 *
 * @info: Note that we are reporting java-ish coordinates,
 *  i.e. indices are starting at 0. This implies
 *  that we have to perform an index shift when using the extracted coordinates
 *  in any language that starts index counting at 1 (such as Matlab or Ruby).
 *
 */
public class TraWriter extends LargeFileWriter {

    public TraWriter(String output_base_path, String dataset, int till_index) {
        String outputPath = output_base_path + "traj_out_" + dataset+"_fc_" + till_index + ".txt";
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
}

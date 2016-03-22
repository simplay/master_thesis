package writers;

import datastructures.Trajectory;
import managers.TrajectoryManager;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TraWriter extends LargeFileWriter {

    public TraWriter(String output_base_path, String dataset, int till_index) {
        String outputPath = output_base_path + "traj_out_" + dataset+"_fc_" + till_index + ".txt";
        reportFilePath(outputPath, "Writing trajectories to output file:");

        int n = TrajectoryManager.getTrajectories().size();
        int counter = 0;

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

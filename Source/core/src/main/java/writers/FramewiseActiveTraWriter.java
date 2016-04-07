package writers;

import managers.TrajectoryManager;
import pipeline_components.ArgParser;

import java.io.File;
import java.io.IOException;

public class FramewiseActiveTraWriter extends LargeFileWriter {

    public FramewiseActiveTraWriter(String dataset, int till_index) {
        String outputPath = "../output/trajectory_label_frame/" + dataset + "/";
        reportFilePath(outputPath, "Writing active trajectory frame files:");

        // create directory if not present yet
        (new File(outputPath)).mkdirs();

        // TODO: replace this approach by a more efficient version: do not iterate and compute active tra (compute them beforehand, during thracking step)
        String fname = "";
        String prefix = ArgParser.getCustomFileNamePrefix();
        if (!prefix.isEmpty()) prefix += "_";
        for (int idx = 0; idx <= till_index; idx++) {
            System.out.println("+ Iteration " + (idx+1));
            fname = outputPath + prefix + "active_tra_f_" + (idx+1) + ".txt";
            String activeTra = TrajectoryManager.getInstance().toFramewiseOutputString(idx);
            try {
                writeFile(activeTra, fname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

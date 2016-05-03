package writers;

import managers.TrajectoryManager;
import pipeline_components.ArgParser;
import pipeline_components.Logger;

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
        String fpname = "";
        for (int idx = 0; idx <= till_index; idx++) {
            fname = getOutputFilenamePrefix() + "_active_tra_f_" + (idx+1) + ".txt";
            fpname = outputPath + fname;
            Logger.println("+ Saved activities of frame " + (idx+1) + " in: `./" + fname);
            String activeTra = TrajectoryManager.getInstance().toFramewiseOutputString(idx);
            try {
                writeFile(activeTra, fpname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.println();
    }
}

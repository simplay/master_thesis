package writers;

import managers.TrajectoryManager;
import pipeline_components.Logger;
import java.io.File;
import java.io.IOException;

/**
 * FramewiseActiveTraWriter creates for every dataset frame a file
 * containing a list of trajectories (their label value and the location of their tracking point
 * active in the current frame) that are active in a certain frame.
 *
 * Active refers to the fact that a trajectory contains a tracking points traced in
 * the frame under consideration.
 *
 * Trajectory activity files are used to retrieve all trajectories active in a certain frame,
 * which is used to display the motion segmentation, since generally associate a segmentation label
 * to a trajectory label. Therefore, in order visualize the segmentation, we lookup for all active
 * trajectories (i.e. their labels) in a target frame and then fetch the
 * cluster labels (the segmentation colors) of these trajectories. This is all the information/data
 * we need in order to render the segmentation.
 *
 * For every frame, a trajectory activity text file is created, named `PREFIX_active_tra_f_ID.txt`
 * and located `./output/trajectory_label_frame/DATASET/`.
 *
 * The line of such an activity file looks like the following:
 *  /TRAJECTORY_LABEL(\s)TP_ROW_ID(\s)TP_COL_ID/
 *
 * Example: given the following active trajectory frame `foobar_active_tra_f_3.txt`
 *  15 2.34 4.5
 * this means, that in frame 3, the trajectory with label 15 has a tracking point
 * traced at the location (2.34, 4.5) (row,column) (exact tracking position).
 *
 */
public class FramewiseActiveTraWriter extends LargeFileWriter {

    /**
     * Store the frame-wise active trajectories files to a given location for a chosen dataset.
     *
     * @param dataset name of the dataset we are running.
     * @param till_index last index of frames in sequence we want to store.
     * @param fileStoreAtPath directory where we want to store the generated labels file.
     */
    public FramewiseActiveTraWriter(String dataset, int till_index, String fileStoreAtPath) {
        String outputPath = fileStoreAtPath + dataset + "/";
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

    /**
     * Store the frame-wise active trajectories files to `../output/similarities/` for a chosen dataset.
     *
     * @param dataset name of the dataset we are running
     * @param till_index last index of frames in sequence we want to store.
     */
    public FramewiseActiveTraWriter(String dataset, int till_index) {
        this(dataset, till_index, "../output/trajectory_label_frame/");
    }
}

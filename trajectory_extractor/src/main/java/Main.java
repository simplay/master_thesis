import java.io.*;
// TODO write more descriptive information:
// value range of input data, invalid values, what they do, where they are used
// what data is required / optional
// where it is computed and stored
/**
 * The following input data (frame-wise) is always read into memory:
 *  tracking candidate locations
 *  invalid tracking regions
 *  forward-and backward flow fields
 *  local and global flow variance values
 *  cie lab color values
 *
 *  optional input data:
 *      depth fields: not that the value zero indicates invalid pixel regions.
 *
 * Supported user args
 *
 *  required args:
 *      -d => dataset that should be used
 *      -task => similarity method that should be used
 *          1 => runs SumDistTask
 *  @example:
 *      -d c14 -task 1
 */
public class Main {
    public static void main(String[] argv) {

        String output_base_path = "../output/trajectories/";
        ArgParser.getInstance(argv);

        // Default runtime parameter setup
        String dataset = "c14";

        // TODO: determine this value automatically
        int samplingRate = 8;

        if (ArgParser.hasArgs()) {
            dataset = ArgParser.getDatasetName();
        }
        ArgParser.reportUsedArgs();
        System.out.println();

        /**
         * Read required input data
         */
        File folder = new File("../output/tracker_data/" + dataset);
        File[] fileList = folder.listFiles();
        int counter = 0;
        for (File file : fileList) {
            if (file.getName().matches("candidates_[\\d]+.txt")) {
                counter++;
            }
        }
        System.out.println("Tracking points over " + counter + " frames...");
        System.out.println();

        int till_index = counter;
        for (int idx = 1; idx <= till_index; idx++) {
            String fileNr = Integer.toString(idx);
            new CandidateFileReader(dataset, fileNr);
            new FlowFileReader(dataset, FlowField.FORWARD_FLOW, fileNr);
            new FlowFileReader(dataset, FlowField.BACKWARD_FLOW, fileNr);
            new InvalidRegionReader(dataset, fileNr);
            new FlowVarFileReader(dataset, fileNr);
            new GlobalVarFileReader(dataset);
            new ColorImageReader(dataset, fileNr);
        }

        System.out.println("Files loaded...");
        System.out.println();

        /**
         * Extract trajectories
         */
        System.out.println("Sampling every " + samplingRate + "th pixel");
        new Tracker(till_index, samplingRate);
        System.out.println();
        System.out.println("Number of extracted trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());
        for (int k = 0; k <= till_index+1; k++) {
            int trajectoryCount = TrajectoryManager.getInstance().allTrajectoryWithLength(k).size();
            System.out.println("#Trajectories with len=" + k + ": " + trajectoryCount);
        }
        System.out.println();

        /**
         * Filter extracted trajectories
         */

        // one pointed trajectories have a length of 0.
        System.out.println("Filtering 1-pointed trajectories...");

        TrajectoryManager.getInstance().filterOnePointedTrajectories();
        System.out.println("Filtered too short trajectories...");
        System.out.println("Number of remaining trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());

        // TODO compute similartites
        System.out.println("Starts computing affinity values between remaining trajectories...");
        new AffinityCalculator();


        // TODO apply post-filtering step: all zero-trajectories
        /**
         * Write output data
         */
        String output_filePathName = output_base_path + "traj_out_" + dataset+"_fc_" + till_index + ".txt";
        System.out.println("Writting trajectories to output file: " + output_filePathName);
        TrajectoryManager.getInstance().saveTrajectoriesToFile(output_filePathName);

        String outTLF = "../output/trajectory_label_frame/" + dataset + "/";
        System.out.println("Writting active trajectory frame files: " + outTLF);
        (new File(outTLF)).mkdirs();
        TrajectoryManager.getInstance().saveFramewiseTrajectoryDataToFile(outTLF, till_index);


        // TODO write similarity matrix, label mapping, nearest neighbors
    }
}

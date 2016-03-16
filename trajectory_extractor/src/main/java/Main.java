import datastructures.FlowField;
import managers.TrajectoryManager;
import readers.*;
import pipeline_components.AffinityCalculator;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import writers.SimilarityWriter;

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
 *
 *  optional input data:
 *      cie lab color values
 *      depth fields: not that the value zero indicates invalid pixel regions.
 *
 * Supported user args
 *
 *  required args:
 *      -d => dataset that should be used
 *      -task => similarity method that should be used
 *          1 => runs SumDistTask
 *      -color => should color cues be used for later computations?
 *          -color 1 => use color cues, i.e. load color files
 *          -color 0 => do not load color images
 *      -var => should the local variance be used
 *          -var 1 => use the local flow variance values for normalization
 *          -var 0 => use the global flow variance values.
 *  @example:
 *      -d c14 -task 1
 */
public class Main {
    public static void main(String[] argv) {
        long startTime = System.currentTimeMillis();
        String output_base_path = "../output/trajectories/";
        ArgParser.getInstance(argv);

        // Default runtime parameter setup
        String dataset = "c14";

        // TODO: determine this value automatically
        int samplingRate = 8;

        if (ArgParser.hasArgs()) {
            dataset = ArgParser.getDatasetName();
        }
        ArgParser.reportUsedParameters();
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

            // optionally loaded cue-data
            if (ArgParser.useColorCues()) new ColorImageReader(dataset, fileNr);
            // TODO load depth information
        }
        long tillFileLoadedTime = System.currentTimeMillis();
        System.out.println("Files loaded in "+((tillFileLoadedTime-startTime)/1000d) +"s...");
        System.out.println();

        /**
         * Extract trajectories
         */
        System.out.println("Sampling every " + samplingRate + "th pixel");
        new Tracker(till_index, samplingRate);

        long tillTrajectoriesTrackedTime = System.currentTimeMillis();
        System.out.println("Tracking took " + ((tillTrajectoriesTrackedTime-tillFileLoadedTime)/1000d)+ "s");

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
        long beforeAffCompTime = System.currentTimeMillis();
        new AffinityCalculator();
        long afterAffCompTime = System.currentTimeMillis();
        System.out.println("Computing similarity values took " + ((afterAffCompTime-beforeAffCompTime)/1000d)+ "s");

        // TODO apply post-filtering step: all zero-trajectories
        TrajectoryManager.getInstance().filterNoSimilarityTrajectories();

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
        // TrajectoryManager.sortTrajectories();
        try {
            new SimilarityWriter(dataset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long tillFinishedTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + ((tillFinishedTime-startTime)/1000d)+ "s");
    }
}
